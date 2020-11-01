package com.ythwork.soda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class AccountInfo {
	private String owner;
	private String bankcode;
	private String accountNumber;
	private Long balance;
	private Long accountId;
}
