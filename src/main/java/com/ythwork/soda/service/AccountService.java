package com.ythwork.soda.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.AccountRepository;
import com.ythwork.soda.data.MemberRepository;
import com.ythwork.soda.domain.Account;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Openapi;
import com.ythwork.soda.dto.AccountInfo;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFound;

@Service
@Transactional
public class AccountService {
	@Autowired
	private MemberRepository memberRepo;
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private OpenapiService openapiService;
	
	// 서비스에 이용할 계좌를 추가한다.
	// 성공하면 등록한 계좌 정보를 반환한다.
	public Long addAccountToBoard(Long memberId, String bankcode, String accountNumber) {
		Openapi api = openapiService.findOpenapi(bankcode, accountNumber);
		if(api == null) {
			throw new EntityNotFound("계좌번호 : " + accountNumber + "는 존재하지 않습니다.");
		}
		Optional<Member> optMem = memberRepo.findById(memberId);
		Member m = null;
		if(optMem.isPresent()) {
			m = optMem.get();
		} else {
			throw new EntityNotFound("존재하지 않는 회원입니다.");
		}
		
		Account account = new Account();
		account.setMember(m);
		account.setOpenapi(api);
		
		if(alreadyAccount(account)) {
			throw new EntityAlreadyExistsException("이미 등록된 계좌입니다.");
		}
		
		accountRepo.save(account);
		
		return account.getId();
	}
	
	public Openapi findOpenapiByAccountId(Long accountId) {
		Optional<Account> optAccount = accountRepo.findById(accountId);
		if(optAccount.isPresent()) {
			return optAccount.get().getOpenapi();
		} else {
			throw new EntityNotFound("계좌 정보를 찾지 못했습니다.");
		}
	}
	
	private boolean alreadyAccount(Account account) {
		Account a = accountRepo.findByOpenapi(account.getOpenapi());
		return a != null ? true : false;
	}
	
	// 다른 채널을 통해 계좌로 금융 거래를 했을지도 모르므로
	// 대시보드를 보여주기 전에 항상 업데이트를 해야 한다.
	public Long updateAccount(Account account) {
		Openapi oldOpenapi = account.getOpenapi();
		Openapi api = openapiService.findById(oldOpenapi.getId());
		account.setOpenapi(api);
		accountRepo.save(account);
		
		return account.getId();
	}
	
	public AccountInfo getAccountInfo(Long id) {
		Optional<Account> optAccount = accountRepo.findById(id);
		Account account = null;
		if(optAccount.isPresent()) {
			account = optAccount.get();
		} else {
			throw new EntityNotFound("계좌 정보가 아직 등록되어 있지 않습니다.");
		}
		
		return new AccountInfo(account.getOpenapi().getOwner(), 
					account.getOpenapi().getBankcode().getCode(), 
					account.getOpenapi().getAccountNumber(),
					account.getOpenapi().getBalance(),
					account.getId());
	}
	
	public void deleteAccount(Long id) {
		accountRepo.deleteById(id);
	}
}
