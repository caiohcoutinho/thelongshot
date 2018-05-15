package longshot.model.condition;


import longshot.model.ErrorMessage;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.model.Turn;

/**
 * Created by Naiara on 12/10/2015.
 */
public interface Condition {
    boolean check(Stage stage, Player player, Turn turn);
    ErrorMessage errorMessage();
}
