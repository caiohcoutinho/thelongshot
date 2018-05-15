package longshot.model.entity;

import javax.persistence.*;

/**
 * Created by Caio Coutinho on 27/09/2015.
 */
@Entity
@Table(name = "LS_DAMAGE")
public class Damage {
    private Long id;
    private ShotResult shotResult;
    private String damagedUser;
    private Long damage;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne
    @JoinColumn(name = "SHOT_RESULT_ID")
    public ShotResult getShotResult() {
        return shotResult;
    }

    public void setShotResult(ShotResult shotResult) {
        this.shotResult = shotResult;
    }

    @Column(name = "DAMAGED_USER_ID")
    public String getDamagedUser() {
        return damagedUser;
    }

    public void setDamagedUser(String damagedUser) {
        this.damagedUser = damagedUser;
    }

    @Column(name = "DAMAGE")
    public Long getDamage() {
        return damage;
    }

    public void setDamage(Long damage) {
        this.damage = damage;
    }
}
