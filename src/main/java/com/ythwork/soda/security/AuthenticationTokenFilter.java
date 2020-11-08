package com.ythwork.soda.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ythwork.soda.exception.JwtAuthenticationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthenticationTokenFilter extends OncePerRequestFilter {
	@Autowired
	private JwtManager jwtManager;
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// jwt 토큰이 있다면 헤더에서 받아온다.
		// 없으면 request.getHeader()는 null을 반환한다.
		String tokenHeader = request.getHeader("Authorization");
		String token = null;
		if(tokenHeader!= null && tokenHeader.startsWith("Bearer")) {
			token = tokenHeader.substring(7);
			try {
				// jwt 검증
				jwtManager.validateToken(token);
				
				// 검증된 멤버라면 authentication을 만들어서 
				// 시큐리티 컨텍스트에 대입한다. 
				String username = jwtManager.getUsername(token);
				UserDetails details = userDetailsService.loadUserByUsername(username);
				
				// Authentication implementation
				UsernamePasswordAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(
								details, null, details.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				
				SecurityContextHolder.getContext().setAuthentication(authentication);
			
			} catch(JwtAuthenticationException e) {
				log.error("요청 헤더의 JWT는 유효하지 않습니다.");
			}
		}
		
		filterChain.doFilter(request, response);
		
	}

}
