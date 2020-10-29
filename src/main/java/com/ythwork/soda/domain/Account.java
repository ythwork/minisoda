package com.ythwork.soda.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Account {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id")
	private Long id;
	
	
	// 테이블을 구성할 때 논리적으로 멤버가 탈퇴했을 때 멤버의 계좌 정보도 지우는 것이 옮으므
	// 외래키를 선언할 때 ON DELETE CASCADE를 명시한다. DRI(선언적 참조 무결성)
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	// 테이블 구성 시 ON DELETE CASCADE 명시
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "openapi_id")
	private Openapi openapi;
	
}
