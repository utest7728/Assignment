package com.airmon.service;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.airmon.dto.UserRegistrationDto;
import com.airmon.entity.User;

public interface IUserService extends UserDetailsService {

    User findByUserName(String userName);

	ResponseEntity<?> bulkRegister(List<UserRegistrationDto> userRegistrationDtoList);
	
	void exportCSV(HttpServletResponse response) throws Exception;
}
