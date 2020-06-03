package com.airmon.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airmon.dto.ResponseBulkDto;
import com.airmon.dto.UserRegistrationDto;
import com.airmon.entity.Role;
import com.airmon.entity.User;
import com.airmon.entity.UserRegistrationError;
import com.airmon.repository.ErrorRepository;
import com.airmon.repository.RoleRepository;
import com.airmon.repository.UserRepository;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private ErrorRepository errorRepository;
	
	
	@Override
	@Transactional
	public User findByUserName(String userName) {
		return userRepository.findByUsername(userName);
	}

	@Transactional
	public ResponseEntity<?> bulkRegister(List<UserRegistrationDto> userRegistrationDtoList) {

		int totalUserParsed = userRegistrationDtoList.size();
		int userInsertionFailed = 0;
		int userInsertionSuccess = 0;
		User user = null;

		for (UserRegistrationDto userRegistrationDto : userRegistrationDtoList) {
			UserRegistrationError userRegistrationError= null;
			user= userRepository.findByUsername(userRegistrationDto.getUsername());
			if (user != null) {
				 userRegistrationError = new UserRegistrationError(
						userRegistrationDto.getUsername(), userRegistrationDto.getPassword(),
						userRegistrationDto.getFirstName(), userRegistrationDto.getLastName(),
						userRegistrationDto.getEmail(), "User Already exists");

				errorRepository.save(userRegistrationError);
				userInsertionFailed++;
			} else {

				user = new User();
				user.setFirstName(userRegistrationDto.getFirstName());
				user.setLastName(userRegistrationDto.getLastName());
				user.setUsername(userRegistrationDto.getUsername());
				// user.setUsername(userRegistrationDto.getEmail());
				user.setEmail(userRegistrationDto.getEmail());
				user.setPassword(userRegistrationDto.getPassword());
				
				if(!userRegistrationDto.getPassword().equals( userRegistrationDto.getConfirmPassword())) {
					userRegistrationError = new UserRegistrationError(
							userRegistrationDto.getUsername(), userRegistrationDto.getPassword(),
							userRegistrationDto.getFirstName(), userRegistrationDto.getLastName(),
							userRegistrationDto.getEmail(), "Password and Confirm Password didn't Match");

					errorRepository.save(userRegistrationError);
					userInsertionFailed++;
					continue;
				}
					
				
				String emailRegex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
				if(!userRegistrationDto.getEmail().matches(emailRegex)) {
					userRegistrationError = new UserRegistrationError(
							userRegistrationDto.getUsername(), userRegistrationDto.getPassword(),
							userRegistrationDto.getFirstName(), userRegistrationDto.getLastName(),
							userRegistrationDto.getEmail(), "Email is Invalid");

					errorRepository.save(userRegistrationError);
					userInsertionFailed++;
					continue;
					
				}
				

				List<Role> roles = new ArrayList<Role>();

				String[] rolesList = userRegistrationDto.getRoles().split("|");
				for (String roleName : rolesList) {
					roles.add(roleRepository.findByRollName(roleName));
				}

				user.setRoles(roles);

				try {
					userRepository.save(user);
					userInsertionSuccess++;
				} catch (Exception e) {

					 userRegistrationError = new UserRegistrationError(
							userRegistrationDto.getUsername(), userRegistrationDto.getPassword(),
							userRegistrationDto.getFirstName(), userRegistrationDto.getLastName(),
							userRegistrationDto.getEmail(), e.getMessage());

					errorRepository.save(userRegistrationError);
					userInsertionFailed++;

				}
			} // else
		} // for
		return new ResponseEntity<ResponseBulkDto>(new ResponseBulkDto(totalUserParsed, userInsertionFailed,
				"RegistrationErorrs.csv"),
				HttpStatus.OK);
	}

	@Override
	public void exportCSV(HttpServletResponse response) throws Exception {

        String filename = "error.csv";

        response.setContentType("text/csv");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + filename + "\"");

        StatefulBeanToCsv<UserRegistrationError> writer = new StatefulBeanToCsvBuilder<UserRegistrationError>(response.getWriter())
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(false)
                .build();

        writer.write(errorRepository.findAll());
                
    }

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}

	
}
