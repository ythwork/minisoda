package com.ythwork.soda.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@NamedQuery(
		name="Member.findByUsername",
		query="select m from Member m where m.auth.username = :username")
public class Member implements UserDetails {
	
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
	@OneToMany(mappedBy = "member", fetch=FetchType.EAGER)
	private Set<Account> accounts = new HashSet<>();
	
	@OneToMany(mappedBy = "member", fetch=FetchType.EAGER)
	private Set<Transaction> transactions = new HashSet<>();

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return auth.getRoles().stream().map(role -> {
			return new SimpleGrantedAuthority(role.toString());
		}).collect(Collectors.toList());
	}

	@Override
	public String getPassword() {
		return auth.getPassword();
	}

	@Override
	public String getUsername() {
		return auth.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
}
