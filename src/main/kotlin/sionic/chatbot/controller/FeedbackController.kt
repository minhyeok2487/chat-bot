package sionic.chatbot.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sionic.chatbot.dto.ApiResponse
import sionic.chatbot.dto.FeedbackDto
import sionic.chatbot.service.FeedbackService

@RestController
@RequestMapping("/api/feedbacks")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @PostMapping
    fun createFeedback(@RequestBody createRequest: FeedbackDto.CreateRequest): ResponseEntity<ApiResponse<FeedbackDto.Response>> {
        // TODO: createRequest 유효성 검사
        val feedbackResponse = feedbackService.createFeedback(createRequest)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.successWithData(feedbackResponse, "피드백이 성공적으로 제출되었습니다."))
    }

    @GetMapping
    fun getFeedbacks(
        @PageableDefault(size = 10, sort = ["createdAt"], direction = org.springframework.data.domain.Sort.Direction.DESC) pageable: Pageable,
        @RequestParam(required = false) isPositive: Boolean? // 긍정/부정 필터
    ): ResponseEntity<ApiResponse<Page<FeedbackDto.Response>>> {
        val feedbackPage = feedbackService.listFeedback(pageable, isPositive)
        return ResponseEntity.ok(ApiResponse.successWithData(feedbackPage, "피드백 목록이 성공적으로 조회되었습니다."))
    }

    @PatchMapping("/{feedbackId}/status")
    fun updateFeedbackStatus(
        @PathVariable feedbackId: Long,
        @RequestBody statusUpdateRequest: FeedbackDto.StatusUpdateRequest
    ): ResponseEntity<ApiResponse<FeedbackDto.Response>> {
        // TODO: statusUpdateRequest 유효성 검사
        val updatedFeedback = feedbackService.updateFeedbackStatus(feedbackId, statusUpdateRequest.status)
        return ResponseEntity.ok(ApiResponse.successWithData(updatedFeedback, "피드백 상태가 성공적으로 업데이트되었습니다."))
    }
}