package com.example.umc9th.domain.kakao.controller;

import com.example.umc9th.domain.kakao.service.KakaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class HomeController {

    private final KakaoService kakaoService;

    @GetMapping("/")
    public String login(Model model) {
        // 카카오 로그인 URL을 모델에 담아서 뷰로 전달
        model.addAttribute("kakaoUrl", kakaoService.getKakaoLoginUrl());
        return "index"; // templates/index.html (Thymeleaf 기준)
    }
}
