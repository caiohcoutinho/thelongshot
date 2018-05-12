package longshot.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.beans.Transient;

/**
 * Created by Naiara on 18/09/2015.
 */
@Entity
@Table(name = "LS_USER")
public class User {
    private String id;
    private String username;
    private String picture;

    public User() {
    }

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public User(String username) {
        this.username = username;
    }

    public User(String id, String username, String sessionToken, String picture) {
        this.id = id;
        this.username = username;
        this.picture = picture;
    }

    @Id
    @GeneratedValue
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }



}
