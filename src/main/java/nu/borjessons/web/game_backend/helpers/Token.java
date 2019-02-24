package nu.borjessons.web.game_backend.helpers;

import org.apache.commons.lang3.RandomStringUtils;

public class Token {
	private String token;
	
	public String generateToken(int length) {
		String generatedString = RandomStringUtils.randomAlphanumeric(length);	
		return generatedString;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
}