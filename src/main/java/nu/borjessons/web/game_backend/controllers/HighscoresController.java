package nu.borjessons.web.game_backend.controllers;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import nu.borjessons.web.game_backend.exceptions.UnauthorizedUserException;
import nu.borjessons.web.game_backend.helpers.Header;
import nu.borjessons.web.game_backend.helpers.ValidateToken;
import nu.borjessons.web.game_backend.models.Highscore;
import nu.borjessons.web.game_backend.models.HighscoreRepository;
import nu.borjessons.web.game_backend.models.User;
import nu.borjessons.web.game_backend.models.UserRepository;

@CrossOrigin
@RestController
@RequestMapping(path="/highscores")
public class HighscoresController {
		
	@Autowired HighscoreRepository highscoreRepository;
	@Autowired UserRepository userRepository;
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PutMapping(path="/update")
	public @ResponseBody ResponseEntity updateHighscore(@RequestBody Highscore reqObject , @RequestHeader(value="token") String token) {
		
		User user = userRepository.findByToken(token);		
		if (user == null) {
			throw new UnauthorizedUserException("Your token is invalid or missing");
		}
		System.out.println(user.getEmail());
		
		Integer score = reqObject.getScore();
		
		//user.setToken(new Token().generateToken(20));			
		//userRepository.save(user);
		//HttpHeaders headers = Header.setHeaders(user);
		HttpHeaders headers = new HttpHeaders();
		
		
		// @TODO Add score to users list of scores
		String name = user.getName();
		Date now = new Date();
		Timestamp date = new Timestamp(now.getTime());
		Highscore highscore = highscoreRepository.findByName(name);
		
		if (highscore != null) {
			if(score > highscore.getScore()) {
				highscore.setScore(score);
				highscore.setDate(date);
				highscoreRepository.save(highscore);
				return new ResponseEntity("You broke your personal best. The global scoreboard has been updated", headers, HttpStatus.OK);
			} else {
				return new ResponseEntity("No updates were made since this was not your personal best",headers, HttpStatus.OK);
			}			
		} else {
			Highscore newHighscore = new Highscore(score, name, date);
			newHighscore.setScore(score);
			newHighscore.setName(name);
			newHighscore.setDate(date);
			highscoreRepository.save(newHighscore);
			return new ResponseEntity("Your score was added to the global score board", headers, HttpStatus.OK);
		}	
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping(path="/all")
	public @ResponseBody ResponseEntity updateHighscore(@RequestHeader(value="token") String token) {
		
		User user = userRepository.findByToken(token);
		User validUser = ValidateToken.validate(user, token);
		userRepository.save(validUser);
		HttpHeaders headers = Header.setHeaders(validUser);
		
		Iterable<Highscore> highscoreArray = highscoreRepository.findAll();
		return new ResponseEntity(highscoreArray, headers, HttpStatus.OK);		
	}
}
