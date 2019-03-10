package nu.borjessons.web.game_backend.helpers;

import org.springframework.http.HttpHeaders;

import nu.borjessons.web.game_backend.models.User;

public class Header {
	public static HttpHeaders setHeaders(User user) {		
		HttpHeaders headers = new HttpHeaders();
	    headers.add("token", user.getToken());
	    headers.add("Access-Control-Expose-Headers", "token" );
	    return headers;
	}
}
