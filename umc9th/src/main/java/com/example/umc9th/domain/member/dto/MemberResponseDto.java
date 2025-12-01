package com.example.umc9th.domain.member.dto;

import com.example.umc9th.domain.member.enums.Gender;
import com.example.umc9th.global.auth.enums.SocialType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberResponseDto {

    private Long userId;
    private String name;
    private Gender gender;
    private Integer age;
    private String address;
    private String detailAddress;
    private String memberStatus;
    private String phoneNumber;
    private SocialType socialType;
    private Integer point;

}
