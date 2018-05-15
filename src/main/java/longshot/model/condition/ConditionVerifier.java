package longshot.model.condition;


import longshot.model.ErrorMessage;
import longshot.model.Player;
import longshot.model.Stage;
import longshot.model.Turn;

/**
 * Created by Naiara on 12/10/2015.
 */
public class ConditionVerifier {
    private Condition[] conditions;

    public ConditionVerifier(Condition... conditions) {
        this.conditions = conditions;
    }

    public ErrorMessage verify(Stage stage, Player player, Turn turn){
        for(Condition condition : this.conditions){
            if(!condition.check(stage, player, turn)){
                return condition.errorMessage();
            }
        }
        return null;
    }
}
