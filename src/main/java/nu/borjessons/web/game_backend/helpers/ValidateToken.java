package nu.borjessons.web.game_backend.helpers;

import nu.borjessons.web.game_backend.exceptions.UnauthorizedUserException;
import nu.borjessons.web.game_backend.models.User;

public class ValidateToken {
		
	public static User validate(User user, String token) {		
		if (user == null) {
			throw new UnauthorizedUserException("Your token is invalid or missing");
		}		
		user.setToken(new Token().generateToken(20));	
		return user;
	}
	
}
