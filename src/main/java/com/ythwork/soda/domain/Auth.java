package com.ythwork.soda.domain;

import java.util.Collection;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;

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
	
	private Collection<? extends GrantedAuthority> authorities;
}
