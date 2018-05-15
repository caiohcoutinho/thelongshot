package longshot.model.dto;

/**
 * Created by Caio on 30/09/2015.
 */
public class Token {
    private String sessionToken;

    public Token() {
    }

    public Token(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }
}
