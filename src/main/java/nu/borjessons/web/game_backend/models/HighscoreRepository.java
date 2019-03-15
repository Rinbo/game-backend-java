package nu.borjessons.web.game_backend.models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import nu.borjessons.web.game_backend.models.Highscore;

public interface HighscoreRepository extends CrudRepository<Highscore, Integer>{
	Highscore findByName(String name);
	List<Highscore> findAllByOrderByScoreDesc();
}
