package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import net.javaguides.springboot.service.UserService;
import net.javaguides.springboot.web.dto.UserRegistrationDto;

import java.util.Map;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

	private UserService userService;

	@Autowired
	private UserRepository userRepo;

	public UserRegistrationController(UserService userService) {
		super();
		this.userService = userService;
	}
	
	@ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }
	
	@GetMapping
	public String showRegistrationForm() {
		return "registration";
	}
	
	@PostMapping
	public String registerUserAccount(@ModelAttribute("user")
												  UserRegistrationDto registrationDto,
									  Model model) {
		User userFromDb = userRepo.findByEmail(registrationDto.getEmail());

		if (userFromDb != null) {

			model.addAttribute("message", "User with this email exists!");
			return "registration";

		}
		userService.save(registrationDto);
		return "redirect:/registration?success";
	}
}
