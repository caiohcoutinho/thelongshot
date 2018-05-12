package longshot.model.condition;


import longshot.model.ErrorMessage;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.model.Turn;
import longshot.ws.BattleWebSocketService;

/**
 * Created by Naiara on 12/10/2015.
 */
public class StageValidatedMovementCondition implements Condition {
    @Override
    public boolean check(Stage stage, Player player, Turn turn) {
        return stage.checkMovement(player, turn.getMove());
    }

    @Override
    public ErrorMessage errorMessage() {
        return new ErrorMessage(BattleWebSocketService.MOVEMENT_INVALID_ON_STAGE, "Movement is invalid on this stage!");
    }
}
