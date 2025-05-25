package sionic.chatbot.entity.users

import jakarta.persistence.*
import sionic.chatbot.entity.BaseTimeEntity

@Entity
@Table(name = "login_events")
class LoginEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,
) : BaseTimeEntity()