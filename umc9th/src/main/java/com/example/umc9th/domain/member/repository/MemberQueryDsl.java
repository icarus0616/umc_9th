package com.example.umc9th.domain.member.repository;

import com.example.umc9th.domain.member.entity.Member;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface MemberQueryDsl {
    List<Member> searchMember(Predicate predicate);

}
