package app.TravelGo.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentRequest {
    private String fileName;
    private String title;
    private String username;
    private MultipartFile file;
}
