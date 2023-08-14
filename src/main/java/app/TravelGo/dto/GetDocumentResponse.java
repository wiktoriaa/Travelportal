package app.TravelGo.dto;

import app.TravelGo.Offer.Offer;
import app.TravelGo.Trip.Trip;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDocumentResponse {
    private Long id;
    private String file_name;
    private String title;
    private Trip trip;
    private Offer offer;

}
