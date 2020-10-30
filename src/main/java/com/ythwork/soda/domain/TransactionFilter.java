package com.ythwork.soda.domain;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionFilter {
	private Long memberId;
	private Long sendAcntId;
	private Date from;
	private Date to;
	private TransactionStatus status;
	private Long amount;
}
