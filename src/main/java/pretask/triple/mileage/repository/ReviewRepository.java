package pretask.triple.mileage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretask.triple.mileage.model.entity.ReviewEntity;

public interface ReviewRepository extends JpaRepository<ReviewEntity, String> {
    @Query(value = "SELECT IFNULL((SELECT COUNT(*) FROM Review WHERE UserID = :userID AND PlaceID = :placeID GROUP BY UserID, PlaceID), 0) AS RESULT", nativeQuery = true)
    int isExistsByUserIdAndPlaceId(String userID, String placeID);
    @Query(value = "SELECT IFNULL((SELECT COUNT(*) FROM Review WHERE PlaceID = :placeID GROUP BY PlaceID), 0) AS RESULT", nativeQuery = true)
    int countByPlaceId(String placeID);
}
