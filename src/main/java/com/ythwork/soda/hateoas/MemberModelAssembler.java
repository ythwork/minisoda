package com.ythwork.soda.hateoas;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.ythwork.soda.dto.MemberInfo;
import com.ythwork.soda.web.MemberController;

@Component
public class MemberModelAssembler implements RepresentationModelAssembler<MemberInfo, EntityModel<MemberInfo>> {

	@Override
	public EntityModel<MemberInfo> toModel(MemberInfo memberInfo) {
		return EntityModel.of(memberInfo, 
				linkTo(methodOn(MemberController.class).getMember(memberInfo.getMemberId(), null)).withSelfRel());
	}

}
