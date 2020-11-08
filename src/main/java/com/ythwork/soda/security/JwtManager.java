package com.ythwork.soda.security;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ythwork.soda.exception.JwtAuthenticationException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtManager {
	@Value("${jpa.jwt.secret}")
	private String secretKey;
	@Value("${jpa.jwt.expiration}")
	private Long expiration;
	
	public String getToken(Authentication authentication) {
		UserDetailsImpl principal = (UserDetailsImpl)authentication.getPrincipal();
		return Jwts.builder()
				.setSubject(principal.getUsername())
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + expiration))
				.signWith(SignatureAlgorithm.HS512, secretKey)
				.compact();
	}
	
	private String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}
	
	public String getUsername(HttpServletRequest request) {
		String jwt = getJwtFromRequestHeader(request);
		if(jwt==null) {
			throw new JwtAuthenticationException("요청 헤더에 JWT가 없습니다.");
		}
		
		return getUsername(jwt);
	}
	
	private void validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
		} catch(IllegalArgumentException e) {
			throw new JwtAuthenticationException("JWT 클레임에 페어가 없습니다.");
		} catch(ExpiredJwtException e) {
			throw new JwtAuthenticationException("토큰이 만료되었습니다.");
		} catch(UnsupportedJwtException e) {
			throw new JwtAuthenticationException("지원하지 않는 JWT입니다.");
		} catch(SignatureException e) {
			throw new JwtAuthenticationException("JWT 서명이 잘못되었습니다.");
		}
	}
	
	public void validateToken(HttpServletRequest request) {
		String jwt = getJwtFromRequestHeader(request);
		if(jwt == null) {
			throw new JwtAuthenticationException("요청 헤더에 JWT가 없습니다.");
		}
		
		try {
			validateToken(jwt);
		} catch(JwtAuthenticationException e) {
			throw e;
		}
	}
	
	public String getJwtFromRequestHeader(HttpServletRequest request) {
		// jwt 토큰이 있다면 헤더에서 받아온다.
		// 없으면 request.getHeader()는 null을 반환한다.
		String tokenHeader = request.getHeader("Authorization");		
		String token = null;
		if(tokenHeader!= null && tokenHeader.startsWith("Bearer")) {
			token = tokenHeader.substring(7);
		}
		return token;		
	}
	
}
