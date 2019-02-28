package nu.borjessons.web.game_backend.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nu.borjessons.web.game_backend.exceptions.UnauthorizedUserException;
import nu.borjessons.web.game_backend.exceptions.UserNotFoundException;
import nu.borjessons.web.game_backend.helpers.PasswordUtil;
import nu.borjessons.web.game_backend.helpers.Token;
import nu.borjessons.web.game_backend.models.PrunedUser;
import nu.borjessons.web.game_backend.models.TokenObject;
import nu.borjessons.web.game_backend.models.User;
import nu.borjessons.web.game_backend.models.UserRepository;


@CrossOrigin
@RestController
@RequestMapping(path="/users") 
public class UsersController {
	@Autowired 
	          
	private UserRepository userRepository;
	
	@PostMapping(path="/add") 
	public @ResponseBody ResponseEntity<User> addNewUser (@Valid @RequestBody User reqUser)
			 {
		User newUser = new User();
		try {
			newUser.setName(reqUser.getName().trim());
			newUser.setEmail(reqUser.getEmail().trim());
			newUser.setPassword(PasswordUtil.hashPassword(reqUser.getPassword().trim()));
			Token tokenClass = new Token();
			String token = tokenClass.generateToken(20);
			newUser.setToken(token);
			userRepository.save(newUser);
			return new ResponseEntity<User>(newUser, HttpStatus.OK);		
		} catch (Exception e) {
			return new ResponseEntity<User>(reqUser, HttpStatus.BAD_REQUEST);
		}

	}
	
	@PostMapping(path="/validatetoken")
	public @ResponseBody ResponseEntity<User> validateUser(@RequestBody TokenObject token) {
		User storedUser = userRepository.findByToken(token.getToken());		
		if(!storedUser.isPresent()) {
			return new ResponseEntity<User>(HttpStatus.UNAUTHORIZED);
		}
		storedUser.setToken(new Token().generateToken(20));
		userRepository.save(storedUser);
		return new ResponseEntity<User>(storedUser, HttpStatus.OK);
	}
	
	@PostMapping(path="/signout")
	public @ResponseBody String signOutUser (@Valid @RequestBody PrunedUser reqUser) {
		User storedUser = (User) userRepository.findByEmail(reqUser.getEmail());
	    if (!storedUser.isPresent()) {	    	
	      throw new UserNotFoundException("email-" + reqUser.getEmail());
	    }
	    
	    if(storedUser.getToken().equals(reqUser.getToken())) {
		    storedUser.setToken("");
		    userRepository.save(storedUser);	    
		    return "You were successfully signed out";
	    } else {
	    	throw new UnauthorizedUserException("Where is your token dude?");
	    }
	}	
	
	@PostMapping(path="/signin")
	public @ResponseBody ResponseEntity<User> signInUser (@Valid @RequestBody User reqUser) throws NoSuchAlgorithmException {
		// Check if User exists
		try {
			userRepository.findByEmail(reqUser.getEmail());
		} catch (Exception e) {
			return new ResponseEntity<User>(reqUser, HttpStatus.NOT_FOUND);
		}
		
		User storedUser = userRepository.findByEmail(reqUser.getEmail());
		// Check if password matches what is stored in database
		if (storedUser.checkPassword(PasswordUtil.hashPassword(reqUser.getPassword().trim()))) {
			storedUser.setToken(new Token().generateToken(20));
			userRepository.save(storedUser);
			return new ResponseEntity<User>(storedUser, HttpStatus.OK);
		} else {
			return new ResponseEntity<User>(reqUser, HttpStatus.UNAUTHORIZED);
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
	
	@GetMapping(path="/{id}")
	public ResponseEntity<User> retrieveUser(@PathVariable int id) {	
	    User storedUser = (User) userRepository.findById(id);

	    if (!storedUser.isPresent()) {
	      throw new UserNotFoundException("id-" + id);
	    }
	    return new ResponseEntity<User>(storedUser, HttpStatus.OK);
	  }
	
	
	@RequestMapping(path="/ping")
	public String index() {
		return "Pong";
	}
	
	@DeleteMapping(path="/{id}") 
	public ResponseEntity<User> deleteUser(@PathVariable int id) {
	    userRepository.deleteById(id);
	    return new ResponseEntity<User>(HttpStatus.OK);
	}
	
	
}