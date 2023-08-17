package app.TravelGo.Comment;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "comments")
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "comments")
    @Column(name = "id")
    private Long id;


    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "post_id")
    private Long postId;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;


}
