package sionic.chatbot.dto

import java.time.LocalDateTime

object AnalyticsDto {
    data class UserActivityLogResponse(
        val signupCount: Long,
        val loginCount: Long,
        val chatCreationCount: Long,
        val periodStart: LocalDateTime,
        val periodEnd: LocalDateTime
    )
}