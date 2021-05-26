package com.smart.Controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.userReposetory;
import com.smart.entites.Contact;
import com.smart.entites.User;
import com.smart.helper.Message;

@Controller
public class homeController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
	private userReposetory userReposetory;
	
	@RequestMapping("/")
	public String home(Model model) {
		model.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about(Model model) {
		model.addAttribute("title", "About - Smart Contact Manager");
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) 
	{
		model.addAttribute("title", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	
	
	
	
	//this is for register
	@RequestMapping(value = "do_register",method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result1,@RequestParam(value = "agreement",defaultValue = "false")
	Boolean agreement,Model model ,HttpSession session)
	{
		try {
			
			if(!agreement) {
				System.out.println("you have not agreed term and condition");
				throw new Exception("you have not agreed term and condition");
			}
			
			if (result1.hasErrors()) {
				System.out.println("ERROR"+result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement :"+agreement);
			System.out.println("User"+user);
			
			User result = this.userReposetory.save(user);
			
			
			
			
			model.addAttribute("user", new User());
			session.setAttribute("message",new Message("Sucsessfully registered !!", "alert-success"));
			return "signup";
			
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user",user);
			session.setAttribute("message",new Message("something went worng !!"+e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
	
	@GetMapping("/signin")
	public String customLogin(Model model)
	{
		model.addAttribute("Title", "Login Page");
		return "login";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
