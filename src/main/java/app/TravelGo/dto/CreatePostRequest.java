package app.TravelGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {
    private String title;
    private String content;
    private String username;
    private String about;
    private Integer status;
    private Integer likes;
}
