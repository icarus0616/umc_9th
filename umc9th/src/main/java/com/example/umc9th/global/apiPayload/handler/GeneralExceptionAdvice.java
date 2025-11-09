package com.example.umc9th.global.apiPayload.handler;

import com.example.umc9th.global.apiPayload.ApiResponse;
import com.example.umc9th.global.apiPayload.code.BaseErrorCode;
import com.example.umc9th.global.apiPayload.code.GeneralErrorCode;
import com.example.umc9th.global.apiPayload.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.umc9th.global.notifier.WebhookNotifier;

import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class GeneralExceptionAdvice {

    private final  WebhookNotifier webhookNotifier;



    // 애플리케이션에서 발생하는 커스텀 예외를 처리
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            GeneralException ex
    ) {

        return ResponseEntity.status(ex.getCode().getStatus())
                .body(ApiResponse.onFailure(
                                ex.getCode(),
                                null
                        )
                );
    }


    /** ✅ 정의되지 않은 모든 예외 처리 (500 Internal Server Error) */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(
            Exception ex, HttpServletRequest request
    ) {
        String requestUri = request.getRequestURI();

        // ✅ favicon.ico 요청은 무시
        if ("/favicon.ico".equals(requestUri)) {
            return ResponseEntity.ok().build();
        }

        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;

        // ⚡ Discord/Slack 알림 전송 로직
        String errorMessage = String.format("""
            🚨 **500 Internal Server Error 발생**
            **시간:** %s
            **예외 타입:** %s
            **메시지:** %s
            """,
                LocalDateTime.now(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );

        webhookNotifier.sendDiscordMessage(errorMessage);

        // ⚙️ 클라이언트 응답 반환
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(
                        code,
                        ex.getMessage()
                ));
    }

    /*
    사용자에게만 보여주는 에러
    // 그 외의 정의되지 않은 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleException(
            Exception ex
    ) {

        BaseErrorCode code = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.onFailure(
                                code,
                                ex.getMessage()
                        )
                );
    }
    */
}
