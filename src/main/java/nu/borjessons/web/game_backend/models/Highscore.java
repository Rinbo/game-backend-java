package nu.borjessons.web.game_backend.models;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Highscore {

	public Highscore(Integer score, String name, Timestamp date) {
		this.score = score;
		this.name = name;
		this.date = date;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private Integer score;

	private String name;

	private Timestamp date;

	private Integer flashRank;

	public Highscore() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public Integer getFlashRank() {
		return flashRank;
	}

	public void setFlashRank(Integer flashRank) {
		this.flashRank = flashRank;
	}
}
