package nu.borjessons.web.game_backend.helpers;

import org.apache.commons.lang3.RandomStringUtils;

public class Token {
	
	public String generateToken(int length) {
		String generatedString = RandomStringUtils.randomAlphanumeric(length);	
		return generatedString;
	}
	
}