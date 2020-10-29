package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythwork.soda.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
