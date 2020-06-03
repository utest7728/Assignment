package com.airmon.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.airmon.entity.UserRegistrationError;

@Repository
public interface ErrorRepository extends JpaRepository<UserRegistrationError, Long>{


    
}
