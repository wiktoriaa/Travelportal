package app.TravelGo.Trip;

import app.TravelGo.Document.Document;
import app.TravelGo.Post.Post;
import app.TravelGo.User.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
@Entity
@Table(name = "trips")
@JsonIgnoreProperties("documents")
public class Trip implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "trips")
    @Column(name = "id")
    private Long id;

    private LocalDate date;
    private String gatheringPlace;
    private String tripName;
    private String description;

    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @ToString.Exclude
    @JsonIgnore
    private List<Document> documents = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "post_id")
    private List<Post> posts = new ArrayList<>();

    private Double rate;
    private Integer numberOfRates;

    private Boolean archived;

    @ElementCollection
    private Map<User, Double> userRates = new HashMap<>();

    @ManyToMany
    @JoinTable(
            name = "trip_trip_guide",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "trip_guide_id")
    )
    private List<User> tripGuides = new ArrayList<>();


    @ManyToMany(mappedBy = "enrolledTrips", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<User> participants = new HashSet<>();

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Trip trip = (Trip) obj;
        return Objects.equals(id, trip.id);
    }


}
