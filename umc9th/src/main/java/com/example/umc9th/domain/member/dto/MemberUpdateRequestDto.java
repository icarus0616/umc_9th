package com.example.umc9th.domain.member.dto;

import com.example.umc9th.domain.member.enums.Gender;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MemberUpdateRequestDto {

    private String name;
    private Gender gender;
    private Integer age;
    private String address;
    private String detailAddress;
    private String phoneNumber;

}
