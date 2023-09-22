package app.TravelGo.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {
    private String fileName;
    private String title;
    private Long tripId;

}
