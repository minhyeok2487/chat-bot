package sionic.chatbot.controller

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sionic.chatbot.dto.AnalyticsDto
import sionic.chatbot.dto.ApiResponse
import sionic.chatbot.service.AnalyticsService

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // 이 컨트롤러의 모든 API는 ADMIN 역할 필요
class AdminController(
    private val analyticsService: AnalyticsService
) {

    @GetMapping("/activity-log")
    fun getUserActivityLog(): ResponseEntity<ApiResponse<AnalyticsDto.UserActivityLogResponse>> {
        val activityLog = analyticsService.getUserActivityLog()
        return ResponseEntity.ok(ApiResponse.successWithData(activityLog, "사용자 활동 로그가 성공적으로 조회되었습니다."))
    }

    @GetMapping("/reports/chats/csv")
    fun generateChatReportCsv(response: HttpServletResponse) {
        try {
            analyticsService.generateChatReportCsv(response)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR
        }
    }
}