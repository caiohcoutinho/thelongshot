package longshot.model;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Naiara on 27/09/2015.
 */
@Entity
@Table(name = "LS_SHOT_RESULT")
public class ShotResult {
    private Long id;
    private String userId;
    private Position start;
    private Position end;
    private String fx;
    private String fy;
    private String fz;
    private Long t;
    private Long points;
    private List<Damage> damage;
    private Stage stage;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "USER_ID")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "startx")),
            @AttributeOverride(name = "y", column = @Column(name = "starty")),
            @AttributeOverride(name = "z", column = @Column(name = "startz"))
    })
    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "endx")),
            @AttributeOverride(name = "y", column = @Column(name = "endy")),
            @AttributeOverride(name = "z", column = @Column(name = "endz"))
    })
    public Position getEnd() {
        return end;
    }

    public void setEnd(Position end) {
        this.end = end;
    }

    @Column(name = "fx")
    public String getFx() {
        return fx;
    }

    public void setFx(String fx) {
        this.fx = fx;
    }

    @Column(name = "fy")
    public String getFy() {
        return fy;
    }

    public void setFy(String fy) {
        this.fy = fy;
    }

    @Column(name = "fz")
    public String getFz() {
        return fz;
    }

    public void setFz(String fz) {
        this.fz = fz;
    }

    @Column(name = "POINTS")
    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    @OneToMany(mappedBy = "shotResult", cascade = CascadeType.ALL)
    public List<Damage> getDamage() {
        return damage;
    }

    public void setDamage(List<Damage> damage) {
        this.damage = damage;
    }

    @ManyToOne
    @JoinColumn(name = "stage_id")
    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Column(name = "t")
    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }
}
