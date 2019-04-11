package nu.borjessons.web.game_backend.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AllScoresRepository extends CrudRepository<AllScores, Integer> {

	List<AllScores> findByUserId(int userId);

	ArrayList<Object> findFirst10ByUserIdOrderByScoreDesc(Integer userId);

		@Modifying
	@Transactional
	@Query("DELETE FROM AllScores s WHERE s.userId = ?1")
	public void deleteByUserId(Integer userId);
}
