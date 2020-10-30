package com.ythwork.soda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AccountInfo {
	String owner;
	String bankcode;
	String accountNumber;
	Long balance;
	Long accountId;
}
