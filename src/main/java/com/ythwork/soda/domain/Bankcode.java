package com.ythwork.soda.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class Bankcode {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="bankcode_id")
	private Long id;
	
	private String code;
	
	@OneToMany(mappedBy = "bankcode")
	private List<Openapi> openapis = new ArrayList<>();
	
}
