package com.ythwork.soda.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Embeddable
public class Auth {
	@NotNull
	private String username;
	@NotNull
	private String password;
	// 연관 관계 객체 참조 추가는 언제 해야 하는가?
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="member_role",
	joinColumns=@JoinColumn(name="member_id"),
	inverseJoinColumns=@JoinColumn(name="role_id"))
	private Set<Role> roles = new HashSet<>();
}
