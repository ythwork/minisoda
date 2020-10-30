package com.ythwork.soda.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ythwork.soda.data.BankcodeRepository;
import com.ythwork.soda.data.OpenapiRepository;
import com.ythwork.soda.domain.Address;
import com.ythwork.soda.domain.Bankcode;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Openapi;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace=Replace.NONE)
public class TransactionServiceTests {
	@Autowired
	BankcodeRepository bankcodeRepo;
	@Autowired
	MemberService memberService;
	@Autowired
	OpenapiRepository openapiRepo;
	@Autowired
	TransactionService transactionService;
	
	@Test
	public void transferTest() {
		// given
		Member m = new Member();
		m.setFirstName("태환");
		m.setLastName("양");
		m.setPhoneNumber("01066496270");
		Address address = new Address();
		address.setCountry("한국");
		address.setProvince("경기도");
		address.setCity("군포시");
		address.setStreet("산본천로");
		address.setHouseNumber("214");
		m.setAddress(address);
		m.setEmail("ythwork@naver.com");
		
		memberService.register(m);
		
		Openapi send = new Openapi();
		Bankcode bc1 = bankcodeRepo.findByCode("A BANK");
		send.setBankcode(bc1);
		send.setOwner("양태환");
		send.setAccountNumber("123-45-6789");
		send.setBalance(20000L);
		openapiRepo.save(send);
		
		Openapi recv = new Openapi();
		Bankcode bc2 = bankcodeRepo.findByCode("B BANK");
		send.setBankcode(bc2);
		send.setOwner("조샛별");
		send.setAccountNumber("987-65-4321");
		send.setBalance(500L);
		openapiRepo.save(recv);
		
		
		
		
		
	}
}
