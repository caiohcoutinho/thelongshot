package longshot.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * Created by Naiara on 27/09/2015.
 */
@Embeddable
public class Position {
    private Double x;
    private Double y;
    private Double z;

    public Position() {
    }

    public Position(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Column(name = "x")
    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    @Column(name = "y")
    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Column(name = "z")
    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    @Transient
    public Position plus(Position position){
        return new Position(this.getX()+position.getX(),
                this.getY()+position.getY(),
                this.getZ()+position.getZ());
    }

    @Transient
    public Position minus(Position position) {
        return new Position(this.getX() - position.getX(),
                this.getY() - position.getY(),
                this.getZ() - position.getZ());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (getX() != null ? !getX().equals(position.getX()) : position.getX() != null) return false;
        if (getY() != null ? !getY().equals(position.getY()) : position.getY() != null) return false;
        return !(getZ() != null ? !getZ().equals(position.getZ()) : position.getZ() != null);

    }

    @Override
    public int hashCode() {
        int result = getX() != null ? getX().hashCode() : 0;
        result = 31 * result + (getY() != null ? getY().hashCode() : 0);
        result = 31 * result + (getZ() != null ? getZ().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
