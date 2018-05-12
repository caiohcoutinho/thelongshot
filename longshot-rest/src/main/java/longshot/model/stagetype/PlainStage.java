package longshot.model.stagetype;

import longshot.model.Move;
import longshot.model.Player;
import longshot.model.Position;
import longshot.model.Stage;

import javax.persistence.Entity;
import javax.persistence.Transient;

/**
 * Created by Naiara on 27/09/2015.
 */
@Entity
public class PlainStage extends Stage {

    @Override
    @Transient
    public Position hit(Position start, Position end) {
        Double x1 = start.getX();
        Double x2 = end.getX();
        Double y1 = start.getY();
        Double y2 = end.getY();
        Double z1 = start.getZ();
        Double z2 = end.getZ();
        if(y1 * y2 > 0){
            return null;
        }
        if(y1 == 0 && y2 > 0){
            return null;
        }
        if(y2 == 0){
            return end;
        }
        Double t = y1 / new Double(y2 - y1);
        return new Position(
                new Double(x1+t*(x2-x1)),
                new Double(0),
                new Double(z1+t*(z2-z1))
        );
    }

    @Override
    @Transient
    public Position indexPosition(int index) {
        Position pos = null;
        Double dist = 20.0;
        switch (index){
            case 0: pos = new Position(dist, 0.0, 0.0);
                break;
            case 1: pos = new Position(0.0, 0.0, dist);
                break;
            case 2: pos = new Position(-dist, 0.0, 0.0);
                break;
            case 3: pos = new Position(0.0, 0.0, dist);
                break;
        }
        return pos;
    }

    @Override
    public Position indexRotation(int index) {
        return new Position(0.0, 0.0, 0.0);
    }


    @Override
    @Transient
    public Long maxPlayers() {
        return 2L;
    }

    @Override
    @Transient
    public boolean checkMovement(Player player, Move message) {
        boolean res = true;
        return res;
    }
}
