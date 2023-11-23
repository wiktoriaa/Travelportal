package app.TravelGo.dto;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UpdatePostRequest {
    private String title;
    private String content;
    private String about;
    private List<MultipartFile> images;
}
