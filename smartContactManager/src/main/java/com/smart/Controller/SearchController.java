package com.smart.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.userReposetory;
import com.smart.entites.Contact;
import com.smart.entites.User;

@RestController
public class SearchController {

	@Autowired
	private userReposetory userReposetory;
	@Autowired
	private ContactRepository ContactRepository;
	
	
	//search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query, Principal principal)
	{
		
		System.out.println(query);
		
		User user=this.userReposetory.getUserByUserName(principal.getName());
		List<Contact> contacts=this.ContactRepository.findByNameContainingAndUser(query, user);
		
		
		
		return ResponseEntity.ok(contacts);
	}
	
	
	
}
