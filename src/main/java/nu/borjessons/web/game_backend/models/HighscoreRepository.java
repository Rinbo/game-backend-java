package nu.borjessons.web.game_backend.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import nu.borjessons.web.game_backend.models.Highscore;

@Repository
public interface HighscoreRepository extends JpaRepository<Highscore, Integer> {
	Highscore findByName(String name);

	ArrayList<Highscore> findFirst10ByOrderByScoreDesc();

	ArrayList<Highscore> findAllByOrderByScoreDesc();

	@Query("SELECT hs FROM Highscore hs WHERE hs.name = ?1")
	Highscore findByNameAndRank(String name);
}
