package com.airmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.airmon.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>{

	 Role findByRollName(String rollName);
	
}
