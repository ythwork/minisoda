package com.ythwork.soda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.AccountRepository;
import com.ythwork.soda.data.BankcodeRepository;
import com.ythwork.soda.data.OpenapiRepository;
import com.ythwork.soda.domain.Account;
import com.ythwork.soda.domain.Bankcode;
import com.ythwork.soda.domain.Openapi;

@Service
@Transactional
public class AccountService {
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private OpenapiRepository openapiRepo;
	@Autowired
	private BankcodeRepository bankcodeRepo;
	
	public Long register(String bankcode, String accountNumber) {
		
	}
	
	private boolean alreadyAccount(Account account) {
		Openapi api = accountRepo.findByOpenapi(account.getOpenapi());
		return api != null ? true : false;
	}
	
	private Openapi findOpenapi(String code, String accountNumber) {
		Bankcode bankcode = bankcodeRepo.findByCode(code);
		if(bankcode == null) return null;
		return openapiRepo.findByBankcodeAndAccountNumber(bankcode, accountNumber);
	}
}
