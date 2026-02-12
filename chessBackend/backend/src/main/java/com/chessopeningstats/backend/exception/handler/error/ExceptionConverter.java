package com.chessopeningstats.backend.exception.handler.error;

import com.chessopeningstats.backend.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExceptionConverter {

    public ErrorResponse convert(Throwable t) {
        if (t instanceof BusinessException) {
            log.debug("BusinessException caught: {}", t.getMessage());
            return ErrorResponse.of(((BusinessException) t).getErrorCode());
        } else {
            // BusinessException이 아닌 모든 예외(RuntimeException, Checked Exception)와
            // Error(OutOfMemoryError 등)는 모두 예측하지 못한 내부 서버 오류로 처리합니다.
            log.error("Unhandled throwable caught", t);
            return ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
