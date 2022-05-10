package tw.elliot.trick05.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tw.elliot.trick05.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
}
