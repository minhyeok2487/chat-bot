package sionic.chatbot.entity.chats

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import sionic.chatbot.entity.users.User
import java.time.LocalDateTime

@Repository
interface ChatRepository : JpaRepository<Chat, Long> {
    // 특정 사용자의 모든 대화 중 가장 최근 대화를 가져오는 메소드
    fun findFirstByThreadsUserOrderByCreatedAtDesc(user: User): Chat?

    fun findByThreadsOrderByCreatedAtAsc(threads: Threads): List<Chat>

    @Query(
        value = "SELECT c FROM Chat c JOIN FETCH c.threads th JOIN FETCH th.user u WHERE u = :user",
        countQuery = "SELECT count(c) FROM Chat c WHERE c.threads.user = :user"
    )
    fun findAllChatsDetailsByUser(
        @Param("user") user: User,
        pageable: Pageable // 이 Pageable 객체의 Sort 정보 사용
    ): Page<Chat>

    @Query(
        value = "SELECT c FROM Chat c JOIN FETCH c.threads th JOIN FETCH th.user u",
        countQuery = "SELECT count(c) FROM Chat c"
    )
    fun findAllChatsDetailsAllUser(pageable: Pageable): Page<Chat> // 이 Pageable 객체의 Sort 정보가 사용

    fun countByCreatedAtBetween(startTime: LocalDateTime, endTime: LocalDateTime): Long // 특정 기간 대화 생성 수

    // CSV 보고서를 위한 데이터 조회 (Chat, 연관된 Threads, Threads의 User 정보 포함)
    @Query("SELECT c FROM Chat c JOIN FETCH c.threads th JOIN FETCH th.user WHERE c.createdAt BETWEEN :startTime AND :endTime ORDER BY c.createdAt ASC")
    fun findForReportByCreatedAtBetween(
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<Chat>
}