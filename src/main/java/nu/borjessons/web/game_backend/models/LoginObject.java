package nu.borjessons.web.game_backend.models;

public class LoginObject {

	private String email;
	private String password;
	private String name;
	private Integer score = null; // Temp score for relaying score from non logged in user

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Integer getScore() {
		return score;
	}

}
