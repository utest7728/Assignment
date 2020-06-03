package com.airmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airmon.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

	@Query("SELECT u FROM User u  WHERE u.username = :username")
    User findByUsername(@Param("username") String username);
	
	@Query("SELECT u FROM User u  WHERE u.email = :email")
    User findByEmail(@Param("email") String email);
}
