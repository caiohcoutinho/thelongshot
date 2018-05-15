package longshot.model.dto;

/**
 * Created by Caio Coutinho on 09/10/2015.
 */
public class Move {
    private Long userId;
    private Position position;
    private Position newPosition;
    private Position rotation;
    private Position newRotation;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Position newPosition) {
        this.newPosition = newPosition;
    }

    public Position getRotation() {
        return rotation;
    }

    public void setRotation(Position rotation) {
        this.rotation = rotation;
    }

    public Position getNewRotation() {
        return newRotation;
    }

    public void setNewRotation(Position newRotation) {
        this.newRotation = newRotation;
    }
}
