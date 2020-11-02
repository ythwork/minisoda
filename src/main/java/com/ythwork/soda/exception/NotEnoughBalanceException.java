package com.ythwork.soda.exception;

public class NotEnoughBalanceException extends RuntimeException {
	public NotEnoughBalanceException(String msg) {
		super(msg);
	}
}
