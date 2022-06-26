package pretask.triple.mileage.model.entity;

import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pointhistory")
@ToString(of = {"pointID", "pointEarned", "reason", "userID", "reviewID", "placeID", "createDate"})
@Builder
public class PointHistoryEntity {
    @Id
    @Column(name = "pointid")
    private String pointID;
    @Column(name = "pointearned")
    private int pointEarned;
    @Column(name = "reason")
    private String reason;
    @Column(name = "userid")
    private String userID;
    @Column(name = "reviewid")
    private String reviewID;
    @Column(name = "placeid")
    private String placeID;
    @Generated(GenerationTime.INSERT)
    @Column(name = "createdate")
    private Date createDate;
}
