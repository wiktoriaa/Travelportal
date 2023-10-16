package app.TravelGo.dto;

import lombok.*;

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
    private Long tripGuideId;
}
