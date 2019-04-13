package nu.borjessons.web.game_backend.controllers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import nu.borjessons.web.game_backend.helpers.Header;
import nu.borjessons.web.game_backend.helpers.ValidateToken;
import nu.borjessons.web.game_backend.models.AllScores;
import nu.borjessons.web.game_backend.models.AllScoresRepository;
import nu.borjessons.web.game_backend.models.Highscore;
import nu.borjessons.web.game_backend.models.HighscoreRepository;
import nu.borjessons.web.game_backend.models.User;
import nu.borjessons.web.game_backend.models.UserRepository;
import nu.borjessons.web.game_backend.service.HighscoreService;

@CrossOrigin
@RestController
@RequestMapping(path = "/highscores")
public class HighscoresController {

	@Autowired
	HighscoreRepository highscoreRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	AllScoresRepository allScoresRepository;

	@Autowired
	HighscoreService highscoreService;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PutMapping(path = "/update")
	public @ResponseBody ResponseEntity updateScores(@RequestBody Highscore reqObject,
			@RequestHeader(value = "token") String token) {

		Date now = new Date();
		Timestamp date = new Timestamp(now.getTime());

		Integer score = reqObject.getScore();
		if (score == null) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
					"Null score indicates something went wrong. Try logging in again");
		}

		User user = userRepository.findByToken(token);
		User validUser = ValidateToken.validate(user, token);
		userRepository.save(validUser);
		HttpHeaders headers = Header.setHeaders(validUser);

		allScoresRepository.save(new AllScores(validUser.getId(), score, date));

		String name = validUser.getName();
		Highscore highscore = highscoreRepository.findByName(name);

		if (highscore != null) {
			if (score > highscore.getScore()) {
				highscore.setScore(score);
				highscore.setDate(date);
				highscoreRepository.save(highscore);
				// @TODO Special message if user was placed as number one
				return new ResponseEntity("You broke your personal best. The global scoreboard has been updated", headers,
						HttpStatus.OK);
			} else {
				return new ResponseEntity("Score added to your history", headers, HttpStatus.OK);
			}
		} else {
			Highscore newHighscore = new Highscore(score, name, date);
			highscoreRepository.save(newHighscore);
			return new ResponseEntity("Your score was added to the global score board", headers, HttpStatus.OK);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping(path = "/all")
	public @ResponseBody ResponseEntity getHighscores(@RequestParam(value = "name", required = false) String name) {
		ArrayList<Highscore> highscoreArray = highscoreRepository.findFirst10ByOrderByScoreDesc();
		if (name != null) {
			Highscore userHighscore = highscoreService.getEntryAndPositionOfName(name);
			if (userHighscore.getName() != null) {
				if (userHighscore.getFlashRank() > 10) {
					highscoreArray.add(userHighscore);
				}
			}
		}
		return new ResponseEntity(highscoreArray, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping(path = "/user")
	public @ResponseBody ResponseEntity getUserScores(@RequestHeader(value = "token") String token) {

		User user = userRepository.findByToken(token);
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user");
		}

		User validUser = ValidateToken.validate(user, token);
		if (validUser == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to validate your token");
		}

		userRepository.save(validUser);
		HttpHeaders headers = Header.setHeaders(validUser);

		ArrayList<AllScores> userScoresArray = allScoresRepository.findByUserIdOrderByScoreDesc(validUser.getId());
		return new ResponseEntity(userScoresArray, headers, HttpStatus.OK);
	}
}
