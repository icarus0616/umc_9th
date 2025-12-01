package com.example.umc9th.domain.member.service;

import com.example.umc9th.domain.member.dto.MemberResponseDto;
import com.example.umc9th.domain.member.dto.MemberUpdateRequestDto;
import com.example.umc9th.domain.member.entity.Member;
import com.example.umc9th.domain.member.enums.Gender;
import com.example.umc9th.domain.member.repository.MemberRepository;
import com.example.umc9th.global.auth.enums.SocialType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // ✅ 마이페이지 조회
    public MemberResponseDto getMember(Long userId) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        return MemberResponseDto.builder()
                .userId(member.getUserId())
                .name(member.getName())
                .gender(member.getGender())
                .age(member.getAge())
                .address(member.getAddress())
                .detailAddress(member.getDetailAddress())
                .memberStatus(member.getMemberStatus())
                .phoneNumber(member.getPhoneNumber())
                .socialType(member.getSocialType())
                .point(member.getPoint())
                .build();
    }

    // ✅ 회원 정보 수정 (지금 방식 유지)
    public MemberResponseDto updateMember(Long userId, MemberUpdateRequestDto dto) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        member = Member.builder()
                .userId(member.getUserId())
                .name(dto.getName())
                .gender(dto.getGender())
                .age(dto.getAge())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .memberStatus(member.getMemberStatus())
                .phoneNumber(dto.getPhoneNumber())
                .socialType(member.getSocialType())
                .socialId(member.getSocialId())
                .point(member.getPoint())
                .build();

        memberRepository.save(member);

        return getMember(userId);
    }

    // ✅ 카카오 로그인: 있으면 반환, 없으면 생성
    public Member loginOrCreateKakaoMember(Long kakaoId, String nickname) {

        String socialId = String.valueOf(kakaoId); // "123456789" 이런 형태

        return memberRepository.findBySocialTypeAndSocialId(SocialType.KAKAO, socialId)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .name(nickname)
                            .gender(Gender.NONE)
                            .age(null)
                            .address(null)
                            .detailAddress(null)
                            .memberStatus("ACTIVE")
                            .suspendedAt(null)
                            .phoneNumber(null)
                            .socialType(SocialType.KAKAO)
                            .socialId(socialId)
                            .point(0)
                            .build();
                    return memberRepository.save(newMember);
                });
    }
}
