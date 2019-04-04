package nu.borjessons.web.game_backend.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface AllScoresRepository extends CrudRepository<AllScores, Integer> {

	List<AllScores> findByUserId(int userId);

	ArrayList<Object> findFirst10ByUserIdOrderByScoreDesc(Integer userId);

}
