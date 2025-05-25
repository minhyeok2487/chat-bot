package sionic.chatbot.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import sionic.chatbot.dto.ApiResponse
import sionic.chatbot.dto.ChatDto
import sionic.chatbot.service.ChatService

@RestController
@RequestMapping("/api/chats")
class ChatController(
    private val chatService: ChatService
) {

    @PostMapping
    fun createChatMessage(
        @RequestBody createRequest: ChatDto.CreateRequest
    ): ResponseEntity<ApiResponse<ChatDto.MessageResponse>> {
        val messageResponse = chatService.createChat(createRequest)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.successWithData(messageResponse, "대화가 성공적으로 생성되었습니다."))
    }
}