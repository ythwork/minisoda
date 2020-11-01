package com.ythwork.soda.exception;

public class MemberNotFoundException extends RuntimeException {
	public MemberNotFoundException() {}
	public MemberNotFoundException(String msg) {
		super(msg);
	}
	
	public MemberNotFoundException(Throwable cause) {
		initCause(cause);
	}
	
	public MemberNotFoundException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
