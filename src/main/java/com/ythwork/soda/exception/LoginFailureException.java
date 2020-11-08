package com.ythwork.soda.exception;

public class LoginFailureException extends RuntimeException {
	public LoginFailureException() {}
	public LoginFailureException(String msg) {
		super(msg);
	}
	
	public LoginFailureException(Throwable cause) {
		initCause(cause);
	}
	
	public LoginFailureException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
