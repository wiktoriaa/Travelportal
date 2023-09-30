package app.TravelGo.dto;

import lombok.*;

import java.time.LocalDate;

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
}
