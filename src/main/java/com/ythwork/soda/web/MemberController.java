package com.ythwork.soda.web;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ythwork.soda.domain.Member;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFound;
import com.ythwork.soda.exception.MemberAlreadyExistsException;
import com.ythwork.soda.exception.MemberNotFoundException;
import com.ythwork.soda.hateoas.MemberModelAssembler;
import com.ythwork.soda.service.MemberService;

@RestController
// Accept 헤더가 "application/json" 인 요청만 받는다.
@RequestMapping(path="/member", produces="application/json")
// 다른 도메인(프로토콜, 호스트, 포트)을 가진 클라이언트에서 API를 사용할 수 있다. 
@CrossOrigin(origins="*")
public class MemberController {
	private final MemberService memberService;
	private final MemberModelAssembler assembler;
	
	public MemberController(MemberService memberService, MemberModelAssembler assembler) {
		this.memberService = memberService;
		this.assembler = assembler;
	}
	
	// consumes는 Content-type : application/json 인 요청만 처리한다는 의미
	@PostMapping(path="/register", consumes="application/json")
	public ResponseEntity<?> registerMember(@RequestBody Member member) {
		// @RequestBody : json -> Object
		// 없으면 쿼리나 폼 매개변수와 바인딩한다고 간주
		Member m = null;
		try {
			m = memberService.register(member);
		} catch(EntityAlreadyExistsException e) {
			// 예외 연쇄 기법
			// repository와 service 계층의 에러를 web 계층의 예외로 감싼다.
			// 컨트롤러 어드바이스로 예외 처리. 
			throw new MemberAlreadyExistsException(e.getMessage(), e);
		}
		
		EntityModel<Member> entityModel = assembler.toModel(m);
		/*
		CREATED status
		location header : given URI.
		*/
		return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
	}
	
	@GetMapping("/{id}")
	public EntityModel<Member> getMember(@PathVariable Long id) {
		Member member = null;
		try {
			member = memberService.findById(id);
		} catch(EntityNotFound e) {
			throw new MemberNotFoundException(e.getMessage(), e);
		}
		return assembler.toModel(member);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteMember(@PathVariable Long id) {
		memberService.deleteById(id);
		// DRI 설정으로 Member가 삭제되면 그와 연관된 Account와 Transaction 로우 모두 삭제
		return ResponseEntity.noContent().build();
	}
}
