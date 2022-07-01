package pretask.triple.mileage.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pretask.triple.mileage.model.entity.PlaceEntity;
import pretask.triple.mileage.model.entity.ReviewEntity;
import pretask.triple.mileage.model.entity.UserEntity;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void saveTest()
    {
        ReviewEntity review = ReviewEntity.builder()
                .reviewID(UUID.randomUUID().toString())
                .userEntity(new UserEntity("3ede0ef2-92b7-4817-a5f3-0c575361f745"))
                .placeEntity(new PlaceEntity("2e4baf1c-5acb-4efb-a1af-eddada31b00f"))
                .bonus(true)
                .content("리뷰 테스트 입니다.").build();

        ReviewEntity savedReview = reviewRepository.save(review);

        Assertions.assertEquals("리뷰 테스트 입니다.", savedReview.getContent());
    }
}