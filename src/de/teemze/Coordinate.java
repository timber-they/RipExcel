package de.teemze;

/**
 * Adapted from the Numericat project
 */
public class Coordinate implements Comparable
{
    private final double x;
    private final double y;

    public Coordinate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int roundX() {
        return (int) Math.round(x);
    }

    public int roundY() {
        return (int) Math.round(y);
    }

    public double getY(){
        return y;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof Coordinate))
            return 0;
        return Double.compare(x, ((Coordinate) o).x);
    }
}
