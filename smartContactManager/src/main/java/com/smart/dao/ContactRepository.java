package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entites.Contact;
import com.smart.entites.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	//pegination....
	
	@Query("from Contact as c where c.user.id =:userId")
	
	//cuttrntpage-page
	//contact per page-5
	public Page<Contact> findContactByUser(@Param("userId") int userid,Pageable pageable);
	
	//search
	public List<Contact> findByNameContainingAndUser(String name,User user);
}