package sionic.chatbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class ChatBotApplication

fun main(args: Array<String>) {
	runApplication<ChatBotApplication>(*args)
}
