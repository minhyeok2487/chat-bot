package sionic.chatbot.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sionic.chatbot.dto.ApiResponse
import sionic.chatbot.dto.UserDto
import sionic.chatbot.service.UserService

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody signUpRequest: UserDto.SignUpRequest): ResponseEntity<ApiResponse<Unit>> {
        userService.signUp(signUpRequest)
        val response = ApiResponse.success<Unit>("회원가입이 성공적으로 완료되었습니다.")
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: UserDto.LoginRequest): ResponseEntity<ApiResponse<UserDto.TokenResponse>> {
        val tokenResponse = userService.login(loginRequest)
        val response = ApiResponse.successWithData(tokenResponse, "로그인 성공")
        return ResponseEntity.ok(response)
    }
}