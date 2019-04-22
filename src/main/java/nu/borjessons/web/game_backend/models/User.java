package nu.borjessons.web.game_backend.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.springframework.lang.Nullable;

@Entity
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@NotNull
	@Size(min = 1, message = "It would be nice if your name consisted of more than one letter")
	private String name;

	@Email(message = "You must provide a valid email address")
	private String email;

	@NotNull
	@Size(min = 6, message = "Password should have atleast 6 characters")
	private String password;

	private String token;

	private Integer score = null; // Temp score stored here for relaying score from registration and login

	@Nullable
	private String newPw;

	private Boolean singleSemiColon = false;

	public Integer getId() {
		return id;
	}

	public Boolean getSingleSemiColon() {
		return singleSemiColon;
	}

	public void setSingleSemiColon(Boolean singleSemiColon) {
		this.singleSemiColon = singleSemiColon;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setNewPw(String newPw) {
		this.newPw = newPw;
	}

	@JsonIgnore
	public String getNewPw() {
		return newPw;
	}

	@JsonIgnore
	public String getToken() {
		return token;
	}

	@JsonProperty
	public void setToken(String token) {
		this.token = token;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	public boolean isPresent() {
		return true;
	}

	public Integer setScore(int score) {
		return this.score = score;
	}

	public Integer getScore() {
		return score;
	}

}