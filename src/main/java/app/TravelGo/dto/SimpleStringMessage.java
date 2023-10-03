package app.TravelGo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimpleStringMessage {
    private String message;

    public SimpleStringMessage(String message) {
        this.message = message;
    }
}
