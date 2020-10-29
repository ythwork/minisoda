package com.ythwork.soda.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Transaction {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	// 회원이 탈퇴했을 때 거래 내역이 남는 것은 옳지 않으므로
	// DRI에서 ON DELETE CASCADE로 튜플을 삭제한다.
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="member_id")
	private Member member;
	
	// 회원이 계좌를 삭제했더라도 우리 서비스에서 탈퇴하지 않았다면
	// 거래 내역을 남기는 것이 더 낫고 
	// 회원에게 거래 내역을 남길지 다 삭제할지를 결정하도록 해야 하므로
	// 외래키를 생성할 때 아무런 제약 조건을 걸지 않았다. 
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="send_account")
	private Openapi send;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="recv_account")
	private Openapi recv;
	
	// Long의 범위는 -9223372036854775808 ~ 9223372036854775807이다.
	// transaction 테이블의 amount 컬럼의 데이터 타입은 BIGINT UNSIGNED 0 ~ 18446744073709551615
	// Openapi.balance 필드와 같은 이유로 현실적으로 송금액을 고려했을 때 데이터 손실은 발생하지 않을 것이므로
	// Long을 그대로 사용한다.
	private Long amount;
	
	@Column(name = "after_balance")
	private Long afterBalance;
	
	@Column(name = "transaction_status")
	// 자바의 enum 타입을 사용할 수 있다. 이때 EnumType을 STRING으로 하면 데이터베이스에는
	// 문자 타입으로 전달된다. (CHAR 이나 VARCHAR)
	@Enumerated(EnumType.STRING)
	private TransactionStatus transactionStatus;
	
	// 기간 내 거래 내역 검색에 사용하므로 인덱스를 만들어 둔다.
	@Temporal(TemporalType.TIMESTAMP)
	private Date processAt;
	
	public Transaction() {
		// 거래가 시작되었다.
		this.transactionStatus = TransactionStatus.IN_PROCESS;
	}
	
	public void setMember(Member member) {
		this.member = member;
		if(!member.getTransactions().contains(this)) {
			member.getTransactions().add(this);
		}
	}
	
	// 거래 내역이 저장되기 전에 processAt을 생성한다.
	@PrePersist
	void processAt() {
		this.processAt = new Date();
	}
}
