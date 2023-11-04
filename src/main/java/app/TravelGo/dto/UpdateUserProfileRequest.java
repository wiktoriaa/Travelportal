package app.TravelGo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateUserProfileRequest {
    private String name;
    private String surname;
    private String email;
    private Integer phoneNumber;
}
