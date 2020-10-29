package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythwork.soda.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
}
