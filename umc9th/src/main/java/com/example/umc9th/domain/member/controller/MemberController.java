package com.example.umc9th.domain.member.controller;

import com.example.umc9th.domain.member.dto.MemberResponseDto;
import com.example.umc9th.domain.member.dto.MemberUpdateRequestDto;
import com.example.umc9th.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "마이페이지 조회", description = "로그인한 사용자의 정보를 조회합니다.")
    @GetMapping("/me")
    public MemberResponseDto getMyPage() {
        Long loginUserId = 1L; // ⛔ 예시 → 실제로는 Security에서 꺼내야함
        return memberService.getMember(loginUserId);
    }

    @Operation(summary = "회원 조회", description = "회원ID를 기준으로 사용자를 조회합니다.")
    @GetMapping("/{userId}")
    public MemberResponseDto getMember(@PathVariable Long userId) {
        return memberService.getMember(userId);
    }

    @Operation(summary = "회원 정보 수정", description = "회원 정보를 변경합니다.")
    @PutMapping("/{userId}")
    public MemberResponseDto updateMember(
            @PathVariable Long userId,
            @RequestBody MemberUpdateRequestDto dto) {
        return memberService.updateMember(userId, dto);
    }

}
