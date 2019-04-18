package nu.borjessons.web.game_backend.controllers;

import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import nu.borjessons.web.game_backend.exceptions.UnauthorizedUserException;
import nu.borjessons.web.game_backend.exceptions.UserNotFoundException;
import nu.borjessons.web.game_backend.helpers.Header;
import nu.borjessons.web.game_backend.helpers.PasswordUtil;
import nu.borjessons.web.game_backend.helpers.Token;
import nu.borjessons.web.game_backend.models.AllScores;
import nu.borjessons.web.game_backend.models.AllScoresRepository;
import nu.borjessons.web.game_backend.models.Highscore;
import nu.borjessons.web.game_backend.models.HighscoreRepository;
import nu.borjessons.web.game_backend.models.LoginObject;
import nu.borjessons.web.game_backend.models.PrunedUser;
import nu.borjessons.web.game_backend.models.User;
import nu.borjessons.web.game_backend.models.UserRepository;

@CrossOrigin
@RestController
@RequestMapping(path = "/users")
public class UsersController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private HighscoreRepository highscoreRepository;
	@Autowired
	private AllScoresRepository allScoresRepository;

	@PostMapping(path = "/add")
	public @ResponseBody ResponseEntity<User> addNewUser(@Valid @RequestBody User reqUser) {

		if (userRepository.findByName(reqUser.getName()) != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "That Name is already taken");
		}

		User newUser = new User();
		try {
			newUser.setName(reqUser.getName().trim());
			newUser.setPassword(PasswordUtil.hashPassword(reqUser.getPassword().trim()));
			newUser.setToken(new Token().generateToken(20));
			userRepository.save(newUser);

			Date now = new Date();
			Timestamp date = new Timestamp(now.getTime());

			if (reqUser.getScore() != null && reqUser.getScore() != 0) {
				highscoreRepository.save(new Highscore(reqUser.getScore(), newUser.getName(), date));
				allScoresRepository.save(new AllScores(newUser.getId(), reqUser.getScore(), date));
			}

			HttpHeaders headers = Header.setHeaders(newUser);
			return new ResponseEntity<User>(newUser, headers, HttpStatus.OK);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "That did not work :(. Try Again later");
		}
	}

	@PutMapping(path = "/update")
	public @ResponseBody ResponseEntity<User> updateUser(@RequestBody User reqUser,
			@RequestHeader(value = "token") String token) throws NoSuchAlgorithmException {
		User user;
		try {
			user = userRepository.findByToken(token);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token. Try logging in again");
		}

		if (reqUser.getEmail() != null) {
			user.setEmail(reqUser.getEmail());
		}
		if (reqUser.getNewPw() != null) {
			String newHashedPassword = PasswordUtil.hashPassword(reqUser.getNewPw().trim());
			if (user.checkPassword(PasswordUtil.hashPassword(reqUser.getPassword().trim()))) {
				user.setPassword(newHashedPassword);
			} else {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
						"Unauthorized. Your 'Current Password' was incorrect");
			}
		}

		user.setToken(new Token().generateToken(20));
		userRepository.save(user);
		HttpHeaders headers = Header.setHeaders(user);
		return new ResponseEntity<User>(user, headers, HttpStatus.OK);
	}

	@PostMapping(path = "/signin")
	public @ResponseBody ResponseEntity<User> signInUser(@Valid @RequestBody LoginObject credentials)
			throws NoSuchAlgorithmException {

		String name = credentials.getName();
		String password = credentials.getPassword();

		if (userRepository.findByName(name) == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "We could not find a user matching that name.");
		}

		User storedUser = userRepository.findByName(name);

		if (storedUser.checkPassword(PasswordUtil.hashPassword(password))) {
			storedUser.setToken(new Token().generateToken(20));
			userRepository.save(storedUser);

			if (credentials.getScore() != null && credentials.getScore() != 0) {
				Date now = new Date();
				Timestamp date = new Timestamp(now.getTime());
				allScoresRepository.save(new AllScores(storedUser.getId(), credentials.getScore(), date));
				Highscore userHighscore = highscoreRepository.findByName(storedUser.getName());
				if (userHighscore == null) {
					highscoreRepository.save(new Highscore(credentials.getScore(), name, date));
				} else if (userHighscore.getScore() < credentials.getScore()) {
					userHighscore.setScore(credentials.getScore());
					userHighscore.setDate(date);
					highscoreRepository.save(userHighscore);
				}
			}

			HttpHeaders headers = Header.setHeaders(storedUser);
			return new ResponseEntity<User>(storedUser, headers, HttpStatus.OK);
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password");
		}
	}

	@GetMapping(path = "/validatetoken")
	public @ResponseBody ResponseEntity<User> validateUser(@RequestHeader(value = "token") String token) {
		User storedUser = userRepository.findByToken(token);
		if (!storedUser.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to validate your token. Try logging in again.");
		}
		storedUser.setToken(new Token().generateToken(20));
		HttpHeaders headers = Header.setHeaders(storedUser);
		userRepository.save(storedUser);
		return new ResponseEntity<User>(storedUser, headers, HttpStatus.OK);
	}

	@DeleteMapping(path = "/signout")
	public @ResponseBody String signOutUser(@RequestHeader(value = "token") String token) {

		User storedUser = (User) userRepository.findByToken(token);
		System.out.println(storedUser);
		if (!storedUser.isPresent()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Your token is invalid or missing");
		}

		if (storedUser.getToken().equals(token)) {
			storedUser.setToken("");
			userRepository.save(storedUser);
			return "You were successfully signed out";
		} else {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to validate token. Try logging in again.");
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping(path = "/all")
	public @ResponseBody ResponseEntity getAllUsers(@RequestHeader(value = "token") String token) {

		User storedUser = userRepository.findByToken(token);
		if (!storedUser.isPresent()) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to validate token. Try logging in again.");
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

	@GetMapping(path = "/{id}")
	public ResponseEntity<User> retrieveUser(@PathVariable int id, @RequestHeader(value = "token") String token) {

		User storedUser = (User) userRepository.findById(id);

		if (!storedUser.isPresent()) {
			throw new UserNotFoundException("id-" + id);
		}

		if (!storedUser.getToken().equals(token)) {
			throw new UnauthorizedUserException("Your token is invalid");
		}

		storedUser.setToken(new Token().generateToken(20));
		userRepository.save(storedUser);

		HttpHeaders headers = Header.setHeaders(storedUser);

		return new ResponseEntity<User>(storedUser, headers, HttpStatus.OK);
	}

	@RequestMapping(path = "/ping")
	public String index() {
		return "Pong";
	}

	@DeleteMapping(path = "/delete")
	public ResponseEntity<String> deleteUser(@RequestHeader(value = "token") String token) {
		User storedUser = userRepository.findByToken(token);

		if (!storedUser.isPresent() || !storedUser.getToken().equals(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unable to validate token. Try logging in again.");
		}

		try {

			allScoresRepository.deleteByUserId(storedUser.getId());
			userRepository.deleteById(storedUser.getId());
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong server side");
		}
		return new ResponseEntity<String>("Account Deleted", HttpStatus.OK);
	}

}