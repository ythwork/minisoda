package com.ythwork.soda.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ythwork.soda.domain.Member;
import com.ythwork.soda.exception.JwtAuthenticationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtManager jwtManager;
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			jwtManager.validateToken(request);
			// 검증된 멤버라면 authentication을 만들어서 
			// 시큐리티 컨텍스트에 대입한다. 
			String username = jwtManager.getUsername(request);
			Member member = (Member)userDetailsService.loadUserByUsername(username);
			
			// Authentication implementation
			UsernamePasswordAuthenticationToken authentication = 
					new UsernamePasswordAuthenticationToken(
							// Member 객체를 Authentication 객체에 담아둔 후
							// Principal 객체로 받아 사용한다.
							// @AuthenticationPrincipal 애너테이션으로 컨트롤러 메서드에서 받아 사용할 수 있다.
							member, null, member.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
		
		} catch(JwtAuthenticationException e) {
			log.error(e.getMessage());
		}
		
		filterChain.doFilter(request, response);
		
	}

}
