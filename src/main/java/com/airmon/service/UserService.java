package com.airmon.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.airmon.dto.UserRegistrationDto;
import com.airmon.entity.Role;
import com.airmon.entity.User;
import com.airmon.repository.RoleRepository;
import com.airmon.repository.UserRepository;

@Service
public class UserService implements IUserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	/*
	 * @Autowired private BCryptPasswordEncoder passwordEncoder;
	 */

	@Override
	@Transactional
	public User findByUserName(String userName) {
		return userRepository.findByUsername(userName);
	}

	@Transactional
	public User register(UserRegistrationDto userRegistrationDto) {
		
		User user = new User();
		user.setFirstName(userRegistrationDto.getFirstName());
		user.setLastName(userRegistrationDto.getLastName());
		//user.setUsername(userRegistrationDto.getUsername());
		user.setUsername(userRegistrationDto.getEmail());
		user.setEmail(userRegistrationDto.getEmail());
		user.setPassword(userRegistrationDto.getPassword());
		
	
		List<Role> roles = new ArrayList<Role>();
		roles.add(roleRepository.findByRollName("ROLE_USER"));
		
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
