package app.TravelGo.dto.Auth;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

@Getter
public class AuthRequest {
    @NotNull
    private String email = "";

    @NotNull
    private String password = "";
}
