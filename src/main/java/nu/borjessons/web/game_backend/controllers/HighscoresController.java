package nu.borjessons.web.game_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nu.borjessons.web.game_backend.exceptions.UnauthorizedUserException;
import nu.borjessons.web.game_backend.models.HighscoreRepository;
import nu.borjessons.web.game_backend.models.User;
import nu.borjessons.web.game_backend.models.UserRepository;

@CrossOrigin
@RestController
@RequestMapping(path="/highscores")
public class HighscoresController {
	@Autowired
	
	private HighscoreRepository highscoreRepository;
	private UserRepository userRepository;
	
	@PutMapping(path="/update")
	public @ResponseBody String updateHighscore(@RequestBody Integer score , @RequestHeader(value="token") String token) {
		
		User user = userRepository.findByToken(token);
		if (user == null) {
			throw new UnauthorizedUserException("Your token is invalid or missing");
		}
		
		
		
		
		
		return "Hello";
	}
}
