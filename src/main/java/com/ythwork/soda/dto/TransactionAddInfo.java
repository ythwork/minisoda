package com.ythwork.soda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionAddInfo {
	private Long memberId;
	private Long sendAcntId;
	private String recvcode;
	private String recvAcntNum;
	private Long amount;
}
