package nu.borjessons.web.game_backend.helpers;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import nu.borjessons.web.game_backend.models.User;

public class ValidateToken {

	public static User validate(User user, String token) {
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "");
		}
		user.setToken(new Token().generateToken(20));
		return user;
	}

}
