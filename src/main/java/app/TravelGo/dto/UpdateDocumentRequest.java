package app.TravelGo.dto;

import lombok.Data;

@Data
public class UpdateDocumentRequest {
    private String fileName;
    private String title;
    private Long tripId;
}
