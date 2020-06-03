package com.airmon.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.airmon.dto.UserRegistrationDto;
import com.airmon.entity.User;

public interface IUserService extends UserDetailsService {

    User findByUserName(String userName);

	User register(UserRegistrationDto userRegistrationDto);

    
}
