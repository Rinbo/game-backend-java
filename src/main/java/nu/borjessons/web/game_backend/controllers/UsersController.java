package nu.borjessons.web.game_backend.controllers;

import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nu.borjessons.web.game_backend.helpers.Token;
import nu.borjessons.web.game_backend.models.PrunedUser;
import nu.borjessons.web.game_backend.models.User;
import nu.borjessons.web.game_backend.models.UserRepository;


@CrossOrigin
@RestController
@RequestMapping(path="/users") 
public class UsersController {
	@Autowired 
	          
	private UserRepository userRepository;
	
	@PostMapping(path="/add") 
	public @ResponseBody ResponseEntity<User> addNewUser (@Valid @RequestBody User user)
			 {
		User n = new User();
		try {
			n.setName(user.getName().trim());
			n.setEmail(user.getEmail().trim());
			n.setPassword(user.getPassword().trim());
			Token tokenClass = new Token();
			String token = tokenClass.generateToken(20);
			n.setToken(token);
			userRepository.save(n);
			return new ResponseEntity<User>(n, HttpStatus.OK);		
		} catch (Exception e) {
			return new ResponseEntity<User>(user, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(path="/signin")
	public @ResponseBody ResponseEntity<User> signInUser (@Valid @RequestBody User user) {
		// Check if User exists
		try {
			userRepository.findByEmail(user.getEmail());
		} catch (Exception e) {
			return new ResponseEntity<User>(user, HttpStatus.NOT_FOUND);
		}
		
		User n = userRepository.findByEmail(user.getEmail());
		// Check if password matches what is stored in database
		if (n.checkPassword(user.getPassword().trim())) {
			n.setToken(new Token().generateToken(20));
			return new ResponseEntity<User>(n, HttpStatus.OK);
		} else {
			return new ResponseEntity<User>(user, HttpStatus.UNAUTHORIZED);
		}		
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<PrunedUser> getAllUsers() {
		// Get the users and send them back but remove password and token first
		Iterable<User> userArray = userRepository.findAll();
		ArrayList<PrunedUser> prunedUsersArray = new ArrayList<PrunedUser>();
		for (User user : userArray) {
			PrunedUser prunedUser = new PrunedUser();
			prunedUser.setId(user.getId());
			prunedUser.setEmail(user.getEmail());
			prunedUser.setName(user.getName());
			prunedUsersArray.add(prunedUser);			
		}
		return prunedUsersArray;
	}
	
	@RequestMapping(path="/ping")
	public String index() {
		return "Pong";
	}
}