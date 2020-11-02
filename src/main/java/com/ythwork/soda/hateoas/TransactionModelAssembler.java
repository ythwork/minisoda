package com.ythwork.soda.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ythwork.soda.domain.Transaction;
import com.ythwork.soda.domain.TransactionFilter;
import com.ythwork.soda.domain.TransactionStatus;
import com.ythwork.soda.web.TransactionController;

@Component
public class TransactionModelAssembler implements RepresentationModelAssembler<Transaction, EntityModel<Transaction>> {

	@Override
	public EntityModel<Transaction> toModel(Transaction transaction) {
		TransactionFilter transactionFilter = new TransactionFilter();
		EntityModel<Transaction> entityModel =  EntityModel.of(transaction, 
				linkTo(methodOn(TransactionController.class).search(transactionFilter)).withRel("transactions"));
		
		if(transaction.getTransactionStatus() != TransactionStatus.CANCELED) {
			entityModel.add(linkTo(methodOn(TransactionController.class).getTransaction(transaction.getId())).withSelfRel());
		}
		
		if(transaction.getTransactionStatus() == TransactionStatus.IN_PROCESS) {
			entityModel.add(linkTo(methodOn(TransactionController.class).complete(transaction.getId())).withRel("complete"));
			entityModel.add(linkTo(methodOn(TransactionController.class).cancel(transaction.getId())).withRel("cancel"));
		}
		
		return entityModel;
	}

}
