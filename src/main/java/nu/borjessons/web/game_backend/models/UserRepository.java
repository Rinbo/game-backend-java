package nu.borjessons.web.game_backend.models;

import org.springframework.data.repository.CrudRepository;
import nu.borjessons.web.game_backend.models.User;


public interface UserRepository extends CrudRepository<User, Integer> {
	
	User findByEmail(String email);
	User findById(int id);
	User findByToken(String token);

}