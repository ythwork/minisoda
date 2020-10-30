package com.ythwork.soda.exception;

public class TransactionFailureException extends RuntimeException {
	public TransactionFailureException(String msg) {
		super(msg);
	}
}
