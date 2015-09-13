package com.alfredvc.module2;

/**
 * Created by erpa_ on 9/13/2015.
 */
public class PointLine {

    public static int width = 3;

    private final DoublePoint from;
    private final DoublePoint to;

    public PointLine(DoublePoint from, DoublePoint to) {
        this.from = from;
        this.to = to;
    }

    public DoublePoint getFrom() {
        return from;
    }

    public DoublePoint getTo() {
        return to;
    }

    @Override
    public String toString() {
        return "PointLine{" +
                "from=" + from +
                ", to=" + to +
                '}';
    }
}
