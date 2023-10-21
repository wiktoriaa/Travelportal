package app.TravelGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPostResponse {
    private Long id;
    private String title;
    private String content;
    private String username;
    private Long userID;
    private String about;
    private LocalDateTime createdAt;
    private String status;
    private Integer likes;
    private String imagesDir;
}
