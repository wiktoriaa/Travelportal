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
    private String gathering_place;
    private String trip_name;
    private Double rate;
    private Integer number_of_rates;
    private Boolean archived;
}
