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

    data class ChatInfo(
        val chatId: Long,
        val question: String,
        val answer: String,
        val modelUsed: String?,
        val chatCreatedAt: LocalDateTime
    )

    data class ThreadWithChatsResponse(
        val threadId: Long,
        val userId: Long,       // 스레드 소유자 ID
        val userEmail: String,  // 스레드 소유자 이메일
        val threadCreatedAt: LocalDateTime,
        val chats: List<ChatInfo> // 해당 스레드에 속한 메시지들
    )
}