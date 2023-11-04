package app.TravelGo.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class CreateTripRequest {
    private LocalDate date;
    private String gatheringPlace;
    private String tripName;
    private List<Long> guidesIDs;
}
