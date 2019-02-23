package nu.borjessons.web.game_backend;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(path="/users") 
public class UsersController {
	@Autowired 
	          
	private UserRepository userRepository;
	
	@PostMapping(path="/add") 
	public @ResponseBody Optional<User> addNewUser (@RequestBody User user)
			 {		
		User n = new User();
		n.setName(user.getName());
		n.setEmail(user.getEmail());
		n.setPassword(user.getPassword());
		Token tokenClass = new Token();
		String token = tokenClass.generateToken(20);
		n.setToken(token);
		userRepository.save(n);
		return userRepository.findById(n.getId());
	}

	@GetMapping(path="/all")
	public @ResponseBody Iterable<User> getAllUsers() {
		// This returns a JSON or XML with the users
		return userRepository.findAll();
	}
	
	@RequestMapping(path="/ping")
	public String index() {
		return "Pong";
	}
}