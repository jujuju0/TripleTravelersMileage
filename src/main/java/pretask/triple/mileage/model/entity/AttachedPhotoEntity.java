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
@Table(name = "attachedphoto")
@ToString(of = {"attachedPhotoIDS", "createDate", "updateDate"})
@Builder
public class AttachedPhotoEntity {
    @Id
    @Column(name = "attachedphotoids")
    private String attachedPhotoIDS;
    @Generated(GenerationTime.INSERT)
    @Column(name = "createdate")
    private Date createDate;
    @Generated(GenerationTime.ALWAYS)
    @Column(name = "updatedate")
    private Date updateDate;

    @ManyToOne
    @JoinColumn(name = "reviewid")
    private ReviewEntity reviewEntity;
}
