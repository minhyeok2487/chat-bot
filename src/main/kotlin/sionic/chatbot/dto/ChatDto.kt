package sionic.chatbot.dto

import java.time.LocalDateTime

object ChatDto { // object로 감싸서 네임스페이스처럼 활용

    data class CreateRequest(
        val question: String,

        val isStreaming: Boolean = false, // 기본값 false

        val model: String? = null // 사용할 AI 모델 (선택 사항)
    )

    data class MessageResponse(
        val chatId: Long,
        val threadId: Long,
        val question: String,
        val answer: String,
        val modelUsed: String?,
        val createdAt: LocalDateTime
    )
}