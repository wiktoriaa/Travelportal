package app.TravelGo.dto;

import app.TravelGo.User.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetTripResponse {
    private Long id;
    private LocalDate date;
    private String gatheringPlace;
    private String tripName;
    private Double rate;
    private Integer numberOfRates;
    private Boolean archived;
    private Set<User> participants;
    private List<Long> tripGuides;
}
