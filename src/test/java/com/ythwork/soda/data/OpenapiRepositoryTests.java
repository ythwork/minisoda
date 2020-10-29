package com.ythwork.soda.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.domain.Bankcode;
import com.ythwork.soda.domain.Openapi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
// 테스트할 때는 테스트 종료 후에 자동으로 롤백
@Transactional
public class OpenapiRepositoryTests {
	@Autowired
	BankcodeRepository bankRepo;
	@Autowired
	OpenapiRepository openapiRepo;
	
	@Test
	public void findByBankcodeAndAccountNumberTest() {
		// given
		final String bankcode = "A BANK";
		final String accountNumber = "123-45-6789";
		final String owner = "양태환";
		final Long balance = 20000L;
		
		Optional<Bankcode> optBc1 = bankRepo.findById(1L);
		Bankcode bc1 = optBc1.get();
		
		Openapi api1 = new Openapi();		
		api1.setBankcode(bc1);
		api1.setAccountNumber(accountNumber);
		api1.setOwner(owner);
		api1.setBalance(balance);
		
		openapiRepo.save(api1);
		
		// when
		Openapi api = openapiRepo.findByBankcodeAndAccountNumber(bc1, accountNumber);
		log.info("[ id : " + api.getId() + ", bankcode : " + api.getBankcode().getCode() + 
				", account number : " + api.getAccountNumber() + 
				", owner : " + api.getOwner() + 
				", balance : " + api.getBalance() + 
				"]");
		
		// then
		assertEquals(api.getOwner(), owner);
		assertEquals(api.getBalance(), balance);
	}
	
	@AfterEach
	public void cleanup() {
		openapiRepo.deleteAll();
	}
	
}
