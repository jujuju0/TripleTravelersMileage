package pretask.triple.mileage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretask.triple.mileage.model.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String>{
}
