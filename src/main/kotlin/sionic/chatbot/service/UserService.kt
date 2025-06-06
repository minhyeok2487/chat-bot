package sionic.chatbot.service

import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sionic.chatbot.dto.UserDto
import sionic.chatbot.entity.users.User
import sionic.chatbot.entity.users.UserRepository
import sionic.chatbot.global.security.JwtTokenProvider
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import sionic.chatbot.entity.users.LoginEvent
import sionic.chatbot.entity.users.LoginEventRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtTokenProvider: JwtTokenProvider,
    private val loginEventRepository: LoginEventRepository
) {

    @Transactional
    fun signUp(signUpRequest: UserDto.SignUpRequest) {

        // 이메일 중복 확인
        if (userRepository.existsByEmail(signUpRequest.email)) {
            throw IllegalArgumentException("이미 가입된 이메일 입니다.")
        }

        // 비밀번호 암호화
        val encodedPassword = passwordEncoder.encode(signUpRequest.password)

        // User 엔티티 생성 및 저장
        val user = User(
            email = signUpRequest.email,
            password = encodedPassword,
            name = signUpRequest.name,
            role = signUpRequest.role
        )

        userRepository.save(user)
    }

    fun login(loginRequest: UserDto.LoginRequest): UserDto.TokenResponse {
        try {
            // Spring Security의 AuthenticationManager를 사용하여 인증 수행
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    loginRequest.email,
                    loginRequest.password
                )
            )

            // 로그인 성공한 사용자 정보 가져오기 (LoginEvent 기록에 필요)
            val user = userRepository.findByEmail(loginRequest.email)
                ?: throw UsernameNotFoundException("User not found for login event: ${loginRequest.email}")

            // 로그인 이벤트 기록
            loginEventRepository.save(LoginEvent(user = user))


            // 인증 성공 시 JWT 생성
            val accessToken = jwtTokenProvider.generateAccessToken(authentication)
            return UserDto.TokenResponse(accessToken = accessToken)

        } catch (e: AuthenticationException) {
            throw IllegalArgumentException("로그인 실패")
        }
    }
}