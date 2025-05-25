package sionic.chatbot.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sionic.chatbot.dto.UserDto
import sionic.chatbot.entity.users.User
import sionic.chatbot.entity.users.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}