package sionic.chatbot.entity.chats

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sionic.chatbot.entity.users.User

@Repository
interface ThreadRepository : JpaRepository<Thread, Long> {
    // 특정 사용자의 스레드 중 가장 최근에 '생성된' 스레드를 찾는 메소드
    fun findFirstByUserOrderByCreatedAtDesc(user: User): Thread?
}