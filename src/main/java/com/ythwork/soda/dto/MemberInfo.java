package com.ythwork.soda.dto;

import com.ythwork.soda.domain.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class MemberInfo {
	private String fullName;
	private Address address;
	private String phoneNumber;
	private String email;
	private Long memberId;
}
