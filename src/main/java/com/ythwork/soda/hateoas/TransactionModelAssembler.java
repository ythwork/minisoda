package com.ythwork.soda.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ythwork.soda.domain.TransactionFilter;
import com.ythwork.soda.domain.TransactionStatus;
import com.ythwork.soda.dto.TransactionInfo;
import com.ythwork.soda.web.TransactionController;

@Component
public class TransactionModelAssembler implements RepresentationModelAssembler<TransactionInfo, EntityModel<TransactionInfo>> {

	@Override
	public EntityModel<TransactionInfo> toModel(TransactionInfo transactionInfo) {
		TransactionFilter transactionFilter = new TransactionFilter();
		EntityModel<TransactionInfo> entityModel =  EntityModel.of(transactionInfo, 
				linkTo(methodOn(TransactionController.class).search(transactionFilter, null)).withRel("transactions"));
		
		if(transactionInfo.getTransactionStatus() != TransactionStatus.CANCELED) {
			entityModel.add(linkTo(methodOn(TransactionController.class).getTransaction(transactionInfo.getTransactionId(), null)).withSelfRel());
		}
		
		if(transactionInfo.getTransactionStatus() == TransactionStatus.IN_PROCESS) {
			entityModel.add(linkTo(methodOn(TransactionController.class).complete(transactionInfo.getTransactionId(), null)).withRel("complete"));
			entityModel.add(linkTo(methodOn(TransactionController.class).cancel(transactionInfo.getTransactionId(), null)).withRel("cancel"));
		}
		
		return entityModel;
	}

}
