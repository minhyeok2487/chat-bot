package sionic.chatbot.service

import org.springframework.security.core.context.SecurityContextHolder // 현재 사용자 정보 가져오기
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sionic.chatbot.dto.ChatDto
import sionic.chatbot.entity.chats.Chat
import sionic.chatbot.entity.chats.ChatRepository
import sionic.chatbot.entity.chats.ThreadsRepository
import sionic.chatbot.entity.chats.Threads
import sionic.chatbot.entity.users.User
import sionic.chatbot.entity.users.UserRepository
import java.time.LocalDateTime

@Service
class ChatService(
    private val userRepository: UserRepository,
    private val threadsRepository: ThreadsRepository,
    private val chatRepository: ChatRepository,
) {

    // 현재 인증된 사용자 정보를 가져오기
    private fun getCurrentAuthenticatedUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name
        return userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")
    }

    @Transactional
    fun createChat(request: ChatDto.CreateRequest): ChatDto.MessageResponse {
        val currentUser = getCurrentAuthenticatedUser()

        // 1. 스레드 결정 또는 생성
        val latestUserChat: Chat? = chatRepository.findFirstByThreadsUserOrderByCreatedAtDesc(currentUser)
        val activeThreads: Threads = if (latestUserChat != null &&
            latestUserChat.createdAt.plusMinutes(30).isAfter(LocalDateTime.now())
        ) {
            latestUserChat.threads // 30분 이내면 기존 스레드 사용
        } else {
            threadsRepository.save(Threads(user = currentUser)) // 새 스레드 생성
        }

        // 2. OpenAI 요청을 위한 이전 대화 내역 준비
        val chatHistoryInDb: List<Chat> = chatRepository.findByThreadsOrderByCreatedAtAsc(activeThreads)

        // TODO: 대화 했다고 가정

        if (request.isStreaming) {
            // TODO: 스트리밍 응답 처리 로직 (추후 구현)
        }

        // 3. 새 대화 저장
        val newChat = Chat(
            threads = activeThreads,
            question = request.question,
            answer = "대답"
        )

        val savedChat = chatRepository.save(newChat)

        // 4. 응답 DTO 변환
        return ChatDto.MessageResponse(
            chatId = savedChat.id,
            threadId = activeThreads.id,
            question = savedChat.question,
            answer = savedChat.answer,
            modelUsed = savedChat.modelUsed,
            createdAt = savedChat.createdAt
        )
    }
}