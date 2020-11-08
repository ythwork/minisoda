package com.ythwork.soda.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ythwork.soda.data.MemberRepository;
import com.ythwork.soda.domain.Member;
import com.ythwork.soda.exception.EntityNotFoundException;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private MemberRepository memberRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Member member= memberRepo.findByUsername(username);
		if(member == null) {
			throw new EntityNotFoundException("멤버를 찾을 수 없습니다.");
		}
		
		return UserDetailsImpl.createUserDetails(member);
	}

}
