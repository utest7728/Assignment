
package com.airmon.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.airmon.service.IUserService;

@Configuration

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private IUserService userService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable()
		.authorizeRequests()
			.antMatchers("/","/register","/hello").permitAll()
			.
			// all other requests need to be authenticated
			anyRequest().authenticated();

	}

       
	// beans // bcrypt bean definition
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// authenticationProvider bean definition

	@Bean 
	public DaoAuthenticationProvider authenticationProvider() {
	  DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
	  auth.setUserDetailsService(userService); // set the custom user detailsservice 
	  auth.setPasswordEncoder(passwordEncoder()); // set the passwordencoder - bcrypt 
	  return auth; 
	}

}
