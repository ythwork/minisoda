package com.ythwork.soda.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import com.ythwork.soda.domain.Transaction;
import com.ythwork.soda.domain.TransactionFilter;
import com.ythwork.soda.web.TransactionController;

public class TransactionModelAssembler implements RepresentationModelAssembler<Transaction, EntityModel<Transaction>> {

	@Override
	public EntityModel<Transaction> toModel(Transaction transaction) {
		TransactionFilter transactionFilter = new TransactionFilter();
		return EntityModel.of(transaction, 
				linkTo(methodOn(TransactionController.class).getTransaction(transaction.getId())).withSelfRel(),
				linkTo(methodOn(TransactionController.class).search(transactionFilter)).withRel("transactions"));
	}

}
