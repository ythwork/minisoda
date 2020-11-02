package com.ythwork.soda.dto;

import java.util.Date;

import com.ythwork.soda.domain.TransactionStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class TransactionInfo {
	private Long memberId;
	private String sendcode;
	private String sendAcntNum;
	private String recvcode;
	private String recvAcntNum;
	private Long amount;
	private Long afterBalance;
	private TransactionStatus transactionStatus;
	private Date processAt;
	private Long transactionId;
}
