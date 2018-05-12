package longshot.model.condition;

import longshot.model.ErrorMessage;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.model.Turn;
import longshot.ws.BattleWebSocketService;

/**
 * Created by Naiara on 12/10/2015.
 */
public class FuelIsNotNullCondition implements Condition {
    @Override
    public boolean check(Stage stage, Player player, Turn turn) {
        return player.getFuel() != null;
    }

    @Override
    public ErrorMessage errorMessage() {
        return new ErrorMessage(BattleWebSocketService.FUEL_IS_NULL, "Player fuel is null!");
    }
}
