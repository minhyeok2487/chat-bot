package sionic.chatbot.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    val message: String,
    val data: T? = null
) {
    companion object {
        fun <E> success(message: String): ApiResponse<E> {
            return ApiResponse(message = message, data = null)
        }

        fun <T> successWithData(data: T, message: String = "요청이 성공적으로 처리되었습니다."): ApiResponse<T> {
            return ApiResponse(message = message, data = data)
        }
    }
}
