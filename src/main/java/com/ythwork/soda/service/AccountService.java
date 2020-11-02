package com.ythwork.soda.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.AccountRepository;
import com.ythwork.soda.domain.Account;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Openapi;
import com.ythwork.soda.dto.AccountAddInfo;
import com.ythwork.soda.dto.AccountInfo;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFoundException;

@Service
@Transactional
public class AccountService {
	@Autowired
	private MemberService memberService;
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private OpenapiService openapiService;
	
	// 서비스에 이용할 계좌를 추가한다.
	// 성공하면 등록한 계좌 정보를 반환한다.
	public AccountInfo addAccountToBoard(AccountAddInfo accountAddInfo) {
		Long memberId = accountAddInfo.getMemberId();
		String bankcode = accountAddInfo.getCode();
		String accountNumber = accountAddInfo.getAccountNumber();
		Openapi api = null;
		Member m = null;
		
		try {
			api = openapiService.findOpenapi(bankcode, accountNumber);
			m = memberService.findById(memberId);
		} catch(EntityNotFoundException e) {
			throw e;
		}
		
		Account account = new Account();
		account.setMember(m);
		account.setOpenapi(api);
		
		if(alreadyAccount(account)) {
			throw new EntityAlreadyExistsException("이미 등록된 계좌입니다.");
		}
		
		// em.persist(account)
		// 퍼시스턴스 컨텍스트의 1차 캐시에 등록 
		// 이 시점에 아직 데이터베이스로 SQL을 전송하지는 않는다.
		accountRepo.save(account);
		
		return fromAccountToAccountInfo(account);
	}
	
	public Openapi findOpenapiByAccountId(Long accountId) {
		Optional<Account> optAccount = accountRepo.findById(accountId);
		if(optAccount.isPresent()) {
			return optAccount.get().getOpenapi();
		} else {
			throw new EntityNotFoundException("계좌 정보를 찾지 못했습니다.");
		}
	}
	
	private boolean alreadyAccount(Account account) {
		Account a = accountRepo.findByOpenapi(account.getOpenapi());
		return a != null ? true : false;
	}
	
	public AccountInfo fromAccountToAccountInfo(Account account) {
		return new AccountInfo(account.getOpenapi().getOwner(), 
				account.getOpenapi().getBankcode().getCode(), 
				account.getOpenapi().getAccountNumber(),
				account.getOpenapi().getBalance(),
				account.getId());
	}
	
	public AccountInfo getAccountInfo(Long id) {
		Optional<Account> optAccount = accountRepo.findById(id);
		Account account = null;
		if(optAccount.isPresent()) {
			account = optAccount.get();
		} else {
			throw new EntityNotFoundException("계좌 정보가 아직 등록되어 있지 않습니다.");
		}
		
		return fromAccountToAccountInfo(account);
	}
	
	public List<AccountInfo> getAllAccountInfos() {
		List<Account> allAccounts = accountRepo.findAll();
		return allAccounts.stream().map(this::fromAccountToAccountInfo).collect(Collectors.toList());
	}
	
	public void deleteAccount(Long id) {
		accountRepo.deleteById(id);
	}
	
	public void deleteAll() {
		accountRepo.deleteAll();
	}
}
