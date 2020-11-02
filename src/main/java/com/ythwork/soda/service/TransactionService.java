package com.ythwork.soda.service;

import static com.ythwork.soda.domain.TransactionSpec.amountGtOrEt;
import static com.ythwork.soda.domain.TransactionSpec.memberIs;
import static com.ythwork.soda.domain.TransactionSpec.processAtBetween;
import static com.ythwork.soda.domain.TransactionSpec.sendIs;
import static com.ythwork.soda.domain.TransactionSpec.statusIs;
import static org.springframework.data.jpa.domain.Specification.where;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.TransactionRepository;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Openapi;
import com.ythwork.soda.domain.Transaction;
import com.ythwork.soda.domain.TransactionFilter;
import com.ythwork.soda.domain.TransactionStatus;
import com.ythwork.soda.dto.TransactionAddInfo;
import com.ythwork.soda.exception.EntityNotFound;
import com.ythwork.soda.exception.NotEnoughBalanceException;

@Service
// @Transactional은 unchecked exception = RuntimeException subclass에서만 롤백한다.
// check exception도 롤백하고 싶다면 @Transactional(rollbackFor = Exception.class)라고 명시해야 한다.
// unchecked exception 이지만 익셉션이 발생했을 때 발생 시점까지를 커밋하고 싶다면
// @Transactional(noRollbackFor=NotEoughBalanceException.class)처럼 명시하면 된다.
@Transactional(noRollbackFor=NotEnoughBalanceException.class)
public class TransactionService {
	@Autowired
	private MemberService memberService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private OpenapiService openapiService;
	@Autowired
	private TransactionRepository transactionRepo;
	
	public Transaction createTransaction(TransactionAddInfo transactionAddInfo) {
		Long memberId = transactionAddInfo.getMemberId();
		Long sendAcntId = transactionAddInfo.getSendAcntId();
		String recvcode = transactionAddInfo.getRecvcode();
		String recvAcntNum = transactionAddInfo.getRecvAcntNum();
		Long amount = transactionAddInfo.getAmount();
		
		Transaction transaction = new Transaction();
		
		Member member = memberService.findById(memberId);
		Openapi send = accountService.findOpenapiByAccountId(sendAcntId);
		Openapi recv = openapiService.findOpenapi(recvcode, recvAcntNum);
				
		transaction.setMember(member);
		transaction.setSend(send);
		transaction.setRecv(recv);
		transaction.setAmount(amount);
		transaction.setAfterBalance(0L);
		transaction.setTransactionStatus(TransactionStatus.IN_PROCESS);
		
		Transaction newTransaction = transactionRepo.save(transaction);
		
		return newTransaction;
	}
	
	public Transaction transfer(Transaction transaction) {
		Openapi send = transaction.getSend();
		Openapi recv = transaction.getRecv();
				
		Long sendBalance = send.getBalance();
		Long recvBalance = recv.getBalance();
		Long amount = transaction.getAmount();
		
		if(sendBalance - amount < 0) {
			transaction.setTransactionStatus(TransactionStatus.FAILED);
			transaction.setAfterBalance(sendBalance - amount);
			throw new NotEnoughBalanceException("잔고가 부족합니다.");
		}
		sendBalance -= amount;
		recvBalance += amount;
		
		// 트랜잭션이 실행되고 변경 사항을 
		// 퍼시스턴스 컨텍스트의 1차 캐시에 저장한다.
						
		// 퍼시스턴스 컨텍스트는 flush() 호출할 때 
		// 1차 캐시의 엔티티를 스냅샷과 비교하고
		// 변경된 내용에 대해 UPDATE 구문을 쓰기 지연 저장소에 등록한다.
		// 등록된 쿼리가 데이터베이스에 전송되어 동기화된다. 
		// 커밋이 일어나면 그때 모든 변경 사항이 데이터베이스에서 실행된다.
		
		// ---------- 업데이트 쿼리 시작 ------------------
		/*
		 현재 트랜잭션 안이므로 변경 사항은 퍼시스턴스 컨텍스트의 1차 캐시에 저장
		 트랜잭션 종료되면 
		 스냅샷과 비교 -> UPDATE 구문 생성 -> SQL 저장소 등록 -> 데이터베이스 동기화 
		
		 UPDATE 잠금 수준은 항상 잠금 대상보다 실제 update 적용 대상이 적으므로 
		 JPA의 PESSIMISTIC_WRITE를 이용해 row 단위로 SELECT ...FOR UPDATE를 걸고 
		 변경하는 것을 고려해볼 수 있으나 
		 이번 경우에는 변경하려는 튜플을 특정할 수 있으므로 UPDATE 구문만으로 충분하다.
		*/
		send.setBalance(sendBalance);
		recv.setBalance(recvBalance);
		
		Long afterBalance = sendBalance;
		transaction.setAfterBalance(afterBalance);
		transaction.setTransactionStatus(TransactionStatus.SUCCEEDED);
		
		// ----------- 업데이트 쿼리 종료 ----------------
		
		return transaction;
	}
	
	public void cancelTransaction(Long transactionId) {
		transactionRepo.deleteById(transactionId);
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
	
	public Transaction findById(Long id) {
		Optional<Transaction> transaction = transactionRepo.findById(id);
		if(transaction.isPresent()) {
			return transaction.get();
		} else {
			throw new EntityNotFound("transaction id [" + id + "]를 가진 트랜잭션 내역이 없습니다.");
		}
	}
	
	public void deleteById(Long id) {
		transactionRepo.deleteById(id);
	}
	
	public void deleteAll() {
		transactionRepo.deleteAll();
	}
}
