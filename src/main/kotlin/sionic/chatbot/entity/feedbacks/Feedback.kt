package sionic.chatbot.entity.feedbacks

import jakarta.persistence.*
import sionic.chatbot.entity.BaseTimeEntity
import sionic.chatbot.entity.chats.Chat
import sionic.chatbot.entity.users.User

@Entity
@Table(name = "feedbacks")
class Feedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id", nullable = false)
    val chat: Chat,

    @Column(nullable = false)
    var isPositive: Boolean,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: FeedbackStatus

) : BaseTimeEntity()