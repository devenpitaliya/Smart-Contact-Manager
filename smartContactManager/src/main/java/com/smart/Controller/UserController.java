package com.smart.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.userReposetory;
import com.smart.entites.Contact;
import com.smart.entites.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Autowired
	private userReposetory userReposetory;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//method for adding common data for responce
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) 
	{
		
		String username = principal.getName();
		System.out.println("USERNAME:"+username);
		
		User user = this.userReposetory.getUserByUserName(username);
	
		
		System.out.println("user:"+user.getName());
		
		model.addAttribute("user", user);
		
		model.addAttribute("title", "User Dashboard");
		
		
		
	}
	
	
	//home dashboard
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal) 
	{
		return "normal/user_dashboard";
	}
	
	//open addform handler
	
	@GetMapping("/add_contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact",new Contact());
		return "normal/add_contact_form";
	}
	
	//processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage")MultipartFile file, Principal principal,HttpSession session)
	{
		
		try {
		
		
		String name=principal.getName();
		User user = this.userReposetory.getUserByUserName(name);
		
		/*
		 * if(3>2) { throw new Exception(); }
		 */
		
		//processing and uploading file..
		if(file.isEmpty())
		{
		  //msg if empty
			
			System.out.println("file is empty");
			contact.setImage("contact.png");
			
		}else
		{
			//upload the file in folder and update the name of file
			contact.setImage(file.getOriginalFilename());
			
			File saveFile = new ClassPathResource("static/img").getFile();
			
			Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
			Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			System.out.println("image is uploaded");
		}
			
		
		
		
		user.getContacts().add(contact);
		contact.setUser(user);
		
			
		this.userReposetory.save(user);
		
		System.out.println("data:" +contact);
		System.out.println("Added to data base");
		
		//msg success coz this is success
		session.setAttribute("message", new Message("Your Contact is added !! Add more....", "success"));
		
		}catch (Exception e)
		{
			System.out.println("ERROR"+e.getMessage());
			e.printStackTrace();
			//msg error coz this is error
			session.setAttribute("message", new Message("Something went wrong...!! try again", "danger"));
			
		}
		
		return"normal/add_contact_form";
	}
	//show contact handler
	//per page 5[n]
	//current page=0[page]
	@GetMapping("/show_contacts/{page}")
	public String showContact(@PathVariable("page") Integer page, Model m,Principal principal) {
		
		m.addAttribute("title", "Show User Contacts");
		//user contacts list
		
		String userName = principal.getName();
		
		User user = this.userReposetory.getUserByUserName(userName);
		
		//cuttrntpage-page
		//contact per page-5
		Pageable pageable = PageRequest.of(page, 10);
		
	Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(),pageable);
		
	m.addAttribute("contacts", contacts);
	m.addAttribute("currentPage", page);
	m.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	}
	
	//showing particular contact details
	
	@RequestMapping("/{cid}/contact")
	public String showContactDetail(@PathVariable("cid") Integer cid,Model model,Principal principal)
	{
		System.out.println("CID:"+cid);
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cid);
	Contact contact = contactOptional.get();
	
	//
	String userName = principal.getName();
	User user = this.userReposetory.getUserByUserName(userName);
	
	if(user.getId()==contact.getUser().getId())
	{
		
	
		model.addAttribute("contact", contact);
		model.addAttribute("title", contact.getName());
	}
		
		
		
		return "normal/contact_detail";
	}
	
	//delete contact handler
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,Model model,Principal principal,HttpSession session)
	{
		
		
		System.out.println("CID:"+cid);
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		//check...
		String userName = principal.getName();
		User user = this.userReposetory.getUserByUserName(userName);
		
		if(user.getId()==contact.getUser().getId())
		{
			
          this.contactRepository.delete(contact);
			
			System.out.println("DELETED");
			
			session.setAttribute("message", new Message("Contact Deleted Successfully", "success"));
	
			
		}
		return "redirect:/user/show_contacts/0";
	}
	
	
	//open update form
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid")Integer cid, Model m)
	{
		m.addAttribute("title", "update contact");
		
		Contact contact = this.contactRepository.findById(cid).get();
		
		m.addAttribute("contact", contact);
		
		
		return "normal/update_form";
	}
	
	//update contact handler
	@RequestMapping(value = "/process-update",method = RequestMethod.POST)
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,Model m,HttpSession session,Principal principal )
	{
		try {
			//old contact photo fetch
			Contact oldContactDetails = this.contactRepository.findById(contact.getCid()).get();
			
			
			if(!file.isEmpty())
			{
				
				//delete old photo
				
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1=new File(deleteFile,oldContactDetails.getImage());
				file1.delete();
				
				
				
				//update new photo
				
				contact.setImage(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
			
				
				Files.copy(file.getInputStream(),path , StandardCopyOption.REPLACE_EXISTING);
			
				
				
				contact.setImage(file.getOriginalFilename());
				
				
				
				
			}else
			{
				contact.setImage(oldContactDetails.getImage());
			}
			
			User user=this.userReposetory.getUserByUserName(principal.getName());
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated", "success"));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		System.out.println("contact name:"+contact.getName());
		System.out.println("contact id:"+contact.getCid());
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	//your profile handler
	
	@GetMapping("/profile")
	public String yourProfile(Model model)
	{
		model.addAttribute("title", "Profile page");
		return "normal/profile";
	}
	
	//open setting handler
	
	@GetMapping("/settings")
	public String openSetting() {
		
		return "normal/settings";
	}
	
	//change password handler
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPaswword,Principal principal,HttpSession session) 
	{
		System.out.println("OLD PASSWORD:"+oldPassword);
		System.out.println("NEW PASSWORD:"+newPaswword);
		
		String userName = principal.getName();
		User currentUser = this.userReposetory.getUserByUserName(userName);
		System.out.println(currentUser.getPassword());
		
		
	if(this.bCryptPasswordEncoder.matches(oldPassword, currentUser.getPassword())) 
	{
		//change
		currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPaswword));
		this.userReposetory.save(currentUser);
		session.setAttribute("message", new Message("Your Password is successfully changed", "success"));
		
		
	}else {
		//error
		session.setAttribute("message", new Message("Please enter correct old passsword !!", "danger"));
		return "redirect:/user/settings";
	}
		
		
		
		
		
		return "redirect:/user/index";
	}
	
	//user update
	
	
	
}













































