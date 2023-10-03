package app.TravelGo.dto.Auth;

import app.TravelGo.User.Role.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class AuthResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("roles")
    private Set<Role> roles;

    public AuthResponse(Long id, Set<Role> roles, String accessToken) {
        this.id = id;
        this.accessToken = accessToken;
        this.roles = roles;
    }
}
