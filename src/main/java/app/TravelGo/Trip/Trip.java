package app.TravelGo.Trip;

import app.TravelGo.Document.Document;
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
    private String gathering_place;
    private String trip_name;

    @OneToMany(mappedBy = "trip", fetch = FetchType.LAZY, cascade = {CascadeType.ALL})
    @ToString.Exclude
    private List<Document> documents = new ArrayList<>();

    private Double rate;
    private Integer number_of_rates;

    private Boolean archived;
}
