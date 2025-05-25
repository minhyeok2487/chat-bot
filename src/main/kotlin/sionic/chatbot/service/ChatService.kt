package sionic.chatbot.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
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
import sionic.chatbot.entity.users.UserRole
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

    @Transactional(readOnly = true)
    fun searchChat(pageable: Pageable): Page<ChatDto.ThreadWithChatsResponse> {
        val currentUser = getCurrentAuthenticatedUser()

        // 1. 페이지네이션된 Chat 목록 조회
        val pagedChats: Page<Chat> = if (currentUser.role == UserRole.ADMIN) {
            chatRepository.findAllChatsDetailsAllUser(pageable)
        } else {
            chatRepository.findAllChatsDetailsByUser(currentUser, pageable)
        }

        val chatsOnPage: List<Chat> = pagedChats.content
        if (chatsOnPage.isEmpty()) {
            return Page.empty(pageable)
        }

        // 2. 조회된 Chat들을 Thread 기준으로 그룹화 (순서 유지를 위해 LinkedHashMap 사용)
        val groupedByThreadEntity: Map<Threads, List<Chat>> = chatsOnPage.groupBy { it.threads }

        // 3. DTO로 변환 (스레드 순서 유지를 위해 pagedChats에서 distinct threads를 순서대로 추출)
        val distinctThreadsInOrder = chatsOnPage.map { it.threads }.distinct()

        val responseList: List<ChatDto.ThreadWithChatsResponse> = distinctThreadsInOrder.map { thread ->
            val chatsForThisThread = groupedByThreadEntity[thread] ?: emptyList()
            val chatInfos = chatsForThisThread.map { chat ->
                ChatDto.ChatInfo(
                    chatId = chat.id,
                    question = chat.question,
                    answer = chat.answer,
                    modelUsed = chat.modelUsed,
                    chatCreatedAt = chat.createdAt
                )
            }
            ChatDto.ThreadWithChatsResponse(
                threadId = thread.id,
                userId = thread.user.id,
                userEmail = thread.user.name,
                threadCreatedAt = thread.createdAt,
                chats = chatInfos
            )
        }

        return PageImpl(responseList, pageable, pagedChats.totalElements)
    }

    @Transactional
    fun deleteThread(threadId: Long) {
        val currentUser = getCurrentAuthenticatedUser()

        // 1. 스레드 조회
        val threadToDelete = threadsRepository.findById(threadId)
            .orElseThrow { EntityNotFoundException("요청하신 ID의 스레드를 찾을 수 없습니다: $threadId") }

        // 2. 권한 확인: 현재 사용자가 스레드 소유자인지 확인
        if (threadToDelete.user.id != currentUser.id) {
            throw IllegalArgumentException("이 스레드를 삭제할 권한이 없습니다.")
        }

        // 3. 스레드 삭제
        threadsRepository.delete(threadToDelete)
    }
}