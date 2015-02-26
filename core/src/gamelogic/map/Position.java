package gamelogic.map;

/**
 * This class represents a 2D (x, y) coordinates.
 */
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

    public static float getDistance(Position position1, Position position2) {
        float dx = position1.x - position2.x;
        float dy = position1.y - position2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public boolean equals(Object o) {
        if (o instanceof Position) {
            Position pos = (Position) o;
            return (x == pos.getX() && y == pos.getY());
        }
        return false;

    }
}

