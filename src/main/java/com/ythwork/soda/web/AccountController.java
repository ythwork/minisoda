package com.ythwork.soda.web;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ythwork.soda.domain.Account;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.dto.AccountAddInfo;
import com.ythwork.soda.dto.AccountInfo;
import com.ythwork.soda.exception.AccountAlreadyExistsException;
import com.ythwork.soda.exception.AccountNotFoundException;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFoundException;
import com.ythwork.soda.exception.NotAllowedMemberException;
import com.ythwork.soda.hateoas.AccountModelAssembler;
import com.ythwork.soda.service.AccountService;

@RestController
@RequestMapping("/account")
public class AccountController {
	private final AccountService accountService;
	private final AccountModelAssembler assembler;
	
	public AccountController(AccountService accountService, AccountModelAssembler assembler) {
		this.accountService = accountService;
		this.assembler = assembler;
	}
	
	@GetMapping
	public CollectionModel<EntityModel<AccountInfo>> allAccounts(@AuthenticationPrincipal Member member) {
		List<AccountInfo> accounts = accountService.getAllAccountInfosByMember(member);
		List<EntityModel<AccountInfo>> entityModels = accounts.stream()
				.map(assembler::toModel)
				.collect(Collectors.toList());
		
		return CollectionModel.of(entityModels, 
				linkTo(methodOn(AccountController.class).allAccounts(member)).withSelfRel());
	}
	
	@GetMapping("/{id}")
	public EntityModel<AccountInfo> getAccount(@PathVariable Long id) {
		AccountInfo accountInfo = null;
		try {
			accountInfo = accountService.getAccountInfo(id);
		} catch(EntityNotFoundException e) {
			throw new AccountNotFoundException(e.getMessage(), e);
		}
		return assembler.toModel(accountInfo);
	}
	
	@PostMapping
	ResponseEntity<?> addAccount(@RequestBody AccountAddInfo accountAddInfo, @AuthenticationPrincipal Member member) {
		if(accountAddInfo.getMemberId() != member.getId()) {
			throw new NotAllowedMemberException("회원의 계좌 번호가 아닙니다.");
		}
		
		AccountInfo accountInfo = null;
		try {
			accountInfo = accountService.addAccountToBoard(accountAddInfo);
		} catch(EntityNotFoundException e) {
			throw new AccountNotFoundException(e.getMessage(), e);
		} catch(EntityAlreadyExistsException e) {
			throw new AccountAlreadyExistsException(e.getMessage(), e);
		}
		
		EntityModel<AccountInfo> entityModel = assembler.toModel(accountInfo);
		
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}
	
	@DeleteMapping("/{id}")
	ResponseEntity<?> deleteAccount(@PathVariable Long id, @AuthenticationPrincipal Member member) {
		Account account = null;
		try {
			account = accountService.findById(id);
		} catch(EntityNotFoundException e) {
			throw new AccountNotFoundException(e.getMessage(), e);
		}
		
		if(account.getMember().getId() != member.getId()) {
			throw new NotAllowedMemberException("회원의 계좌 번호가 아닙니다. 삭제할 수 없습니다.");
		}
		
		accountService.deleteAccount(id);
		
		return ResponseEntity.noContent().build();
	}
}
