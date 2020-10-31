package com.ythwork.soda.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.MemberRepository;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFound;

@Service
@Transactional
public class MemberService {
	@Autowired
	private MemberRepository memberRepo;
	
	// 회원 등록
	public Long register(Member member) {
		if(alreadyMember(member)) {
			throw new EntityAlreadyExistsException(member.getLastName() + member.getFirstName() + " 님은 이미 가입한 회원입니다.");
		}
		memberRepo.save(member);
		return member.getId();
	}
	
	// 회원 중복 가입 방지
	private boolean alreadyMember(Member member) {
		Member m = memberRepo.findByEmail(member.getEmail());
		return m != null ? true : false;
	}
	
	// 회원 탈퇴
	public void quit(Member member) {
		memberRepo.delete(member);
	}
	
	public Member findById(Long id) {
		Optional<Member> optMember = memberRepo.findById(id);
		if(optMember.isPresent()) {
			return optMember.get();
		} else {
			throw new EntityNotFound("멤버를 찾을 수 없습니다.");
		}
	}
	
	public void deleteAll() {
		memberRepo.deleteAll();
	}
}
