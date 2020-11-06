package com.ythwork.soda.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Member {
	
	@Id
	// entitymanager.persist() 호출할 때 SQL이 데이터베이스에 전달되므로
	// 트랜잭션 지원 쓰기 지연(lazy)이 불가능
	// 또한 엔티티에 id 값을 할당하려면 데이터베이스를 한번 더 조회해야 한다.
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="member_id")
	private Long id;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Embedded
	private Address address;
	
	@Embedded
	private Auth auth;
	
	@Column(name="phone_number")
	private String phoneNumber;
	
	private String email;
	
	// mappedBy는 매핑 클래스의 필드 이름
	@OneToMany(mappedBy = "member", fetch=FetchType.LAZY)
	private List<Account> accounts = new ArrayList<>();
	
	@OneToMany(mappedBy = "member", fetch=FetchType.LAZY)
	private List<Transaction> transactions = new ArrayList<>();
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name="member_roles",
			joinColumns=@JoinColumn(name="member_id"),
			inverseJoinColumns=@JoinColumn(name="role_id"))
	private Set<Role> roles = new HashSet<>();
	
}
