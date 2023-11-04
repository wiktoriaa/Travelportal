package app.TravelGo.dto;
import lombok.Data;

@Data
public class UpdatePostRequest {
    private String title;
    private String content;
    private String about;
}
