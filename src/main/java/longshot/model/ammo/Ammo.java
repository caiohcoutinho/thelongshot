package longshot.model.ammo;

import longshot.model.dto.Shot;
import longshot.model.entity.ShotResult;
import longshot.model.entity.Stage;
import net.sourceforge.jeval.EvaluationException;

/**
 * Created by Caio Coutinho on 27/09/2015.
 */
public interface Ammo {
    ShotResult calculateFire(Shot shot, Stage stage) throws EvaluationException;
}
