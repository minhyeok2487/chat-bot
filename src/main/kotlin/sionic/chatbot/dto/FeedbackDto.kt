package sionic.chatbot.dto

import com.fasterxml.jackson.annotation.JsonInclude
import sionic.chatbot.entity.feedbacks.Feedback
import sionic.chatbot.entity.feedbacks.FeedbackStatus
import java.time.LocalDateTime

object FeedbackDto {

    data class CreateRequest(
        val chatId: Long,
        val isPositive: Boolean
    )

    data class StatusUpdateRequest(
        val status: FeedbackStatus
    )

    @JsonInclude(JsonInclude.Include.NON_NULL)
    data class Response(
        val feedbackId: Long,
        val userId: Long,
        val userName: String?, // 피드백 남긴 사용자 이름
        val userEmail: String?, // 피드백 남긴 사용자 이메일
        val chatId: Long,
        val chatQuestion: String?, // 피드백 대상 채팅의 질문 (일부)
        val isPositive: Boolean,
        val status: FeedbackStatus,
        val createdAt: LocalDateTime
    ) {
        companion object {
            fun from(feedback: Feedback, includeSensitiveUserData: Boolean = true): Response {
                // Chat -> Threads -> User 경로로 chat의 소유자 정보도 가져올 수 있으나, 여기서는 피드백 작성자만 명시
                val questionPreview = feedback.chat.question.take(50) + if (feedback.chat.question.length > 50) "..." else ""

                return Response(
                    feedbackId = feedback.id,
                    userId = feedback.user.id,
                    userName = if (includeSensitiveUserData) feedback.user.name else null,
                    userEmail = if (includeSensitiveUserData) feedback.user.email else null,
                    chatId = feedback.chat.id,
                    chatQuestion = questionPreview,
                    isPositive = feedback.isPositive,
                    status = feedback.status,
                    createdAt = feedback.createdAt
                )
            }
        }
    }
}