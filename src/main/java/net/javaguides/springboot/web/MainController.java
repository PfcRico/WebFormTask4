package net.javaguides.springboot.web;

import net.javaguides.springboot.model.User;
import net.javaguides.springboot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Controller
public class MainController {

	@Autowired
	private UserRepository userRepo;

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/index")
	public String home() {
		if(checkUser()){
			return "login";
		}
		return "index";
	}

	@GetMapping("/")
	public String showTable(Model model) {
		if(checkUser()){
			return "login";
		}
		String username = getCurrentUser().getName();
		userRepo.findByEmail(username).setLastOnline(new Date());

		Iterable<User> users = userRepo.findAll();
		model.addAttribute("users", users);
		return "index";
	}

	@PostMapping("/receive")
	private String receiveValues(Model model) {
		Iterable<User> users = userRepo.findAll();
		for (User us : users) {
			us.changeSelected();
			userRepo.save(us);
		}

		model.addAttribute("users", users);
		return "index";
	}

	@PostMapping("/block")
	private String block(Model model) {
		Iterable<User> users = userRepo.findAll();
		for (User us : users) {
			if (us.isSelected()) {
				us.setActive(false);
				userRepo.save(us);
				if (us.getEmail().equals(getCurrentUser().getName())) {
					SecurityContextHolder.getContext().
							getAuthentication().
							setAuthenticated(false);
					users = userRepo.findAll();
					model.addAttribute("users", users);
					return "login";
				}
			}
		}

		model.addAttribute("users", users);
		return "index";
	}

	@PostMapping("/unblock")
	private String unblock(Model model) {
		Iterable<User> users = userRepo.findAll();
		for (User us : users) {
			if (us.isSelected()) {
				us.setActive(true);
				userRepo.save(us);
			}
		}
		model.addAttribute("users", users);
		return "index";
	}

	@PostMapping("/delete")
	private String delete(Model model) {
		Iterable<User> users = userRepo.findAll();
		for (User us : users) {
			if (us.isSelected()) {
				userRepo.delete(us);
				if (us.getEmail().equals(getCurrentUser().getName())) {
					SecurityContextHolder.getContext().
							getAuthentication().
							setAuthenticated(false);
					users = userRepo.findAll();
					model.addAttribute("users", users);
					return "login";
				}
			}
		}


		users = userRepo.findAll();
		model.addAttribute("users", users);
		return "index";
	}

	@PostMapping("/select")
	private String select(HttpServletRequest request, Model model) {
		String email = request.getParameter("email");
		Iterable<User> users = userRepo.findAll();
		for (User us : users) {
			if (us.getEmail().equals(email)) {
				us.changeSelected();
				userRepo.save(us);
			}
		}

		model.addAttribute("users", users);
		return "index";
	}

	private Authentication getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication;
	}

	private boolean checkUser() {
		String username = getCurrentUser().getName();
		if (userRepo.findByEmail(username).isActive() == false) {
			SecurityContextHolder.getContext().
					getAuthentication().
					setAuthenticated(false);
			return true;
		}
		return false;
	}
}
