package com.ythwork.soda.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ythwork.soda.domain.Member;
import com.ythwork.soda.web.MemberController;

@Component
public class MemberModelAssembler implements RepresentationModelAssembler<Member, EntityModel<Member>> {

	@Override
	public EntityModel<Member> toModel(Member member) {
		return EntityModel.of(member, 
				linkTo(methodOn(MemberController.class).getMember(member.getId())).withSelfRel());
	}

}
