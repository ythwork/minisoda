package com.ythwork.soda.exception;

public class JwtAuthenticationException extends RuntimeException {
	public JwtAuthenticationException() {}
	
	public JwtAuthenticationException(String msg) {
		super(msg);
	}
	
	public JwtAuthenticationException(Throwable cause) {
		initCause(cause);
	}
	
	public JwtAuthenticationException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
	
}
