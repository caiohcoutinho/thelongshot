package longshot.model.condition;


import longshot.model.ErrorMessage;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.model.Turn;
import longshot.ws.BattleWebSocketService;

/**
 * Created by Naiara on 12/10/2015.
 */
public class PlayerHaveNotMovedCondition implements Condition {
    @Override
    public boolean check(Stage stage, Player player, Turn turn) {
        return !stage.isActivePlayerMoved();
    }

    @Override
    public ErrorMessage errorMessage() {
        return new ErrorMessage(BattleWebSocketService.MOVE_FLAG_IS_TRUE, "Player already moved this turn!");
    }
}
