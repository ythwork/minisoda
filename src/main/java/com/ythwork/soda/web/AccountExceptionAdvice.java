package com.ythwork.soda.web;

import javax.security.auth.login.AccountNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ythwork.soda.exception.AccountAlreadyExistsException;

@RestControllerAdvice
public class AccountExceptionAdvice {
	@ExceptionHandler(AccountAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String accountAlreadyExistsHandler(AccountAlreadyExistsException e) {
		return e.getMessage();
	}
	
	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String accountNotFoundHandler(AccountNotFoundException e) {
		return e.getMessage();
	}
}
