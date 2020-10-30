package com.ythwork.soda.service;

import static com.ythwork.soda.domain.TransactionSpec.amountGtOrEt;
import static com.ythwork.soda.domain.TransactionSpec.memberIs;
import static com.ythwork.soda.domain.TransactionSpec.processAtBetween;
import static com.ythwork.soda.domain.TransactionSpec.sendIs;
import static com.ythwork.soda.domain.TransactionSpec.statusIs;
import static org.springframework.data.jpa.domain.Specification.where;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.TransactionRepository;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Openapi;
import com.ythwork.soda.domain.Transaction;
import com.ythwork.soda.domain.TransactionFilter;
import com.ythwork.soda.domain.TransactionStatus;

@Service
@Transactional
public class TransactionService {
	@Autowired
	private MemberService memberService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private OpenapiService openapiService;
	@Autowired
	private TransactionRepository transactionRepo;
	
	public Long createTransaction(Long memberId, Long sendAcntId, String recvcode, String recvAcntNum, Long amount) {
		Transaction transaction = new Transaction();
		
		Member member = memberService.findById(memberId);
		Openapi send = accountService.findOpenapiByAccountId(sendAcntId);
		Openapi recv = openapiService.findOpenapi(recvcode, recvAcntNum);
		
		Long afterBalance = send.getBalance();
		
		transaction.setMember(member);
		transaction.setSend(send);
		transaction.setRecv(recv);
		transaction.setAfterBalance(afterBalance);
		
		transactionRepo.save(transaction);
		
		return transaction.getId();
	}
	
	public boolean transfer(Transaction transaction) {
		return true;
	}
	
	public List<Transaction> search(TransactionFilter filter) {
		Long memberId = filter.getMemberId();
		Long sendAcntId = filter.getSendAcntId();
		Date from = filter.getFrom();
		Date to = filter.getTo();
		TransactionStatus status = filter.getStatus();
		Member member = memberService.findById(memberId);
		Openapi send = accountService.findOpenapiByAccountId(sendAcntId);
		Long amount = filter.getAmount();
		
		return transactionRepo.findAll(where(memberIs(member)
								.and(sendIs(send))
								.and(processAtBetween(from, to))
								.and(statusIs(status))
								.and(amountGtOrEt(amount))));
	}
}
