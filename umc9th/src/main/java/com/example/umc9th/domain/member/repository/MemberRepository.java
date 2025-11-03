package com.example.umc9th.domain.member.repository;

import com.example.umc9th.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberQueryDsl{
    List<Member> findByNameAndMemberStatus(String name, String memberStatus);

    @Query("select m from Member m where m.name = :name and m.memberStatus is null")
    List<Member> findActiveMember(@Param("name") String name);
}
