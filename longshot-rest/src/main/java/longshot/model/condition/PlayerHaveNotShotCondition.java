package longshot.model.condition;


import longshot.model.ErrorMessage;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.model.Turn;
import longshot.ws.BattleWebSocketService;

/**
 * Created by Naiara on 12/10/2015.
 */
public class PlayerHaveNotShotCondition implements Condition {
    @Override
    public boolean check(Stage stage, Player player, Turn turn) {
        return !stage.isActivePlayerShot();
    }

    @Override
    public ErrorMessage errorMessage() {
        return new ErrorMessage(BattleWebSocketService.SHOOT_FLAG_IS_TRUE, "Already shot!");
    }
}
