package app.TravelGo.Trip;

import app.TravelGo.Document.Document;
import app.TravelGo.Post.Post;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "trips")
public class Trip implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "trips")
    @Column(name = "id")
    private Long id;

    private LocalDate date;
    private String gatheringPlace;
    private String tripName;

    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @ToString.Exclude
    private List<Document> documents = new ArrayList<>();

    @OneToMany@JoinColumn(name = "post_id")
    private List<Post> posts = new ArrayList<>();

    private Double rate;
    private Integer numberOfRates;

    private Boolean archived;
}
