package sionic.chatbot.entity.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User? // 이메일로 사용자 조회
    fun existsByEmail(email: String): Boolean // 이메일 존재 여부 확인
    fun countByCreatedAtBetween(startTime: LocalDateTime, endTime: LocalDateTime): Long // 특정 기간 가입자 수
}