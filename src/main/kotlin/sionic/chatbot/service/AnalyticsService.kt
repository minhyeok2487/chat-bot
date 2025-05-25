package sionic.chatbot.service

import com.opencsv.CSVWriter
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sionic.chatbot.dto.AnalyticsDto
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import sionic.chatbot.entity.chats.ChatRepository
import sionic.chatbot.entity.users.LoginEventRepository
import sionic.chatbot.entity.users.UserRepository

@Service
class AnalyticsService(
    private val userRepository: UserRepository,
    private val loginEventRepository: LoginEventRepository,
    private val chatRepository: ChatRepository
) {

    @Transactional(readOnly = true)
    fun getUserActivityLog(): AnalyticsDto.UserActivityLogResponse {
        val endTime = LocalDateTime.now()
        val startTime = endTime.minusDays(1) // 요청 시점으로부터 하루(24시간) 동안

        val signupCount = userRepository.countByCreatedAtBetween(startTime, endTime)
        val loginCount = loginEventRepository.countByCreatedAtBetween(startTime, endTime)
        val chatCreationCount = chatRepository.countByCreatedAtBetween(startTime, endTime)

        return AnalyticsDto.UserActivityLogResponse(
            signupCount = signupCount,
            loginCount = loginCount,
            chatCreationCount = chatCreationCount,
            periodStart = startTime,
            periodEnd = endTime
        )
    }

    @Transactional(readOnly = true)
    fun generateChatReportCsv(response: HttpServletResponse) {
        val endTime = LocalDateTime.now()
        val startTime = endTime.minusDays(1)

        val chatsForReport = chatRepository.findForReportByCreatedAtBetween(startTime, endTime)

        response.contentType = "text/csv; charset=UTF-8"
        response.setHeader("Content-Disposition", "attachment; filename=\"chat_report_${startTime.toLocalDate()}_${endTime.toLocalDate()}.csv\"")
        response.characterEncoding = StandardCharsets.UTF_8.name()


        val writer = OutputStreamWriter(response.outputStream, StandardCharsets.UTF_8)
        writer.write("\uFEFF")

        val csvWriter = CSVWriter(writer,
            CSVWriter.DEFAULT_SEPARATOR,
            CSVWriter.NO_QUOTE_CHARACTER,
            CSVWriter.DEFAULT_ESCAPE_CHARACTER,
            CSVWriter.DEFAULT_LINE_END)

        // CSV 헤더 작성
        csvWriter.writeNext(arrayOf("Chat ID", "Thread ID", "User ID", "User Email", "User Name", "Question", "Answer", "Chat CreatedAt"))

        // CSV 데이터 작성
        chatsForReport.forEach { chat ->
            csvWriter.writeNext(arrayOf(
                chat.id.toString(),
                chat.threads.id.toString(),
                chat.threads.user.id.toString(),
                chat.threads.user.email,
                chat.threads.user.name,
                chat.question,
                chat.answer,
                chat.createdAt.toString()
            ))
        }
        csvWriter.close()
        writer.close()
    }
}