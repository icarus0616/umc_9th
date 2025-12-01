package com.example.umc9th.domain.kakao.controller;

import com.example.umc9th.domain.kakao.dto.KakaoDto;
import com.example.umc9th.domain.kakao.service.KakaoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KakaoController {

    private final KakaoService kakaoService;

    @GetMapping("/login/kakao/callback")
    public ResponseEntity<KakaoDto> callback(HttpServletRequest request) throws Exception {
        String code = request.getParameter("code");
        KakaoDto kakaoInfo = kakaoService.login(code);

        HttpSession session = request.getSession(true);
        session.setAttribute("loginMemberId", kakaoInfo.getMemberId());

        return ResponseEntity.ok(kakaoInfo);
    }

}
