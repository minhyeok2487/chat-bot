package sionic.chatbot.entity.chats

import jakarta.persistence.*
import sionic.chatbot.entity.BaseTimeEntity

@Entity
@Table(name = "chats")
class Chat(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thread_id", nullable = false)
    var thread: Thread, // 이 대화가 속한 스레드

    @Column(nullable = false, columnDefinition = "TEXT")
    var question: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var answer: String,

) : BaseTimeEntity()