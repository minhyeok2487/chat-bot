package sionic.chatbot.service

import jakarta.persistence.EntityNotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sionic.chatbot.dto.FeedbackDto
import sionic.chatbot.entity.chats.ChatRepository
import sionic.chatbot.entity.feedbacks.Feedback
import sionic.chatbot.entity.feedbacks.FeedbackRepository
import sionic.chatbot.entity.feedbacks.FeedbackStatus
import sionic.chatbot.entity.users.User
import sionic.chatbot.entity.users.UserRepository
import sionic.chatbot.entity.users.UserRole

@Service
class FeedbackService(
    private val feedbackRepository: FeedbackRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) {

    // 현재 인증된 사용자 정보를 가져오기
    private fun getCurrentAuthenticatedUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        val email = authentication.name
        return userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User not found with email: $email")
    }

    @Transactional
    fun createFeedback(request: FeedbackDto.CreateRequest): FeedbackDto.Response {
        val currentUser = getCurrentAuthenticatedUser()
        val chat = chatRepository.findById(request.chatId)
            .orElseThrow { EntityNotFoundException("Chat not found with ID: ${request.chatId}") }

        // 권한 검사: 멤버는 자신이 생성한 대화에만 피드백 가능
        if (currentUser.role == UserRole.MEMBER && chat.threads.user.id != currentUser.id) {
            throw IllegalArgumentException("멤버는 자신이 생성한 대화에만 피드백이 가능합니다.")
        }

        // 중복 피드백 검사
        if (feedbackRepository.existsByUserAndChat(currentUser, chat)) {
            throw IllegalArgumentException("이미 존재하는 피드백 입니다.") // ErrorCode에 정의 필요
        }

        val feedback = Feedback(
            user = currentUser,
            chat = chat,
            isPositive = request.isPositive,
            status = FeedbackStatus.PENDING
        )

        val savedFeedback = feedbackRepository.save(feedback)
        return FeedbackDto.Response.from(savedFeedback)
    }

    @Transactional(readOnly = true)
    fun listFeedback(pageable: Pageable, isPositiveFilter: Boolean?): Page<FeedbackDto.Response> {
        val currentUser = getCurrentAuthenticatedUser()
        val feedbackPage: Page<Feedback>

        if (currentUser.role == UserRole.ADMIN) {
            feedbackPage = if (isPositiveFilter != null) {
                feedbackRepository.findByIsPositiveWithDetails(isPositiveFilter, pageable)
            } else {
                feedbackRepository.findAll(pageable)
            }
        } else { // MEMBER
            feedbackPage = if (isPositiveFilter != null) {
                feedbackRepository.findByUserAndIsPositiveWithDetails(currentUser, isPositiveFilter, pageable)
            } else {
                feedbackRepository.findByUserWithDetails(currentUser, pageable)
            }
        }

        return feedbackPage.map { feedback -> FeedbackDto.Response.from(feedback, currentUser.role == UserRole.ADMIN) }
    }

    @Transactional
    fun updateFeedbackStatus(feedbackId: Long, newStatus: FeedbackStatus): FeedbackDto.Response {
        val currentUser = getCurrentAuthenticatedUser()

        if (currentUser.role != UserRole.ADMIN) {
            throw IllegalArgumentException("관리자만 피드백 상태를 변경할 수 있습니다.")
        }

        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { EntityNotFoundException("Feedback not found with ID: $feedbackId") }

        feedback.status = newStatus
        return FeedbackDto.Response.from(feedback)
    }
}