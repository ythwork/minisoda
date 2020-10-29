package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythwork.soda.domain.Bankcode;
import com.ythwork.soda.domain.Openapi;

public interface OpenapiRepository extends JpaRepository<Openapi, Long> {
	Openapi findByBankcodeAndAccountNumber(Bankcode bankcode, String accountNumber);
}
