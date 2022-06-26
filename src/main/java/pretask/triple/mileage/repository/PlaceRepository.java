package pretask.triple.mileage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pretask.triple.mileage.model.entity.PlaceEntity;

public interface PlaceRepository extends JpaRepository<PlaceEntity, String> {
}
