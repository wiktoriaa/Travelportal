package app.TravelGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetOfferResponse {
    private Long id;
    private LocalDate start_date;
    private LocalDate end_date;
    private Integer price;
}
