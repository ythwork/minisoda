package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.ythwork.soda.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	Member findByUsername(@Param("username") String username);
	Member findByEmail(String email);
}
