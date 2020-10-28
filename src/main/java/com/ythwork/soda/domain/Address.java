package com.ythwork.soda.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Embeddable
public class Address {
	private String country;
	private String province;
	private String city;
	private String street;
	@Column(name="house_number")
	private String houseNumber;
}

