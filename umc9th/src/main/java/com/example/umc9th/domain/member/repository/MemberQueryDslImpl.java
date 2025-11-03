package com.example.umc9th.domain.member.repository;

import com.example.umc9th.domain.member.entity.Member;
import com.example.umc9th.domain.member.entity.QMember;
import com.example.umc9th.domain.review.entity.Review;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberQueryDslImpl implements MemberQueryDsl {

    private final EntityManager entityManager;

    @Override
    public List<Member> searchMember(Predicate predicate){

        //JPA 셋팅
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

        //Q클래스 생성
        QMember member = QMember.member;

        return queryFactory
                .selectFrom(member)
                .where(predicate)
                .fetch();
    }


}
