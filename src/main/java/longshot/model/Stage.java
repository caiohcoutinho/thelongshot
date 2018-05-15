package longshot.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Caio Coutinho on 27/09/2015.
 */
@Entity
@Table(name = "LS_STAGE")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NamedQueries({
        @NamedQuery(name = Stage.FIND_BY_CREATOR_ID, query = Stage.QUERY_FIND_BY_CREATOR_ID),
        @NamedQuery(name = Stage.FIND_OPEN_STAGES, query = Stage.QUERY_FIND_OPEN_STAGES)
})
public abstract class Stage {
    public static final String FIND_BY_CREATOR_ID = "Stage.FindByCreatorId";
    public static final String QUERY_FIND_BY_CREATOR_ID = "SELECT s FROM Stage s WHERE s.creator = :creatorId ORDER BY s.name";
    public static final String FIND_OPEN_STAGES = "Stage.FindOpenStages";
    public static final String QUERY_FIND_OPEN_STAGES = "SELECT s FROM Stage s WHERE s.open = true AND s.creator != :creatorId ORDER BY s.name";

    private Long id;
    private String name;
    private Long playersNumber;
    private Player selectedPlayer;
    private Long activePlayerId;
    private Boolean activePlayerShot;
    private Boolean activePlayerMoved;
    private List<Player> players = new ArrayList<Player>();
    private List<ShotResult> shotResults;
    private ShotResult lastShot;
    private String creator;
    private String creatorName;
    private Date creationDate = new Date();
    private boolean open = true;

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Transient
    public Player getSelectedPlayer() {
        return selectedPlayer;
    }

    @Column(name = "active_player_id")
    public Long getActivePlayerId() {
        return activePlayerId;
    }

    public void setActivePlayerId(Long activePlayerId) {
        this.activePlayerId = activePlayerId;
    }

    @Column(name = "active_player_shot")
    public Boolean isActivePlayerShot() {
        return activePlayerShot;
    }

    public void setActivePlayerShot(Boolean activePlayerShot) {
        this.activePlayerShot = activePlayerShot;
    }

    @Column(name = "active_player_moved")
    public Boolean isActivePlayerMoved() {
        return activePlayerMoved;
    }

    public void setActivePlayerMoved(Boolean activePlayerMoved) {
        this.activePlayerMoved = activePlayerMoved;
    }

    public void setSelectedPlayer(Player selectedPlayer) {
        this.selectedPlayer = selectedPlayer;
    }

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
    @OrderBy("index ASC")
    @Column(name = "STAGE")
    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "PLAYERS_NUMBER")
    public Long getPlayersNumber() {
        return playersNumber;
    }

    public void setPlayersNumber(Long playersNumber) {
        this.playersNumber = playersNumber;
    }

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL)
    public List<ShotResult> getShotResults() {
        return shotResults;
    }

    public void setShotResults(List<ShotResult> shotResults) {
        this.shotResults = shotResults;
    }

    @Transient
    public void addShotResult(ShotResult shotResult) {
        this.getShotResults().add(shotResult);
        shotResult.setStage(this);
        this.setLastShot(shotResult);
        for (Player p : this.getPlayers()) {
            String playerId = p.getUserId();
            if (playerId.equals(shotResult.getUserId())) {
                // TODO: implement self damage
                p.setPoints(p.getPoints() + shotResult.getPoints());
            } else {
                for (Damage d : shotResult.getDamage()) {
                    if (d.getDamagedUser().equals(playerId)) {
                        long health = p.getHealth() - d.getDamage();
                        if (health < 0) {
                            health = 0;
                        }
                        p.setHealth(health);
                    }
                }
            }
        }
    }

    @Transient
    public ShotResult getLastShot() {
        return this.lastShot;
    }

    public void setLastShot(ShotResult lastShot) {
        this.lastShot = lastShot;
    }

    @Column(name = "CREATOR_ID")
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Column(name = "CREATION_DATE")
    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    @Transient
    public void addPlayer(Player player) {
        this.getPlayers().add(player);
    }

    @Column(name = "open")
    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    @Column(name = "creator_name")
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public abstract Position hit(Position start, Position end);

    public abstract Position indexPosition(int index);

    public abstract Position indexRotation(int index);

    public abstract Long maxPlayers();

    @Transient
    public Player getPlayer(String userId){
        for(Player p : this.getPlayers()){
            if(p.getUserId().equals(userId)){
                return p;
            }
        }
        return null;
    }

    public abstract boolean checkMovement(Player player, Move message);

}
