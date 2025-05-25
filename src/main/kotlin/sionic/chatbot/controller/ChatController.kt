package sionic.chatbot.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import sionic.chatbot.dto.ApiResponse
import sionic.chatbot.dto.ChatDto
import sionic.chatbot.service.ChatService

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping
    fun createChat(
        @RequestBody createRequest: ChatDto.CreateRequest
    ): ResponseEntity<ApiResponse<ChatDto.MessageResponse>> {
        val messageResponse = chatService.createChat(createRequest)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.successWithData(messageResponse, "대화가 성공적으로 생성되었습니다."))
    }

    @GetMapping
    fun searchChats(
        @PageableDefault(
            size = 10,                // 기본 페이지 크기
            page = 0,                 // 기본 페이지 번호 (0부터 시작)
            sort = ["createdAt"],     // 기본 정렬 필드 (Chat 엔티티의 필드명)
            direction = Sort.Direction.DESC // 기본 정렬 방향
        ) pageable: Pageable
    ): ResponseEntity<ApiResponse<Page<ChatDto.ThreadWithChatsResponse>>> {
        val threadsPage = chatService.searchChat(pageable)
        return ResponseEntity.ok(ApiResponse.successWithData(threadsPage, "대화 목록이 성공적으로 조회되었습니다."))
    }

    @DeleteMapping("/threads/{threadId}")
    fun deleteThread(
        @PathVariable threadId: Long
    ): ResponseEntity<ApiResponse<Unit>> {
        chatService.deleteThread(threadId)
        return ResponseEntity.ok(ApiResponse.success("스레드가 성공적으로 삭제되었습니다."))
    }
}