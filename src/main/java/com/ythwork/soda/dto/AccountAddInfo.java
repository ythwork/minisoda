package com.ythwork.soda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountAddInfo {
	private Long memberId;
	private String code;
	private String accountNumber;
}
