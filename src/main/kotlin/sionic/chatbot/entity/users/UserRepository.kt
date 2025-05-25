package sionic.chatbot.entity.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User? // 이메일로 사용자 조회
    fun existsByEmail(email: String): Boolean // 이메일 존재 여부 확인
}