package app.TravelGo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserProfileResponse {
     private String username;
     private String name;
     private String surname;
     private String email;
     private Integer phoneNumber;


}
