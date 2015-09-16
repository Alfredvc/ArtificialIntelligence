package com.alfredvc.module2;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.*;

/**
 * Created by erpa_ on 9/13/2015.
 */
class Graph2D extends JPanel {
    private List<DoublePoint> points;
    private List<PointLine> lines;
    private double maxX;
    private double maxY;
    private double minX;
    private double minY;
    private double minSize;
    private double deltaX;
    private double deltaY;
    private static final List<Color> colorForInt;
    static {
        List<Color> list = new ArrayList<>();
        list.add(Color.white);
        list.add(Color.BLUE);
        list.add(Color.RED);
        list.add(Color.GREEN);
        list.add(Color.ORANGE);
        list.add(Color.YELLOW);
        list.add(Color.PINK);
        list.add(Color.MAGENTA);
        list.add(Color.CYAN);
        list.add(Color.DARK_GRAY);
        list.add(Color.LIGHT_GRAY);
        colorForInt = Collections.unmodifiableList(list);
    }

    private final BasicStroke lineStroke;
    private final BasicStroke pointStroke;

    public Graph2D(List<DoublePoint> points, List<PointLine> lines) {
        this.points = points;
        this.lines = lines;

        lineStroke = new BasicStroke(PointLine.width);
        pointStroke = new BasicStroke(2);

        maxX = Integer.MIN_VALUE;
        maxY = Integer.MIN_VALUE;
        minX = Integer.MAX_VALUE;
        minY = Integer.MAX_VALUE;
        
        for (DoublePoint p : points) {
            if (p.getX() < minX) minX = p.getX();
            if (p.getX() > maxX) maxX = p.getX();
            if (p.getY() < minY) minY = p.getY();
            if (p.getY() > maxY) maxY = p.getY();
        }
    }

    public void setAllWhite() {
        for (DoublePoint p : points) {
            p.setColor(0);
        }
    }

    public void setPointColor(int point, int color) {
        points.get(point).setColor(color);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());

        Graphics2D g2 = (Graphics2D) g;

        double width = getWidth() - 20;
        double height = getHeight() - 20;

        minSize = width > height ? height : width;


        deltaX = (width - minSize) / 2 + 10;
        deltaY = (height - minSize) / 2 + 10;

        for (PointLine line : lines) {
            drawLine(g2, line);
        }

        for (DoublePoint point : points) {
            drawPoint(g2, point);
        }
    }
    
    private void drawLine(Graphics2D g, PointLine line) {
        g.setPaint(Color.BLACK);
        g.setStroke(lineStroke);
        int fromX = getX(line.getFrom().getX());
        int fromY = getY(line.getFrom().getY());
        int toX = getX(line.getTo().getX());
        int toY = getY(line.getTo().getY());
        g.drawLine(fromX, fromY, toX, toY);
    }

    private void drawPoint(Graphics2D g, DoublePoint point) {
        int x = getX(point.getX()) - DoublePoint.DIAMETER/2;
        int y = getY(point.getY()) - DoublePoint.DIAMETER/2;
        g.setPaint(Color.black);
        g.setStroke(pointStroke);
        g.drawOval(x, y, DoublePoint.DIAMETER, DoublePoint.DIAMETER);
        g.setPaint(colorForInt.get(point.getColor()));
        g.fillOval(x, y, DoublePoint.DIAMETER, DoublePoint.DIAMETER);
    }

    private int getX(double x) {
        return (int) ((x - minX) * minSize / (maxX - minX) + deltaX);
    }

    private int getY(double y) {
        return (int) ((y - minY) * minSize / (maxY - minY) + deltaY);
    }


}
