package com.ythwork.soda.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.BankcodeRepository;
import com.ythwork.soda.data.OpenapiRepository;
import com.ythwork.soda.domain.Address;
import com.ythwork.soda.domain.Bankcode;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Openapi;
import com.ythwork.soda.domain.Transaction;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// Service 빈을 컨텍스트에 올려야 하므로 모든 빈을 불러들여 
// 통합 테스트 환경을 만들어주는 부트 테스트 애너테이션을 사용한다.
@SpringBootTest
// 스프링 컨테이너의 기본 전략은 
// 트랜잭션과 퍼시스턴스 컨텍스트의 라이프 사이클이 같다.
// 즉, 트랜잭션이 시작되지 않으면 퍼시스턴스 컨텍스트도 만들어지지 않는다.
// @Transactional 애너테이션을 붙이지 않으면 다음과 같은 에러가 발생하는데
//org.hibernate.LazyInitializationException: (생략) could not initialize proxy
// 이는 글로벌 페치 전략을 fetch=FetchType.LAZY로 해놓았기 때문에 연관 객체에 대해 프록시 객체를 만들려고 하는데
// 트랜잭션이 시작되지 않았으므로 퍼시스턴스 컨텍스트가 만들어지지 않아서 실패하기 때문이다.
// 테스트 환경에서는 테스트가 끝나고 모든 SQL에 대해 롤백해주는 역할도 한다. 
@Transactional
public class TransactionServiceTests {
	@Autowired
	BankcodeRepository bankcodeRepo;
	@Autowired
	MemberService memberService;
	@Autowired
	AccountService accountService;
	@Autowired
	OpenapiRepository openapiRepo;
	@Autowired
	TransactionService transactionService;
	
	private void printAccountInfo(String who, Openapi api) {
		log.info(who + " : [" + "owner : " + api.getOwner() + 
				", bank : " + api.getBankcode().getCode() + 
				", account number : " + api.getAccountNumber() + 
				", balance : " + api.getBalance());
	}
	
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
		recv.setBankcode(bc2);
		recv.setOwner("조샛별");
		recv.setAccountNumber("987-65-4321");
		recv.setBalance(500L);
		openapiRepo.save(recv);
		log.info("BEFORE TRANSFER");
		printAccountInfo("send", send);
		printAccountInfo("recv", recv);
		
		Long sendAcntId = accountService.addAccountToBoard(m.getId(), "A BANK", "123-45-6789");
		
		//when
		Long transactionId = transactionService.createTransaction(m.getId(), sendAcntId, "B BANK", "987-65-4321", 10000L);
		
		// 트랜잭션 상태는 잘 변경되는지 확인하기 위해 트랜잭션을 받아온다.
		Transaction transaction = transactionService.findById(transactionId);
		log.info("Transaction Status : " + transaction.getTransactionStatus());
		
		transactionService.transfer(transactionId);
		
		log.info("AFTER TRANSFER");
		// 퍼시스턴스 컨텍스트에 속해있는 상태이므로 openapiRepo를 이용해 
		// 다시 받아오지 않아도 데이터가 업데이트 된다. 
		// lazy writing을 하므로 트랜잭션이 끝나기 전에는 flush 하지 않는다. 
		printAccountInfo("send", send);
		printAccountInfo("recv", recv);
		
		// 무사히 변경되었다.
		log.info("Transaction Status : " + transaction.getTransactionStatus());

		
		//then
		assertEquals(send.getBalance(), 10000L);
		assertEquals(recv.getBalance(), 10500L);
	}
	
}
