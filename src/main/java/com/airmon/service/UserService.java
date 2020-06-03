package com.airmon.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	ErrorRepository errorRepository;

	
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

			if (user != userRepository.findByUsername(userRegistrationDto.getUsername())) {
				UserRegistrationError userRegistrationError = new UserRegistrationError(
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

					UserRegistrationError userRegistrationError = new UserRegistrationError(
							userRegistrationDto.getUsername(), userRegistrationDto.getPassword(),
							userRegistrationDto.getFirstName(), userRegistrationDto.getLastName(),
							userRegistrationDto.getEmail(), e.getMessage());

					errorRepository.save(userRegistrationError);
					userInsertionFailed++;

				}
			} // else
		} // for
		return new ResponseEntity<ResponseBulkDto>(new ResponseBulkDto(totalUserParsed, userInsertionFailed),
				HttpStatus.OK);
	}

	@Transactional
	public User register(UserRegistrationDto userRegistrationDto) {

		User user = new User();
		user.setFirstName(userRegistrationDto.getFirstName());
		user.setLastName(userRegistrationDto.getLastName());
		// user.setUsername(userRegistrationDto.getUsername());
		user.setUsername(userRegistrationDto.getEmail());
		user.setEmail(userRegistrationDto.getEmail());
		user.setPassword(userRegistrationDto.getPassword());

		List<Role> roles = new ArrayList<Role>();
		roles.add(roleRepository.findByRollName("ROLL_USER"));
		user.setRoles(roles);

		return userRepository.save(user);

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
