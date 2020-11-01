package com.ythwork.soda.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ythwork.soda.domain.Transaction;
import com.ythwork.soda.domain.TransactionFilter;
import com.ythwork.soda.dto.TransactionAddInfo;
import com.ythwork.soda.exception.EntityNotFound;
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
	public CollectionModel<EntityModel<Transaction>> search(@RequestBody TransactionFilter transactionFilter) {
		List<EntityModel<Transaction>> transactions = transactionService.search(transactionFilter).stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		
		return CollectionModel.of(transactions, 
				linkTo(methodOn(TransactionController.class).search(transactionFilter)).withSelfRel());
	}
	
	@GetMapping("/{id}")
	public EntityModel<Transaction> getTransaction(@PathVariable Long id) {
		Transaction transaction = null; 
		try {
			transaction = transactionService.findById(id);
		} catch(EntityNotFound e) {
			throw new TransactionNotFoundException(e.getMessage(), e);
		}
		
		return assembler.toModel(transaction);
	}
	
	@PostMapping
	public ResponseEntity<EntityModel<Transaction>> newTransaction(@RequestBody TransactionAddInfo transactionAddInfo) {
		Transaction transaction = null;
		try {
			transaction = transactionService.createTransaction(transactionAddInfo);
		} catch() {
			 
		} catch() {
			
		}
		
		return ResponseEntity.created(linkTo(methodOn(TransactionController.class).getTransaction(transaction.getId())).toUri())
				.body(assembler.toModel(transaction));
	}
	
}
