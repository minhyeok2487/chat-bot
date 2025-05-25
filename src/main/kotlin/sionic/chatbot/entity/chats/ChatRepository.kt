package sionic.chatbot.entity.chats

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sionic.chatbot.entity.users.User

@Repository
interface ChatRepository : JpaRepository<Chat, Long> {
    // 특정 사용자의 모든 대화 중 가장 최근 대화를 가져오는 메소드
    fun findFirstByThreadsUserOrderByCreatedAtDesc(user: User): Chat?

    fun findByThreadsOrderByCreatedAtAsc(threads: Threads): List<Chat>
}