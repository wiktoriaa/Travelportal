package app.TravelGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetPostResponse {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private String status;
    private Integer likes;
}
