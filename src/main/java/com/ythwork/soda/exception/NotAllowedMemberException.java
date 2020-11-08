package com.ythwork.soda.exception;

public class NotAllowedMemberException extends RuntimeException {
	public NotAllowedMemberException() {}
	public NotAllowedMemberException(String msg) {
		super(msg);
	}
	
	public NotAllowedMemberException(Throwable cause) {
		initCause(cause);
	}
	
	public NotAllowedMemberException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
