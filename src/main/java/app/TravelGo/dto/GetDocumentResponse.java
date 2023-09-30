package app.TravelGo.dto;

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
    private String fileName;
    private String title;
    private Long tripId;
    private String username;
}
