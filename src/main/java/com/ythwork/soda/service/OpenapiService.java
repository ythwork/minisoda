package com.ythwork.soda.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.BankcodeRepository;
import com.ythwork.soda.data.OpenapiRepository;
import com.ythwork.soda.domain.Bankcode;
import com.ythwork.soda.domain.Openapi;
import com.ythwork.soda.exception.EntityNotFoundException;

@Service
@Transactional
public class OpenapiService {
	@Autowired
	private BankcodeRepository bankcodeRepo;
	@Autowired
	private OpenapiRepository openapiRepo;
	
	// 통합 은행 api 서비스에서 계좌 정보를 가져온다.
	public Openapi findOpenapi(String code, String accountNumber) {
		Bankcode bankcode = bankcodeRepo.findByCode(code);
		if(bankcode == null) {
			throw new EntityNotFoundException("은행 코드 : " + code + "는 존재하지 않습니다.");
		}
		Openapi api = openapiRepo.findByBankcodeAndAccountNumber(bankcode, accountNumber);
		if(api != null) {
			return api;
		} else {
			throw new EntityNotFoundException("계좌번호 : " + accountNumber + "가 존재하지 않습니다.");
		}
	}
	
	public Openapi findById(Long id) {
		Optional<Openapi> optOpenapi = openapiRepo.findById(id);
		if(optOpenapi.isPresent()) { 
			return optOpenapi.get();
		} else {
			throw new EntityNotFoundException("계좌 정보가 없습니다.");
		}
	}
	
	public void deleteAll() {
		openapiRepo.deleteAll();
	}
}
