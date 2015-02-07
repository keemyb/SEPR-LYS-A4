package gamelogic.map;

public class Position {
    private float x;
    private float y;

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position pos = (Position) o;
            return (x == pos.getX() && y == pos.getY());
        }
        return false;

    }
}

