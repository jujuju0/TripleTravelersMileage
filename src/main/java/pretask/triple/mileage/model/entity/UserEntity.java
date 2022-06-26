package pretask.triple.mileage.model.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tripleuser")
@ToString(of = "userID")
@Builder
public class UserEntity {
    @Id
    @Column(name = "userid")
    private String userID;
}
