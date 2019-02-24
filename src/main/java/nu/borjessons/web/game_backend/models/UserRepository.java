package nu.borjessons.web.game_backend.models;

import org.springframework.data.repository.CrudRepository;
import nu.borjessons.web.game_backend.models.User;

// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository

public interface UserRepository extends CrudRepository<User, Integer> {
	
	User findByEmail(String email);

}