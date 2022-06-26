package pretask.triple.mileage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretask.triple.mileage.model.dto.ReviewEventRequest;
import pretask.triple.mileage.model.entity.*;
import pretask.triple.mileage.repository.*;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewPointService {
    private static final int ADD_POINT = 1;
    private static final int DELETE_POINT = -1;
    private static final int DELETE_POINT_CONTENT_PHOTO = -2;
    private final ReviewRepository reviewRepository;
    private final AttachedPhotoRepository attachedPhotoRepository;
    private final UserRepository userRepository;

    private final PointHistoryRepository pointHistoryRepository;
    private final PlaceRepository placeRepository;

    public String ReviewEvents(ReviewEventRequest event)
    {
        switch (event.getAction())
        {
            case ADD ->
            {
                return AddReview(event);
            }
            case MOD ->
            {
                return ModReview(event);
            }
            case DELETE ->
            {
                return DeleteReview(event);
            }
            default ->
            {
                return "등록된 Action 값이 아닙니다.";
            }
        }
    }

    private boolean IsExist(String userId, String placeId)
    {
        return reviewRepository.isExistsByUserIdAndPlaceId(userId, placeId) == 0 ? false : true;
    }

    @Transactional
    private String AddReview(ReviewEventRequest event)
    {
        try
        {
            ReviewEntity review;
            UserEntity userEntity = userRepository.findById(event.getUserId()).orElseThrow(()-> new NullPointerException("등록된 유저가 없습니다."));
            PlaceEntity placeEntity = placeRepository.findById(event.getPlaceId()).orElseThrow(()-> new NullPointerException("등록된 장소가 없습니다."));

            if (IsExist(event.getUserId(), event.getPlaceId())) {
                return "해당 장소에 이미 리뷰를 등록한 유저입니다.";
            }

            if (Objects.isNull(event.getContent()) || event.getContent().length() == 0)
            {
                return "작성 된 리뷰 내용이 없습니다.";
            }
            else
            {
                // 최초 등록
                if(reviewRepository.countByPlaceId(placeEntity.getPlaceID()) == 0)
                {
                    review = ReviewEntity.builder()
                            .reviewID(event.getReviewId())
                            .content(event.getContent())
                            .bonus(true)
                            .placeEntity(placeEntity)
                            .userEntity(userEntity)
                            .build();

                    reviewRepository.save(review);
                    AddPointHistory(event, "Bonus: First Registrant", ADD_POINT);
                }
                else
                {
                    review = ReviewEntity.builder()
                            .reviewID(event.getReviewId())
                            .content(event.getContent())
                            .bonus(false)
                            .placeEntity(placeEntity)
                            .userEntity(userEntity)
                            .build();

                    reviewRepository.save(review);
                }
                AddPointHistory(event, "Text Review", ADD_POINT);
            }

            // 사진 등록
            if (!Objects.isNull(event.getAttachedPhotoIds()) && Arrays.stream(event.getAttachedPhotoIds()).count() > 0)
            {
                Arrays.stream(event.getAttachedPhotoIds()).forEach(x -> attachedPhotoRepository.save(AttachedPhotoEntity.builder().attachedPhotoIDS(x).reviewEntity(review).build()));
                AddPointHistory(event, "Photo Review", ADD_POINT);
            }

            return "리뷰가 등록되었습니다.";
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
    }

    @Transactional
    private String ModReview(ReviewEventRequest event)
    {
        try
        {
            ReviewEntity review = reviewRepository.findById(event.getReviewId()).orElseThrow(() -> new NullPointerException("등록된 리뷰가 없습니다."));
            UserEntity userEntity = userRepository.findById(event.getUserId()).orElseThrow(()-> new NullPointerException("등록된 유저가 없습니다."));

            if (Objects.isNull(event.getContent()) || event.getContent().length() == 0)
            {
                return "작성 된 리뷰 내용이 없습니다.";
            }
            else
            {
                review.UpdateReview(event.getContent());
            }

            List<AttachedPhotoEntity> attachedPhotoEntityList = attachedPhotoRepository.findByReviewId(event.getReviewId());
            if(attachedPhotoEntityList.stream().count() > 0)
            {
                attachedPhotoRepository.findByReviewId(event.getReviewId()).forEach(attachedPhotoRepository::delete);

                if (!Objects.isNull(event.getAttachedPhotoIds()) && Arrays.stream(event.getAttachedPhotoIds()).count() > 0)
                {
                    Arrays.stream(event.getAttachedPhotoIds())
                            .forEach(x -> attachedPhotoRepository.save(AttachedPhotoEntity.builder()
                                    .attachedPhotoIDS(x)
                                    .reviewEntity(review)
                                    .build())
                            );
                }
                else
                {
                    AddPointHistory(event, "Empty Photo", DELETE_POINT);
                }
            }
            else
            {
                if (!Objects.isNull(event.getAttachedPhotoIds()) && Arrays.stream(event.getAttachedPhotoIds()).count() > 0)
                {
                    Arrays.stream(event.getAttachedPhotoIds())
                            .forEach(x -> attachedPhotoRepository.save(AttachedPhotoEntity.builder()
                                    .attachedPhotoIDS(x)
                                    .reviewEntity(review)
                                    .build())
                            );
                    AddPointHistory(event, "Add Photo", ADD_POINT);
                }
            }

            reviewRepository.save(review);
            return "리뷰가 수정되었습니다.";
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
    }

    @Transactional
    private String DeleteReview(ReviewEventRequest event)
    {
        try
        {
            ReviewEntity review = reviewRepository.findById(event.getReviewId()).orElseThrow(() -> new NullPointerException("등록된 리뷰가 없습니다."));
            UserEntity userEntity = userRepository.findById(event.getUserId()).orElseThrow(()-> new NullPointerException("등록된 유저가 없습니다."));

            if(review.isBonus())
            {
                AddPointHistory(event, "Delete Review: Bonus", DELETE_POINT);
            }

            if(attachedPhotoRepository.findByReviewId(event.getReviewId()).stream().count() > 0)
            {
                attachedPhotoRepository.findByReviewId(event.getReviewId()).forEach(attachedPhotoRepository::delete);
                AddPointHistory(event, "Delete Review: Content, Photo", DELETE_POINT_CONTENT_PHOTO);
            }
            else
            {
                AddPointHistory(event, "Delete Review: Content", DELETE_POINT);
            }

            reviewRepository.delete(review);

            return "리뷰가 삭제되었습니다.";
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
    }

    @Transactional
    private void AddPointHistory(ReviewEventRequest event, String reason, int point)
    {
        PointHistoryEntity pointHistory = PointHistoryEntity.builder()
                .pointID(UUID.randomUUID().toString())
                .pointEarned(point)
                .reason(reason)
                .userID(event.getUserId())
                .reviewID(event.getReviewId())
                .placeID(event.getPlaceId())
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    public int GetMyPoint(String userId)
    {
        return Objects.isNull(pointHistoryRepository.findMyPointByUserId(userId)) ? 0 : pointHistoryRepository.findMyPointByUserId(userId);
    }

    public List<PointHistoryEntity> GetMyPointDetail(String userId)
    {
        return pointHistoryRepository.findByUserId(userId);
    }
}
