package app.TravelGo.User;

import lombok.Data;

@Data
public class SignUpDto {
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
}