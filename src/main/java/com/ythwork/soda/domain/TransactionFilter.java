package com.ythwork.soda.domain;

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
public class TransactionFilter {
	private Long memberId;
	private Long sendAcntId;
	private String from;
	private String to;
	private TransactionStatus status;
	private Long amount;
}
