package com.alfredvc.module2;

/**
 * Created by erpa_ on 9/13/2015.
 */
public class DoublePoint {

    public static final int DIAMETER = 10;

    private final double x;
    private final double y;
    private int color;

    public DoublePoint(double x, double y) {
        this.x = x;
        this.y = y;
        this.color = 0;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "DoublePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
