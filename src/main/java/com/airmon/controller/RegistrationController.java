package com.airmon.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.airmon.dto.Constants;
import com.airmon.dto.ResponseDto;
import com.airmon.dto.UserRegistrationDto;
import com.airmon.entity.Role;
import com.airmon.entity.User;
import com.airmon.repository.RoleRepository;
import com.airmon.service.IUserService;

@RestController
public class RegistrationController {
	
	@Autowired
	IUserService userService;
	
	@Autowired
	RoleRepository roleRepository;
	
	
	@GetMapping("/hello")
	public ResponseEntity<?> hello() {
		
       return new ResponseEntity<ResponseDto>(new ResponseDto(Constants.SUCCESS_CODE , "Application Started" , false ,null), HttpStatus.OK);
        
		
	}
	
	
	@PostMapping(value ="/register")
	public ResponseEntity<?> register(@RequestBody UserRegistrationDto userRegistrationDto , BindingResult result) {
		
		
		User user = userService.findByUserName(userRegistrationDto.getUsername());
        if (user != null){
            result.rejectValue("email", null, "There is already an account registered with that email");
            return new ResponseEntity<ResponseDto>(new ResponseDto(Constants.SUCCESS_CODE , "There is already an account registered with that email" , false ,user), HttpStatus.OK);
        }

        if (result.hasErrors()){
        	return new ResponseEntity<ResponseDto>(new ResponseDto(Constants.SUCCESS_CODE , "Error" , false ,userRegistrationDto), HttpStatus.OK);
           // return "registration";
        }
        
       user = userService.register(userRegistrationDto);
       return new ResponseEntity<ResponseDto>(new ResponseDto(Constants.SUCCESS_CODE , "User Registered Successfully." , false ,user), HttpStatus.OK);
	
       
	}
	
	@PostConstruct
	public void insertingRoles() {
		
		roleRepository.save(new Role("ROLE_USER"));
		roleRepository.save(new Role("ROLE_ADMIN"));
		roleRepository.save(new Role("ROLE_SUPERADMIN"));
		
	}
}
