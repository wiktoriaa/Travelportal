package app.TravelGo.dto.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {
    @JsonProperty("email")
    private String email;

    @JsonProperty("accessToken")
    private String accessToken;

    public AuthResponse(String email, String accessToken) {
        this.email = email;
        this.accessToken = accessToken;
    }
}
