package com.ythwork.soda.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ythwork.soda.domain.Transaction;
import com.ythwork.soda.domain.TransactionFilter;
import com.ythwork.soda.domain.TransactionStatus;
import com.ythwork.soda.dto.TransactionAddInfo;
import com.ythwork.soda.dto.TransactionInfo;
import com.ythwork.soda.exception.EntityNotFoundException;
import com.ythwork.soda.exception.InvalidTransactionInfoProvidedException;
import com.ythwork.soda.exception.NotEnoughBalanceException;
import com.ythwork.soda.exception.TransactionNotFoundException;
import com.ythwork.soda.hateoas.TransactionModelAssembler;
import com.ythwork.soda.service.TransactionService;

@RestController
@RequestMapping("/transaction")
public class TransactionController {
	private final TransactionService transactionService;
	private final TransactionModelAssembler assembler;
	
	public TransactionController(TransactionService transactionService, TransactionModelAssembler assembler) {
		this.transactionService = transactionService;
		this.assembler = assembler;
	}
	
	@GetMapping
	public CollectionModel<EntityModel<TransactionInfo>> search(@RequestBody TransactionFilter transactionFilter) {
		System.out.println("TransactionFilter : " + transactionFilter);
		List<EntityModel<TransactionInfo>> transactions = transactionService.search(transactionFilter).stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		
		return CollectionModel.of(transactions, 
				linkTo(methodOn(TransactionController.class).search(transactionFilter)).withSelfRel());
	}
	
	@GetMapping("/{id}")
	public EntityModel<TransactionInfo> getTransaction(@PathVariable Long id) {
		TransactionInfo transactionInfo = null; 
		try {
			transactionInfo = transactionService.getTransactionInfoById(id);
		} catch(EntityNotFoundException e) {
			throw new TransactionNotFoundException(e.getMessage(), e);
		}
		
		return assembler.toModel(transactionInfo);
	}
	
	@PostMapping
	public ResponseEntity<EntityModel<TransactionInfo>> newTransaction(@RequestBody TransactionAddInfo transactionAddInfo) {
		TransactionInfo transactionInfo = null;
		try {
			transactionInfo = transactionService.createTransaction(transactionAddInfo);
		} catch(EntityNotFoundException e) {
			 throw new InvalidTransactionInfoProvidedException(e.getMessage(), e);
		} 
		
		return ResponseEntity.created(linkTo(methodOn(TransactionController.class).getTransaction(transactionInfo.getTransactionId())).toUri())
				.body(assembler.toModel(transactionInfo));
	}
	
	@PutMapping("/{id}/complete")
	public ResponseEntity<?> complete(@PathVariable Long id) {
		TransactionInfo transactionInfo = null;
		try {
			transactionInfo = transactionService.getTransactionInfoById(id);
		} catch(EntityNotFoundException e) {
			throw new TransactionNotFoundException(e.getMessage(), e);
		}
		
		if(transactionInfo.getTransactionStatus() == TransactionStatus.IN_PROCESS) {
			try {
				TransactionInfo completedTransactionInfo = transactionService.transfer(transactionInfo.getTransactionId());
				return ResponseEntity.ok(assembler.toModel(completedTransactionInfo));
			} catch(NotEnoughBalanceException e) {
				TransactionInfo failedTransactionInfo = transactionService.getTransactionInfoById(transactionInfo.getTransactionId());
				return ResponseEntity.badRequest().body(assembler.toModel(failedTransactionInfo));
			}
		}
		
		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				// Problem : hypermedia-지원 에러 컨테이너 
				.body(Problem
						.create()
						.withTitle("Method not allowed")
						.withDetail("진행 중이 아닌 경우 송금할 수 없습니다. 현재 트랜잭션 상태는 " + transactionInfo.getTransactionStatus() + "입니다."));
	}
	
	@DeleteMapping("/{id}/cancel")
	public ResponseEntity<?> cancel(@PathVariable Long id) {
		Transaction transaction = null;
		try {
			transaction = transactionService.findById(id);
		} catch(EntityNotFoundException e) {
			throw new TransactionNotFoundException(e.getMessage(), e);
		}
		
		if(transaction.getTransactionStatus() == TransactionStatus.IN_PROCESS) {
			transactionService.deleteById(transaction.getId());
			transaction.setTransactionStatus(TransactionStatus.CANCELED);
			return ResponseEntity.ok(assembler.toModel(
					transactionService.fromTransactionToTransactionInfo(transaction)));
		}
		
		return ResponseEntity
				.status(HttpStatus.METHOD_NOT_ALLOWED)
				.header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
				.body(Problem
						.create()
						.withTitle("Method not allowed")
						.withDetail("진행 중이 아닌 경우 취소할 수 없습니다. 현재 트랜잭션 상태는 " + transaction.getTransactionStatus() + "입니다."));
	}
}
