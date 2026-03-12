package com.chessopeningstats.backend.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // Null인 필드는 JSON에 포함하지 않음
public class ApiResponseDto<T> {

    private final String status;
    private final String message;
    private final T data;

    private ApiResponseDto(String status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponseDto<T> success(T data) {
        return new ApiResponseDto<>("SUCCESS", "요청에 성공했습니다.", data);
    }

    public static <T> ApiResponseDto<T> success() {
        return new ApiResponseDto<>("SUCCESS", "요청에 성공했습니다.", null);
    }
    
    public static ApiResponseDto<?> error(String message) {
        return new ApiResponseDto<>("ERROR", message, null);
    }
}
