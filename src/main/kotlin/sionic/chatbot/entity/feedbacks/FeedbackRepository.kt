package sionic.chatbot.entity.feedbacks

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sionic.chatbot.entity.chats.Chat
import sionic.chatbot.entity.users.User

@Repository
interface FeedbackRepository : JpaRepository<Feedback, Long> {

    // 특정 사용자가 특정 채팅에 대해 피드백을 이미 남겼는지 확인
    fun existsByUserAndChat(user: User, chat: Chat): Boolean

    // 사용자별 피드백 목록 조회 (페이지네이션, 정렬 지원)
    @Query(value = "SELECT f FROM Feedback f JOIN FETCH f.user JOIN FETCH f.chat c JOIN FETCH c.threads th JOIN FETCH th.user WHERE f.user = :user",
        countQuery = "SELECT count(f) FROM Feedback f WHERE f.user = :user")
    fun findByUserWithDetails(user: User, pageable: Pageable): Page<Feedback>

    // 사용자별 + 긍정/부정 필터링된 피드백 목록 조회
    @Query(value = "SELECT f FROM Feedback f JOIN FETCH f.user JOIN FETCH f.chat c JOIN FETCH c.threads th JOIN FETCH th.user WHERE f.user = :user AND f.isPositive = :isPositive",
        countQuery = "SELECT count(f) FROM Feedback f WHERE f.user = :user AND f.isPositive = :isPositive")
    fun findByUserAndIsPositiveWithDetails(user: User, isPositive: Boolean, pageable: Pageable): Page<Feedback>

    // 모든 피드백 목록 조회 (관리자용, 페이지네이션, 정렬 지원)
    @Query(value = "SELECT f FROM Feedback f JOIN FETCH f.user JOIN FETCH f.chat c JOIN FETCH c.threads th JOIN FETCH th.user",
        countQuery = "SELECT count(f) FROM Feedback f")
    override fun findAll(pageable: Pageable): Page<Feedback> // JpaRepository의 findAll 오버라이드

    // 긍정/부정 필터링된 모든 피드백 목록 조회 (관리자용)
    @Query(value = "SELECT f FROM Feedback f JOIN FETCH f.user JOIN FETCH f.chat c JOIN FETCH c.threads th JOIN FETCH th.user WHERE f.isPositive = :isPositive",
        countQuery = "SELECT count(f) FROM Feedback f WHERE f.isPositive = :isPositive")
    fun findByIsPositiveWithDetails(isPositive: Boolean, pageable: Pageable): Page<Feedback>
}