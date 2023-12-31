package app.TravelGo.dto;

import app.TravelGo.User.Role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String name;
    private String surname;
    private Set<Role> roles;
}
