package com.ythwork.soda.web;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.ythwork.soda.exception.MemberAlreadyExistsException;
import com.ythwork.soda.exception.MemberNotFoundException;

@RestControllerAdvice
public class MemberExceptionAdvice {
	
	@ExceptionHandler(MemberAlreadyExistsException.class)
	@ResponseStatus(HttpStatus.CONFLICT)
	String memberAlreadyExistsHandler(MemberAlreadyExistsException e) {
		return e.getMessage();
	}
	
	@ExceptionHandler(MemberNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	String memberNotFoundHandler(MemberNotFoundException e) {
		return e.getMessage();
	}
}
