package longshot.model.ammo;

import longshot.model.Shot;
import longshot.model.ShotResult;
import longshot.model.Stage;
import net.sourceforge.jeval.EvaluationException;

/**
 * Created by Naiara on 27/09/2015.
 */
public interface Ammo {
    ShotResult calculateFire(Shot shot, Stage stage) throws EvaluationException;
}
