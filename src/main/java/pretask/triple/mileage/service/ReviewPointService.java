package pretask.triple.mileage.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pretask.triple.mileage.model.dto.ResultResponse;
import pretask.triple.mileage.model.dto.ReviewEventRequest;
import pretask.triple.mileage.model.entity.*;
import pretask.triple.mileage.model.enums.ActionEnum;
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

    public ResultResponse ReviewEvents(ReviewEventRequest event)
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
                ResultResponse _res = new ResultResponse();
                _res.setType("리뷰 이벤트");
                _res.setSuccess(false);
                _res.setResult("등록된 Action 값이 아닙니다.");
                return _res;
            }
        }
    }

    private boolean IsExist(String userId, String placeId)
    {
        return reviewRepository.isExistsByUserIdAndPlaceId(userId, placeId) == 0 ? false : true;
    }

    @Transactional
    private ResultResponse AddReview(ReviewEventRequest event)
    {
        ResultResponse _res = new ResultResponse();
        try
        {
            _res.setType("리뷰 이벤트");
            ReviewEntity review;
            UserEntity userEntity = userRepository.findById(event.getUserId()).orElseThrow(()-> new NullPointerException("등록된 유저가 없습니다."));
            PlaceEntity placeEntity = placeRepository.findById(event.getPlaceId()).orElseThrow(()-> new NullPointerException("등록된 장소가 없습니다."));

            if(!reviewRepository.findById(event.getReviewId()).isEmpty())
            {
                _res.setSuccess(false);
                _res.setResult("이미 존재하는 Review ID 입니다.");
                return _res;
            }

            for(String id : attachedPhotoRepository.findAllIds())
            {
                for(String photoId : event.getAttachedPhotoIds())
                {
                    if(id.equals(photoId))
                    {
                        _res.setSuccess(false);
                        _res.setResult("이미 존재하는 AttachedPhoto ID 입니다.");
                        return _res;
                    }
                }
            }

            if (IsExist(event.getUserId(), event.getPlaceId()))
            {
                _res.setSuccess(false);
                _res.setResult("해당 장소에 이미 리뷰를 등록한 유저입니다.");
                return _res;
            }

            if (Objects.isNull(event.getContent()) || event.getContent().length() == 0)
            {
                _res.setSuccess(false);
                _res.setResult("작성 된 리뷰 내용이 없습니다.");
                return _res;
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
                    AddPointHistory(ActionEnum.ADD, event, "Bonus: First Registrant", ADD_POINT);
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
                AddPointHistory(ActionEnum.ADD, event, "Text Review", ADD_POINT);
            }

            // 사진 등록
            if (!Objects.isNull(event.getAttachedPhotoIds()) && Arrays.stream(event.getAttachedPhotoIds()).count() > 0)
            {
                Arrays.stream(event.getAttachedPhotoIds()).forEach(x -> attachedPhotoRepository.save(AttachedPhotoEntity.builder().attachedPhotoIDS(x).reviewEntity(review).build()));
                AddPointHistory(ActionEnum.ADD, event, "Photo Review", ADD_POINT);
            }

            _res.setSuccess(true);
            _res.setResult("리뷰가 등록되었습니다.");
            return _res;
        }
        catch (Exception ex)
        {
            _res.setSuccess(false);
            _res.setResult(ex.getMessage());
            return _res;
        }
    }

    @Transactional
    private ResultResponse ModReview(ReviewEventRequest event)
    {
        ResultResponse _res = new ResultResponse();
        try
        {
            _res.setType("리뷰 이벤트");
            ReviewEntity review = reviewRepository.findById(event.getReviewId()).orElseThrow(() -> new NullPointerException("등록된 리뷰가 없습니다."));
            if(!review.getUserEntity().getUserID().equals(event.getUserId()))
            {
                _res.setSuccess(false);
                _res.setResult("리뷰를 등록한 작성자가 아닙니다.");
                return _res;
            }

            if (Objects.isNull(event.getContent()) || event.getContent().length() == 0)
            {
                _res.setSuccess(false);
                _res.setResult("작성 된 리뷰 내용이 없습니다.");
                return _res;
            }

            List<AttachedPhotoEntity> attachedPhotoEntityList = attachedPhotoRepository.findByReviewId(event.getReviewId());
            if(attachedPhotoEntityList.stream().count() > 0)
            {
                attachedPhotoRepository.findByReviewId(event.getReviewId()).forEach(attachedPhotoRepository::delete);

                if (!Objects.isNull(event.getAttachedPhotoIds()) && Arrays.stream(event.getAttachedPhotoIds()).count() > 0)
                {
                    for(String id : attachedPhotoRepository.findAllIds())
                    {
                        for(String photoId : event.getAttachedPhotoIds())
                        {
                            if(id.equals(photoId))
                            {
                                _res.setSuccess(false);
                                _res.setResult("이미 존재하는 AttachedPhoto ID 입니다.");
                                return _res;
                            }
                        }
                    }

                    Arrays.stream(event.getAttachedPhotoIds())
                            .forEach(x -> attachedPhotoRepository.save(AttachedPhotoEntity.builder()
                                    .attachedPhotoIDS(x)
                                    .reviewEntity(review)
                                    .build())
                            );
                }
                else
                {
                    AddPointHistory(ActionEnum.MOD, event, "Empty Photo", DELETE_POINT);
                }
            }
            else
            {
                if (!Objects.isNull(event.getAttachedPhotoIds()) && Arrays.stream(event.getAttachedPhotoIds()).count() > 0)
                {
                    for(String id : attachedPhotoRepository.findAllIds())
                    {
                        for(String photoId : event.getAttachedPhotoIds())
                        {
                            if(id.equals(photoId))
                            {
                                _res.setSuccess(false);
                                _res.setResult("이미 존재하는 AttachedPhoto ID 입니다.");
                                return _res;
                            }
                        }
                    }

                    Arrays.stream(event.getAttachedPhotoIds())
                            .forEach(x -> attachedPhotoRepository.save(AttachedPhotoEntity.builder()
                                    .attachedPhotoIDS(x)
                                    .reviewEntity(review)
                                    .build())
                            );
                    AddPointHistory(ActionEnum.MOD, event, "Add Photo", ADD_POINT);
                }
            }
            review.UpdateReview(event.getContent());

            reviewRepository.save(review);

            _res.setSuccess(true);
            _res.setResult("리뷰가 수정되었습니다.");
            return _res;
        }
        catch (Exception ex)
        {
            _res.setSuccess(false);
            _res.setResult(ex.getMessage());
            return _res;
        }
    }

    @Transactional
    private ResultResponse DeleteReview(ReviewEventRequest event)
    {
        ResultResponse _res = new ResultResponse();
        try
        {
            _res.setType("리뷰 이벤트");
            ReviewEntity review = reviewRepository.findById(event.getReviewId()).orElseThrow(() -> new NullPointerException("등록된 리뷰가 없습니다."));
            if(!review.getUserEntity().getUserID().equals(event.getUserId()))
            {
                _res.setSuccess(false);
                _res.setResult("리뷰를 등록한 작성자가 아닙니다.");
                return _res;
            }

            if(review.isBonus())
            {
                AddPointHistory(ActionEnum.DELETE, event, "Delete Review: Bonus", DELETE_POINT);
            }

            if(attachedPhotoRepository.findByReviewId(event.getReviewId()).stream().count() > 0)
            {
                attachedPhotoRepository.findByReviewId(event.getReviewId()).forEach(attachedPhotoRepository::delete);
                AddPointHistory(ActionEnum.DELETE, event, "Delete Review: Content, Photo", DELETE_POINT_CONTENT_PHOTO);
            }
            else
            {
                AddPointHistory(ActionEnum.DELETE, event, "Delete Review: Content", DELETE_POINT);
            }

            reviewRepository.delete(review);

            _res.setSuccess(true);
            _res.setResult("리뷰가 삭제되었습니다.");
            return _res;
        }
        catch (Exception ex)
        {
            _res.setSuccess(false);
            _res.setResult(ex.getMessage());
            return _res;
        }
    }

    @Transactional
    private void AddPointHistory(ActionEnum action, ReviewEventRequest event, String reason, int point)
    {
        ReviewEntity review = reviewRepository.findById(event.getReviewId()).orElseThrow(() -> new NullPointerException("등록된 리뷰가 없습니다."));

        PointHistoryEntity pointHistory = PointHistoryEntity.builder()
                .pointID(UUID.randomUUID().toString())
                .pointEarned(point)
                .reason(reason)
                .userID(action == ActionEnum.ADD ? event.getUserId() : review.getUserEntity().getUserID())
                .reviewID(event.getReviewId())
                .placeID(action == ActionEnum.ADD ? event.getPlaceId(): review.getPlaceEntity().getPlaceID())
                .build();

        pointHistoryRepository.save(pointHistory);
    }

    public ResultResponse GetUserPoint(String userId)
    {
        ResultResponse _res = new ResultResponse();
        _res.setSuccess(true);
        _res.setType(userId + "님의 현재 포인트 총점 조회");
        if(userRepository.findById(userId).isEmpty())
        {
            _res.setResult("등록되지 않은 유저입니다.");
        }
        else {
            _res.setResult(Objects.isNull(pointHistoryRepository.findMyPointByUserId(userId)) ? 0 : pointHistoryRepository.findMyPointByUserId(userId));

        }
        return _res;
    }

    public ResultResponse GetUserPointDetail(String userId)
    {
        ResultResponse _res = new ResultResponse();
        _res.setSuccess(true);
        _res.setType(userId + "님의 포인트 상세내역 조회");

        if(userRepository.findById(userId).isEmpty())
        {
            _res.setResult("등록되지 않은 유저입니다.");
        }
        else
        {
            _res.setResult(pointHistoryRepository.findByUserId(userId));
        }
        return _res;
    }
}
