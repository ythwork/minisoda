package com.ythwork.soda.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.MemberRepository;
import com.ythwork.soda.data.RoleRepository;
import com.ythwork.soda.domain.Auth;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.domain.Role;
import com.ythwork.soda.domain.RoleType;
import com.ythwork.soda.dto.LoginRequest;
import com.ythwork.soda.dto.MemberAddInfo;
import com.ythwork.soda.dto.MemberInfo;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFoundException;

@Service
@Transactional
public class MemberService {
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private MemberRepository memberRepo;
	@Autowired
	private RoleRepository roleRepo;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	// 회원 등록
	public MemberInfo register(MemberAddInfo memberAddInfo) {
		Member member = new Member();
		member.setFirstName(memberAddInfo.getFirstName());
		member.setLastName(memberAddInfo.getLastName());
		member.setAddress(memberAddInfo.getAddress());
		member.setPhoneNumber(memberAddInfo.getPhoneNumber());
		member.setEmail(memberAddInfo.getEmail());
		
		Auth auth = new Auth();
		auth.setUsername(memberAddInfo.getUsername());
		auth.setPassword(passwordEncoder.encode(memberAddInfo.getPassword()));
		
		Set<String> roleStr = memberAddInfo.getRoles();
		Set<Role> roles = new HashSet<>();
		if(roleStr == null) {
			Role defaultRole = roleRepo.findByRoleType(RoleType.ROLE_USER);
			roles.add(defaultRole);
		} else {
			roleStr.forEach(role -> {
				switch(role) {
				case "ADMIN":
					Role adminRole = roleRepo.findByRoleType(RoleType.ROLE_ADMIN);
					roles.add(adminRole);
					break;
				default:
					Role userRole = roleRepo.findByRoleType(RoleType.ROLE_USER);
					roles.add(userRole);
				}
			});
		}	
		auth.setRoles(roles);
		member.setAuth(auth);
		
		if(alreadyMemberByUsername(member) || alreadyMemberByEmail(member)) {
			throw new EntityAlreadyExistsException(member.getLastName() + member.getFirstName() + " 님은 이미 가입한 회원입니다.");
		}
		
		Member newMember =  memberRepo.save(member);
		
		return fromMemberToMemberInfo(newMember);
	}
	
	// 회원 중복 가입 방지 1
	private boolean alreadyMemberByUsername(Member member) {
		Member m = memberRepo.findByUsername(member.getAuth().getUsername());
		return m != null ? true : false;
	}
	
	// 회원 중복 가입 방지 2
	private boolean alreadyMemberByEmail(Member member) {
		Member m = memberRepo.findByEmail(member.getEmail());
		return m != null ? true : false;
	}
	
	public Authentication authenticate(LoginRequest loginRequest) {
		String username = loginRequest.getUsername();
		String password = loginRequest.getPassword();
		
		try {
			Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
			return authentication;
		} catch(AuthenticationException e) {
			throw e;
		}
		
	}
	
	public Member findById(Long id) {
		Optional<Member> optMember = memberRepo.findById(id);
		if(optMember.isPresent()) {
			return optMember.get();
		} else {
			throw new EntityNotFoundException("멤버를 찾을 수 없습니다.");
		}
	}
	
	public Member findByUsername(String username) {
		Member m = memberRepo.findByUsername(username);
		if(m==null) {
			throw new EntityNotFoundException("멤버를 찾을 수 없습니다.");
		}
		return m;
	}
	
	public List<Member> findAll() {
		return memberRepo.findAll();
	}
	
	
	
	public MemberInfo getMemberInfoById(Long id) {
		Optional<Member> optMember = memberRepo.findById(id);
		if(optMember.isPresent()) {
			Member m = optMember.get();
			return fromMemberToMemberInfo(m);
		} else {
			throw new EntityNotFoundException("멤버를 찾을 수 없습니다.");
		}
	}
	
	public MemberInfo getMemberInfoByUsername(String username) {
		Member member = memberRepo.findByUsername(username);
		if(member == null) {
			throw new EntityNotFoundException("멤버를 찾을 수 없습니다.");
		}
		return fromMemberToMemberInfo(member);
	}
	
	public List<MemberInfo> getAllMemberInfo() {
		List<Member> allMembers = memberRepo.findAll();
		return allMembers.stream().map(this::fromMemberToMemberInfo).collect(Collectors.toList());
	}
	
	// 회원 탈퇴
	public void deleteById(Long id) {
		memberRepo.deleteById(id);
	}
	
	public void deleteAll() {
		memberRepo.deleteAll();
	}
	
	public MemberInfo fromMemberToMemberInfo(Member member) {
		return new MemberInfo(
				member.getLastName() + member.getFirstName(), 
				member.getAddress(), 
				member.getPhoneNumber(),
				member.getEmail(),
				member.getId());
	}
}
