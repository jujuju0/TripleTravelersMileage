package pretask.triple.mileage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretask.triple.mileage.model.entity.PointHistoryEntity;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistoryEntity, String> {
    @Query(value = "SELECT * FROM PointHistory WHERE UserID = :userID ORDER BY CreateDate", nativeQuery = true)
    List<PointHistoryEntity> findByUserId(String userID);

    @Query(value = "SELECT IFNULL((SELECT SUM(PointEarned) AS TotalPoint FROM PointHistory WHERE UserID = :userID GROUP BY UserID), 0) AS RESULT", nativeQuery = true)
    int findMyPointByUserId(String userID);
}
