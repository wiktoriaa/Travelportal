package app.TravelGo.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UpdateUserProfileRequest {
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private MultipartFile profileImage;

}
