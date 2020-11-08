package com.ythwork.soda.security;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.ythwork.soda.exception.JwtAuthenticationException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

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
	
	public String getUsername(String token) {
		return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
	}
	
	public void validateToken(String token) {
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
	
}
