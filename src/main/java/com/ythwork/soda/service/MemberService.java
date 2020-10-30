package com.ythwork.soda.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.MemberRepository;
import com.ythwork.soda.domain.Member;

@Service
@Transactional
public class MemberService {
	@Autowired
	private MemberRepository memberRepo;
	
	public Long register(Member member) {
		if(alreadyMember(member)) {
			throw new IllegalStateException("이미 가입한 회원입니다.");
		}
		memberRepo.save(member);
		return member.getId();
	}
	
	private boolean alreadyMember(Member member) {
		Member m = memberRepo.findByEmail(member.getEmail());
		return m != null ? true : false;
	}
	
	
}
