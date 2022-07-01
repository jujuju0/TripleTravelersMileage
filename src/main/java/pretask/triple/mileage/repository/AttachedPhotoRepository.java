package pretask.triple.mileage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pretask.triple.mileage.model.entity.AttachedPhotoEntity;

import java.util.List;

public interface AttachedPhotoRepository extends JpaRepository<AttachedPhotoEntity, String> {
    @Query(value = "SELECT * FROM AttachedPhoto WHERE ReviewID = :reviewID", nativeQuery = true)
    List<AttachedPhotoEntity> findByReviewId(String reviewID);
    @Query(value = "SELECT AttachedPhotoIds FROM AttachedPhoto", nativeQuery = true)
    List<String> findAllIds();
}
