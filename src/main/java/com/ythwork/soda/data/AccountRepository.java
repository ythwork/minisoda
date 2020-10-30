package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythwork.soda.domain.Account;
import com.ythwork.soda.domain.Openapi;

public interface AccountRepository extends JpaRepository<Account, Long> {
	Account findByOpenapi(Openapi api);
}
