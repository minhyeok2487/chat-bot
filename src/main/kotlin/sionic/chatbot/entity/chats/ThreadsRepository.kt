package sionic.chatbot.entity.chats


import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ThreadsRepository : JpaRepository<Threads, Long> {}
