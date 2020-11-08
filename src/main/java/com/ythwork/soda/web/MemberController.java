package com.ythwork.soda.web;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ythwork.soda.dto.LoginRequest;
import com.ythwork.soda.dto.LoginResponse;
import com.ythwork.soda.dto.MemberAddInfo;
import com.ythwork.soda.dto.MemberInfo;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFoundException;
import com.ythwork.soda.exception.LoginFailureException;
import com.ythwork.soda.exception.MemberAlreadyExistsException;
import com.ythwork.soda.exception.MemberNotFoundException;
import com.ythwork.soda.exception.NotAllowedMemberException;
import com.ythwork.soda.hateoas.JwtResponseAssembler;
import com.ythwork.soda.hateoas.MemberModelAssembler;
import com.ythwork.soda.security.JwtManager;
import com.ythwork.soda.service.MemberService;

@RestController
// Accept 헤더가 "application/json" 인 요청만 받는다.
@RequestMapping(path="/member", produces="application/json")
// 다른 도메인(프로토콜, 호스트, 포트)을 가진 클라이언트에서 API를 사용할 수 있다. 
@CrossOrigin(origins="*")
public class MemberController {
	private final MemberService memberService;
	private final MemberModelAssembler assembler;
	private final JwtManager jwtManager;
	private final JwtResponseAssembler jwtAssembler;
	
	public MemberController(MemberService memberService, MemberModelAssembler assembler, 
							JwtManager jwtManager, JwtResponseAssembler jwtAssembler) {
		this.memberService = memberService;
		this.assembler = assembler;
		this.jwtManager = jwtManager;
		this.jwtAssembler = jwtAssembler;
	}
	
	// consumes는 Content-type : application/json 인 요청만 처리한다는 의미
	@PostMapping(path="/register", consumes="application/json")
	public ResponseEntity<?> registerMember(@RequestBody MemberAddInfo memberAddInfo, HttpServletRequest request) throws URISyntaxException {
		// @RequestBody : json -> Object
		// 없으면 쿼리나 폼 매개변수와 바인딩한다고 간주
		MemberInfo m = null;
		try {
			m = memberService.register(memberAddInfo);
		} catch(EntityAlreadyExistsException e) {
			// 예외 연쇄 기법
			// repository와 service 계층의 에러를 web 계층의 예외로 감싼다.
			// 컨트롤러 어드바이스로 예외 처리. 
			throw new MemberAlreadyExistsException(e.getMessage(), e);
		}
		
		EntityModel<MemberInfo> entityModel = assembler.toModel(m);
		/*
		CREATED status
		location header : given URI.
		*/
		
		String loginUri = request.getRequestURL().toString().replace("register", "login");
		return ResponseEntity.created(new URI(loginUri)).body(entityModel);
	}
	
	@PostMapping("/login")
	public EntityModel<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
		Authentication authentication = null;
		try {
			authentication = memberService.authenticate(loginRequest);
		} catch(AuthenticationException e) {
			throw new LoginFailureException(e.getMessage(), e);
		}
		
		String token = jwtManager.getToken(authentication);
		MemberInfo memberInfo = null;
		try {
			memberInfo = memberService.getMemberInfoByUsername(loginRequest.getUsername());
		} catch(EntityNotFoundException e) {
			throw new MemberNotFoundException(e.getMessage(), e);
		}
		LoginResponse jwtResponse = new LoginResponse(token, memberInfo);
		
		return jwtAssembler.toModel(jwtResponse);
	}
	
	@GetMapping("/{id}")
	public EntityModel<MemberInfo> getMember(@PathVariable Long id, HttpServletRequest request) {
		MemberInfo memberInfo = null;
		try {
			memberInfo = memberService.getMemberInfoById(id);
		} catch(EntityNotFoundException e) {
			throw new MemberNotFoundException(e.getMessage(), e);
		}
		String username = jwtManager.getUsername(request);
		if(memberService.getMemberInfoByUsername(username).getMemberId() != memberInfo.getMemberId()) {
			throw new NotAllowedMemberException("멤버 자신의 정보에만 접근이 가능합니다.");
		}
		
		return assembler.toModel(memberInfo);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteMember(@PathVariable Long id, HttpServletRequest request) {
		String username = jwtManager.getUsername(request);
		if(memberService.getMemberInfoByUsername(username).getMemberId() != memberService.getMemberInfoById(id).getMemberId()) {
			throw new NotAllowedMemberException("멤버 자신의 정보에만 접근이 가능합니다.");
		}
		
		memberService.deleteById(id);
		// DRI 설정으로 Member가 삭제되면 그와 연관된 Account와 Transaction 로우 모두 삭제
		return ResponseEntity.noContent().build();
	}
}
