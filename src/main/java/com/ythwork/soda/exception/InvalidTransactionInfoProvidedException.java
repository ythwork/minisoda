package com.ythwork.soda.exception;

public class InvalidTransactionInfoProvidedException extends RuntimeException {
	public InvalidTransactionInfoProvidedException() {}
	public InvalidTransactionInfoProvidedException(String msg) {
		super(msg);
	}
	
	public InvalidTransactionInfoProvidedException(Throwable cause) {
		initCause(cause);
	}
	
	public InvalidTransactionInfoProvidedException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
