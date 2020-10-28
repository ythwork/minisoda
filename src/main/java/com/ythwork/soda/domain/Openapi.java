package com.ythwork.soda.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Openapi {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "openapi_id")
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	// JoinColumn은 외래키를 의미
	@JoinColumn(name = "bankcode_id")
	private Bankcode bankcode;
	
	@Column(name = "account_number")
	private String accountNumber;
	
	private String owner;
	
	// Long의 범위는 -9223372036854775808 ~ 9223372036854775807이다.
	// openapi 테이블의 balance 컬럼의 데이터 타입은 BIGINT UNSIGNED 0 ~ 18446744073709551615
	// JAVA에서 unsigned은 사용을 권장하지 않으므로 db에서 데이터를 로드할 때 데이터 손실이 생길 수 있다.
	// 다만 현실적으로 잔액이 Long의 상한을 넘기는 일은 일어나지 않을 것이므로 Long 타입을 사용한다.  
	private Long balance;
	
	
}
