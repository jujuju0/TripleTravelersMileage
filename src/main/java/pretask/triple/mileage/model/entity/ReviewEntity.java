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
@Table(name = "review")
@ToString(of = {"reviewID", "content", "bonus", "createDate", "updateDate"})
@Builder
public class ReviewEntity {
    @Id
    @Column(name = "reviewid")
    private String reviewID;
    @Column(name = "content")
    private String content;
    @Column(name = "bonus")
    private boolean bonus;
    @Generated(GenerationTime.INSERT)
    @Column(name = "createdate")
    private Date createDate;
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "updatedate")
    private Date updateDate;

    @ManyToOne
    @JoinColumn(name = "userid")
    private UserEntity userEntity;
    @ManyToOne
    @JoinColumn(name = "placeid")
    private PlaceEntity placeEntity;

    public void UpdateReview(String content)
    {
        this.content = content;
    }
}
