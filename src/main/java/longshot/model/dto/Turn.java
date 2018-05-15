package longshot.model.dto;

/**
 * Created by Caio Coutinho on 09/10/2015.
 */
public class Turn {
    private Move move;
    private Shot shot;
    private Long activePlayerId;

    public Move getMove() {
        return move;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public Shot getShot() {
        return shot;
    }

    public void setShot(Shot shot) {
        this.shot = shot;
    }

    public Long getActivePlayerId() {
        return activePlayerId;
    }

    public void setActivePlayerId(Long activePlayerId) {
        this.activePlayerId = activePlayerId;
    }
}
