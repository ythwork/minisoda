package com.ythwork.soda.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ythwork.soda.dto.LoginResponse;
import com.ythwork.soda.web.MemberController;

@Component
public class JwtResponseAssembler implements RepresentationModelAssembler<LoginResponse, EntityModel<LoginResponse>> {

	@Override
	public EntityModel<LoginResponse> toModel(LoginResponse jwtResponse) {
		return EntityModel.of(jwtResponse, 
				linkTo(methodOn(MemberController.class).getMember(jwtResponse.getMemberInfo().getMemberId())).withRel("member"));
	}

}
