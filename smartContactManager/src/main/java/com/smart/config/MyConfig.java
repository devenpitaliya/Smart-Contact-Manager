package com.smart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class MyConfig extends WebSecurityConfigurerAdapter{

	@Bean 
	public UserDetailService getUserDetailService() {
		
		return new UserDetailService();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder()
	{
		return new  BCryptPasswordEncoder();
	}
	
	public DaoAuthenticationProvider authenticationProvider()
	{
	DaoAuthenticationProvider daoAuthenticationProvider = new	DaoAuthenticationProvider();
	daoAuthenticationProvider.setUserDetailsService(this.getUserDetailService());
	daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
	
	return daoAuthenticationProvider;
	
	}
	
	////configure method...
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
	
		auth.authenticationProvider(authenticationProvider());
		
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/admin/**").hasRole("ADMIN")
		.antMatchers("/user/**").hasRole("USER").antMatchers("/**")
		.permitAll().and().formLogin()
		.loginPage("/signin")
		.loginProcessingUrl("/dologin")
		.defaultSuccessUrl("/user/index")
		//.failureUrl("/login-fail") we can create this page
		.and()
		.csrf().disable();
	}
	
  
	
}



//loginPage()-the custom login page
//loginProcessingUrl()-the url to submit username and password
//defauktSuccessUrl()-the landing page after the successful login
//failureUrl-the landing page after unsuccessful login













