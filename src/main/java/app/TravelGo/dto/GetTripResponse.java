package app.TravelGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
    private List<String> participants;
    private List<Long> tripGuides;
}
