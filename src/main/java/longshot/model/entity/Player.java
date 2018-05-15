package longshot.model.entity;

import longshot.model.dto.Position;

import javax.persistence.*;

/**
 * Created by Caio Coutinho on 27/09/2015.
 */
@Entity
@Table(name = "LS_PLAYER")
public class Player {
    private Long id;
    private Stage stage;
    private String userId;
    private String userName;
    private Long index;
    private Position position;
    private Position rotation;
    private String skin;
    private Long points;
    private Long health;
    private Long fuel;

    public Player() {
    }

    public Player(Stage stage, Long index, String userId, String username, Position position, Position rotation, String skin) {
        this.stage = stage;
        this.index = index;
        this.userId = userId;
        this.userName = username;
        this.position = position;
        this.rotation = rotation;
        this.skin = skin;
        this.points = 0L;
        this.health = 1000L;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "stage")
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Column(name = "INDEX")
    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    @Column(name = "USER_ID")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Column(name = "USER_NAME")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Embedded
    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="x", column = @Column(name="rotx")),
            @AttributeOverride(name="y", column = @Column(name="roty")),
            @AttributeOverride(name="z", column = @Column(name="rotz"))
    })
    public Position getRotation() {
        return rotation;
    }

    public void setRotation(Position rotation) {
        this.rotation = rotation;
    }

    @Column(name = "SKIN")
    public String getSkin() {
        return skin;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    @Column(name = "POINTS")
    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @Column(name = "HEALTH")
    public Long getHealth() {
        return health;
    }

    public void setHealth(Long health) {
        this.health = health;
    }

    @Column(name = "FUEL")
    public Long getFuel() {
        return fuel;
    }

    public void setFuel(Long fuel) {
        this.fuel = fuel;
    }
}
