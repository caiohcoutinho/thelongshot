package longshot.model.ammo;

import longshot.model.*;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Naiara on 27/09/2015.
 */
public class ALCA50 implements Ammo {

    private static final DecimalFormat DECIMAL_FORMAT;

    static {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols();
        otherSymbols.setDecimalSeparator('.');
        DECIMAL_FORMAT = new DecimalFormat("#.#########", otherSymbols);
    }

    @Override
    public ShotResult calculateFire(Shot shot, Stage stage) throws EvaluationException {
        Evaluator evaluator = new Evaluator();
        Map<String, Object> variables = new HashMap<String, Object>();
        Position position = shot.getPosition();
        variables.put("X0", this.format(position.getX()));
        variables.put("Y0", this.format(position.getY()));
        variables.put("Z0", this.format(position.getZ()));
        variables.put("E", this.format(Math.E));
        variables.put("PI", this.format(Math.PI));
        for(int i = 0; i < 10; i++){
            variables.put("V"+i, this.format(shot.getVariables().get(i)));
        }
        Position lastPoint = null;
        Position hit = null;
        String jevalFx = shot.getJevalFx();
        String jevalFy = shot.getJevalFy();
        String jevalFz = shot.getJevalFz();
        int t = -1;
        for (int i = 0; i <= 100; i++) {
            variables.put("t", this.format(i));
            evaluator.setVariables(variables);
            Double xt = this.evalAndCast(evaluator, jevalFx);
            Double yt = this.evalAndCast(evaluator, jevalFy);
            Double zt = this.evalAndCast(evaluator, jevalFz);
            Position point = new Position(xt, yt, zt);
            if(i == 0){
                Position diff = point.minus(position);
                if(diff.getX() > 2 || diff.getY() > 2 || diff.getZ() > 2){
                    throw new RuntimeException("O tiro deve partir da posição do seu veículo (X0, Y0, Z0).");
                }
            }
            if (lastPoint != null) {
                hit = stage.hit(lastPoint, point);
            }
            if (hit != null) {
                break;
            }
            lastPoint = point;
            t++;
        }

        if(hit == null){
            hit = lastPoint;
        }

        ShotResult shotResult = new ShotResult();
        shotResult.setEnd(hit);
        shotResult.setFx(shot.getFx());
        shotResult.setFy(shot.getFy());
        shotResult.setFz(shot.getFz());
        shotResult.setStart(position);
        shotResult.setT((long) t);

        variables.put("hx", this.format(hit.getX()));
        variables.put("hy", this.format(hit.getY()));
        variables.put("hz", this.format(hit.getZ()));
        Long totalPoints = 0L;
        List<Damage> damageList = new ArrayList<Damage>();
        for(Player p : stage.getPlayers()){
            if(!p.getUserId().equals(shot.getUserId())){
                Position playerPosition = p.getPosition();
                variables.put("x", this.format(playerPosition.getX()));
                variables.put("y", this.format(playerPosition.getY()));
                variables.put("z", this.format(playerPosition.getZ()));
                evaluator.setVariables(variables);
                Double distance = this.evalAndCast(evaluator, "pow(pow(#{x}-#{hx}, 2)+pow(#{y}-#{hy}, 2)+pow(#{z}-#{hz}, 2), 1/2)");
                variables.put("d", distance+"");
                evaluator.setVariables(variables);
                Long points = new Long(Math.round(this.evalAndCast(evaluator, "1010*pow(#{E}, -0.230756*#{d})-10")));
                if(points <= 0){
                    points = 0L;
                }
                totalPoints += points;
                Long damageValue = new Long(Math.round(this.evalAndCast(evaluator, "250 - (250*#{d})/7")));
                if(damageValue > 0){
                    Damage damage = new Damage();
                    damage.setDamage(damageValue);
                    damage.setDamagedUser(p.getUserId());
                    damage.setShotResult(shotResult);
                    damageList.add(damage);
                }
            }
        }
        shotResult.setPoints(totalPoints);
        shotResult.setEnd(hit);
        shotResult.setUserId(shot.getUserId());
        shotResult.setDamage(damageList);

        return shotResult;

    }

    private String format(Object value){
        return DECIMAL_FORMAT.format(value);
    }

    private Double evalAndCast(Evaluator evaluator, String string) throws EvaluationException {
        return new Double(evaluator.evaluate(string));
    }
}
