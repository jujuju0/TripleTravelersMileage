package pretask.triple.mileage.model.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "place")
@ToString(of = "placeID")
@Builder
public class PlaceEntity {
    @Id
    @Column(name = "placeid")
    private String placeID;
}
