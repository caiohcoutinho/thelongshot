package longshot.model.dto;

import java.util.Date;

/**
 * Created by Caio Coutinho on 04/10/2015.
 */
public class Post {
    private String message;
    private String author;
    private long time;

    public Post() {
        this("","");
    }

    public Post(String author, String message) {
        this.author = author;
        this.message = message;
        this.time = new Date().getTime();
    }

    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
