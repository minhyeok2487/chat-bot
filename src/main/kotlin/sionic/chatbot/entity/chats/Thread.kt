package sionic.chatbot.entity.chats

import jakarta.persistence.*
import sionic.chatbot.entity.BaseTimeEntity
import sionic.chatbot.entity.users.User

@Entity
@Table(name = "chat_threads")
class Thread(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

) : BaseTimeEntity()