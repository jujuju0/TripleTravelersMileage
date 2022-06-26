package pretask.triple.mileage.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pretask.triple.mileage.model.enums.ActionEnum;
import pretask.triple.mileage.model.enums.TypeEnum;

@Getter
@Setter
@NoArgsConstructor
public class ReviewEventRequest {
    private TypeEnum type;
    private ActionEnum action;
    private String reviewId;
    private String content;
    private String[] attachedPhotoIds;
    private String userId;
    private String placeId;
}
