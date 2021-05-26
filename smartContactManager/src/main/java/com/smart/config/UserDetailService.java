package com.smart.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.userReposetory;
import com.smart.entites.User;



public class UserDetailService implements UserDetailsService {

	@Autowired
	private userReposetory userReposetory;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		//fetcing user from database
		User user = userReposetory.getUserByUserName(username);
		if(user==null)
		{
			throw new UsernameNotFoundException("could not found user !!");
			
		}
		CustomUserDetails customUserDetails=new CustomUserDetails(user);
		
		
		return customUserDetails;
	}

}
