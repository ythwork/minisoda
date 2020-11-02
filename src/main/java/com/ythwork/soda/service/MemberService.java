package com.ythwork.soda.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.MemberRepository;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.exception.EntityAlreadyExistsException;
import com.ythwork.soda.exception.EntityNotFoundException;

@Service
@Transactional
public class MemberService {
	@Autowired
	private MemberRepository memberRepo;
	
	// 회원 등록
	public Member register(Member member) {
		if(alreadyMember(member)) {
			throw new EntityAlreadyExistsException(member.getLastName() + member.getFirstName() + " 님은 이미 가입한 회원입니다.");
		}
		return memberRepo.save(member);
	}
	
	// 회원 중복 가입 방지
	private boolean alreadyMember(Member member) {
		Member m = memberRepo.findByEmail(member.getEmail());
		return m != null ? true : false;
	}
	
	// 회원 탈퇴
	public void deleteById(Long id) {
		memberRepo.deleteById(id);
	}
	
	public Member findById(Long id) {
		Optional<Member> optMember = memberRepo.findById(id);
		if(optMember.isPresent()) {
			return optMember.get();
		} else {
			throw new EntityNotFoundException("멤버를 찾을 수 없습니다.");
		}
	}
	
	public List<Member> findAll() {
		List<Member> allMembers = memberRepo.findAll();
		return allMembers;
	}
	
	public void deleteAll() {
		memberRepo.deleteAll();
	}
}
