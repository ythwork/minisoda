package com.ythwork.soda.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class LoginResponse {
	private String jwt;
	private MemberInfo memberInfo;
}
