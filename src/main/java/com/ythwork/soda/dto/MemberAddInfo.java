package com.ythwork.soda.dto;

import java.util.Set;

import com.ythwork.soda.domain.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddInfo {
	private String firstName;
	private String lastName;
	private Address address;
	private String phoneNumber;
	private String email;
	
	// security
	private String username;
	private String password;
	
	Set<String> roles;
}
