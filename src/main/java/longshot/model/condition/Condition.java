package longshot.model.condition;


import longshot.model.dto.ErrorMessage;
import longshot.model.entity.Player;
import longshot.model.entity.Stage;
import longshot.model.dto.Turn;

/**
 * Created by Caio Coutinho on 12/10/2015.
 */
public interface Condition {
    boolean check(Stage stage, Player player, Turn turn);
    ErrorMessage errorMessage();
}
