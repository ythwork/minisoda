package com.ythwork.soda.exception;

public class TransactionNotFoundException extends RuntimeException {
	public TransactionNotFoundException() {}
	public TransactionNotFoundException(String msg) {
		super(msg);
	}
	
	public TransactionNotFoundException(Throwable cause) {
		initCause(cause);
	}
	
	public TransactionNotFoundException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
