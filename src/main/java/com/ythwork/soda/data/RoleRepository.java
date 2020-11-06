package com.ythwork.soda.data;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ythwork.soda.domain.Role;
import com.ythwork.soda.domain.RoleType;

public interface RoleRepository extends JpaRepository<Role, Long> {
	Role findByRoleType(RoleType roleType);
}
