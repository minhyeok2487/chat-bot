package sionic.chatbot.entity.users

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface LoginEventRepository : JpaRepository<LoginEvent, Long> {
    // 특정 기간 동안의 로그인 이벤트 수 계산
    fun countByCreatedAtBetween(startTime: LocalDateTime, endTime: LocalDateTime): Long
}