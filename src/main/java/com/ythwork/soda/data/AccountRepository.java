package com.ythwork.soda.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythwork.soda.domain.Account;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Openapi;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findByOpenapi(Openapi api);
	List<Account> findByMember(Member member);
}
