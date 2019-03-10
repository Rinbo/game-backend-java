package nu.borjessons.web.game_backend.controllers;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nu.borjessons.web.game_backend.exceptions.UnauthorizedUserException;
import nu.borjessons.web.game_backend.exceptions.UserExistsException;
import nu.borjessons.web.game_backend.exceptions.UserNotFoundException;
import nu.borjessons.web.game_backend.helpers.Header;
import nu.borjessons.web.game_backend.helpers.PasswordUtil;
import nu.borjessons.web.game_backend.helpers.Token;
import nu.borjessons.web.game_backend.models.LoginObject;
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
	public @ResponseBody ResponseEntity<User> addNewUser (@Valid @RequestBody User reqUser)
			 {
		
		if(userRepository.findByEmail(reqUser.getEmail()) != null) {			
			throw new UserExistsException("That email already exists in the database");			
		}
		
		if(userRepository.findByName(reqUser.getName()) != null) {			
			throw new UserExistsException("That name is taken");			
		}
		
		User newUser = new User();
		try {
			newUser.setName(reqUser.getName().trim());
			newUser.setEmail(reqUser.getEmail().trim());
			newUser.setPassword(PasswordUtil.hashPassword(reqUser.getPassword().trim()));			
			newUser.setToken(new Token().generateToken(20));			
			userRepository.save(newUser);
			HttpHeaders headers = Header.setHeaders(newUser);		    
			return new ResponseEntity<User>(newUser, headers, HttpStatus.OK);		
		} catch (Exception e) {
			return new ResponseEntity<User>(reqUser, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(path="/signin")
	public @ResponseBody ResponseEntity<User> signInUser (@Valid @RequestBody LoginObject credentials) throws NoSuchAlgorithmException {
				
		String email = credentials.getEmail();
		String password = credentials.getPassword();

		// Check if User exists
		if (userRepository.findByEmail(email) == null) {
			throw new UserNotFoundException("We could not find a user matching that email address");
		}
		
		User storedUser = userRepository.findByEmail(email);
		
		// Check if password matches what is stored in database
		if (storedUser.checkPassword(PasswordUtil.hashPassword(password))) {
			storedUser.setToken(new Token().generateToken(20));
			userRepository.save(storedUser);
			HttpHeaders headers = Header.setHeaders(storedUser);	
			return new ResponseEntity<User>(storedUser, headers, HttpStatus.OK);
		} else {
			throw new UnauthorizedUserException("Wrong password");
		}		
	}
	
	// @RequestParam(value="email") String email, @RequestParam(value="password") String password
	
	@GetMapping(path="/validatetoken")
	public @ResponseBody ResponseEntity<User> validateUser(@RequestHeader(value="token") String token) {
		User storedUser = userRepository.findByToken(token);
		if(!storedUser.isPresent()) {
			throw new UnauthorizedUserException("Your token is invalid or missing");
		}
		storedUser.setToken(new Token().generateToken(20));
		HttpHeaders headers = Header.setHeaders(storedUser);	
		userRepository.save(storedUser);
		return new ResponseEntity<User>(storedUser, headers, HttpStatus.OK);
	}
	
	@DeleteMapping(path="/signout")
	public @ResponseBody String signOutUser (@RequestHeader(value="token") String token) {
	
		User storedUser = (User) userRepository.findByToken(token);
	    if (!storedUser.isPresent()) {	    	
	      throw new UnauthorizedUserException("Your token is invalid or missing");
	    }
	    
	    if(storedUser.getToken().equals(token)) {
		    storedUser.setToken("");
		    userRepository.save(storedUser);	    
		    return "You were successfully signed out";
	    } else {
	    	throw new UnauthorizedUserException("Your token is invalid. Login again to refresh it");
	    }
	}	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping(path="/all")
	public @ResponseBody ResponseEntity getAllUsers(@RequestHeader(value="token") String token) {
		// Get the users and send them back but remove password and token first
		
		User storedUser = userRepository.findByToken(token);
		if(!storedUser.isPresent()) {
			throw new UnauthorizedUserException("Invalid Token");
		}
		
		storedUser.setToken(new Token().generateToken(20));
		userRepository.save(storedUser);
		
		Iterable<User> userArray = userRepository.findAll();
		ArrayList<PrunedUser> prunedUsersArray = new ArrayList<PrunedUser>();
		for (User user : userArray) {
			PrunedUser prunedUser = new PrunedUser();
			prunedUser.setId(user.getId());
			prunedUser.setEmail(user.getEmail());
			prunedUser.setName(user.getName());
			prunedUsersArray.add(prunedUser);			
		}
		
		HttpHeaders headers = Header.setHeaders(storedUser);	
		return new ResponseEntity(prunedUsersArray, headers, HttpStatus.OK);
	}
	
	@GetMapping(path="/{id}")
	public ResponseEntity<User> retrieveUser(@PathVariable int id, @RequestHeader(value="token") String token) {	
	    
		User storedUser = (User) userRepository.findById(id);

	    if (!storedUser.isPresent()) {
	    	throw new UserNotFoundException("id-" + id);
	    }
	    
	    if(!storedUser.getToken().equals(token)) {
	    	throw new UnauthorizedUserException("Your token is invalid");
	    }
	    
	    storedUser.setToken(new Token().generateToken(20));	    
		userRepository.save(storedUser);
		
		HttpHeaders headers = Header.setHeaders(storedUser);	
	    
	    return new ResponseEntity<User>(storedUser, headers, HttpStatus.OK);
	  }
	
	
	@RequestMapping(path="/ping")
	public String index() {
		return "Pong";
	}
	
	@DeleteMapping(path="/{id}") 
	public ResponseEntity<User> deleteUser(@PathVariable int id, @RequestHeader(value="token") String token) {
		User storedUser = (User) userRepository.findById(id);

	    if (!storedUser.isPresent()) {
	    	throw new UserNotFoundException("id-" + id);
	    }
	    
	    if(!storedUser.getToken().equals(token)) {
	    	throw new UnauthorizedUserException("Your token is invalid");
	    }
	    
	    userRepository.deleteById(id);
	    return new ResponseEntity<User>(HttpStatus.OK);
	}
	
}