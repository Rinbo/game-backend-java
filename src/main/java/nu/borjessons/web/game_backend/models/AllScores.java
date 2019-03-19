package nu.borjessons.web.game_backend.models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class AllScores {
	
	public AllScores(Integer userId, Integer score, Timestamp date) {
		this.userId = userId;
		this.score = score;
		this.date = date;
	}
	public AllScores() {};
	
	private Integer userId;
	private Integer score;
	private Timestamp date;

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	
	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	
}
