package app.TravelGo.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTripRequest {
    private LocalDate date;
    private String gathering_place;
    private String trip_name;
}
