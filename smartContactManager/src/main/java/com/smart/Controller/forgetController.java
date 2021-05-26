package com.smart.Controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.Service.emailService;
import com.smart.dao.userReposetory;
import com.smart.entites.User;

@Controller
public class forgetController {

	Random random = new Random(1000);
	
	@Autowired
	private emailService emailService;
	
	@Autowired
	private userReposetory userReposetory;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//open email id handler
	
	@RequestMapping("/forget")
	public String openEmailForm()
	{
		return "forget_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email")String email,HttpSession session)
	{
		System.out.println("EMAIL:"+email);
		
		
		//generating otp of 4 digit
		
		
		
		int otp = random.nextInt(9999);
		
		System.out.println("OTP:"+otp);
		
		//write code for send otp to email
		
		String subject="OTP FROM SMART CONTACT MANAGER";
		String message=""
				+ "<div style='border:1px solid #e2e2e2; padding:20px'>"
				+ "<h1>"
				+ "OTP is "
				+ "<b>"+otp
				
				+ "</b>"
				+ "</h1>"
				+ "</div>";
		String to=email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
			
		}else
		{
			session.setAttribute("message", "check your email id !!");
			
			
			return "forget_email_form";
		}
		
	}
	
	
	//verify-otp
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session)
	{
		
		int myotp=(int) session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		if(myotp==otp)
		{
			//password change
			
			
			User user = this.userReposetory.getUserByUserName(email);
			
			if(user==null)
			{
				//error msg
				session.setAttribute("message", "User does not exits with this email !!");
				
				
				return "forget_email_form";
			}
			else
			{
				//send change password form
			}
				
			
			
			
			return "password_change_form";
		}else
		{
			session.setAttribute("message", "You have entered wrong OTP");
			return "verify_otp";
		}
		
	}
	
	//change password
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newpassword")String newpassword,HttpSession session)
	{
	
		String email=(String)session.getAttribute("email");
		User user = this.userReposetory.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userReposetory.save(user);
		return "redirect:/signin?change=password changed successfully";
	}
	
	
}


















