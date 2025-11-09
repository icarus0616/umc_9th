package com.example.umc9th.domain.test.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TestSuccessCode {
    // For test
    OK(HttpStatus.OK, "COMMON200", "요청이 성공적으로 처리되었습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
