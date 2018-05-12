package longshot.model.condition;


import longshot.model.ErrorMessage;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.model.Turn;
import longshot.ws.BattleWebSocketService;

/**
 * Created by Naiara on 12/10/2015.
 */
public class CorrectPlayerPositionShotCondition implements Condition {
    @Override
    public boolean check(Stage stage, Player player, Turn turn) {
        return player.getPosition().equals(turn.getShot().getPosition());
    }

    @Override
    public ErrorMessage errorMessage() {
        return new ErrorMessage(BattleWebSocketService.PLAYER_OUT_OF_POSITION, "Player out of position!");
    }
}
