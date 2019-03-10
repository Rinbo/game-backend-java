package nu.borjessons.web.game_backend.models;

import org.springframework.data.repository.CrudRepository;
import nu.borjessons.web.game_backend.models.Highscore;

public interface HighscoreRepository extends CrudRepository<Highscore, Integer>{

}
