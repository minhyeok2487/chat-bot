package sionic.chatbot.dto

import sionic.chatbot.entity.users.UserRole


object UserDto {

    data class SignUpRequest(
        val email: String,
        val password: String,
        val name: String,
        val role: UserRole = UserRole.MEMBER // 기본값을 MEMBER로 설정
    )

    data class LoginRequest(
        val email: String,
        val password: String
    )

    data class TokenResponse(
        val accessToken: String,
        val tokenType: String = "Bearer"
    )
}