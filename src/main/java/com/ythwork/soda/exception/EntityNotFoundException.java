package com.ythwork.soda.exception;

public class EntityNotFoundException extends RuntimeException {
	public EntityNotFoundException() {}
	public EntityNotFoundException(String msg) {
		super(msg);
	}
	
	public EntityNotFoundException(Throwable cause) {
		initCause(cause);
	}
	
	public EntityNotFoundException(String msg, Throwable cause) {
		super(msg);
		initCause(cause);
	}
}
