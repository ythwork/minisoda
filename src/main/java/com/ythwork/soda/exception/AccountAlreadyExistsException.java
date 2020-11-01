package com.ythwork.soda.exception;

public class AccountAlreadyExistsException extends RuntimeException {
	public AccountAlreadyExistsException() {}
	public AccountAlreadyExistsException(String msg) {
		super(msg);
	}
	
	public AccountAlreadyExistsException(Throwable cause) {
		initCause(cause);
	}
	
	public AccountAlreadyExistsException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
