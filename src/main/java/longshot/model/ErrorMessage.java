package longshot.model;

/**
 * Created by Naiara on 22/09/2015.
 */
public class ErrorMessage {
    private final Long code;
    private final String message;

    public ErrorMessage(){
        this.code = null;
        this.message = null;
    }

    public ErrorMessage(Long code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public Long getCode() {
        return code;
    }
}
