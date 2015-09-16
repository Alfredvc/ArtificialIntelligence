package com.alfredvc.module1;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import search_algorithm.State;

/**
 * Created by Alfredvc on 8/27/2015.
 */
public class NavigationState extends State<NavigationState> {

    private final int xSize;
    private final int ySize;
    private final boolean[][] obstacles;
    private final int h;
    private Point location;

    private Point goal;

    public NavigationState(Point location, Point goal, int xSize, int ySize, boolean[][] obstacles) {
        this.location = location;
        this.goal = goal;
        this.xSize = xSize;
        this.ySize = ySize;
        this.obstacles = obstacles;
        this.h = calculateH();
    }

    public static boolean[][] obstacleArrayFromObstaclePoints(List<Point> obstaclePoints, int xSize, int ySize) {
        boolean[][] obstacles = new boolean[xSize][ySize];
        for (Point p : obstaclePoints) {
            obstacles[p.x][p.y] = true;
        }
        return obstacles;
    }

    public static List<Point> obstaclePointsFromObstacleArray(boolean[][] obstacles) {
        List<Point> obstaclePoints = new ArrayList<>();
        for (int x = 0; x < obstacles.length; x++) {
            for (int y = 0; y < obstacles[0].length; y++) {
                if (obstacles[x][y]) {
                    obstaclePoints.add(new Point(x, y));
                }
            }
        }
        return obstaclePoints;
    }


    private int calculateH() {
        return manhattanDistance(location, goal);
    }

    private int manhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public Point getLocation() {
        return this.location;
    }

    public Point getGoal() {
        return goal;
    }

    @Override
    public int getH() {
        return h;
    }

    @Override
    public boolean isASolution() {
        return h == 0;
    }

    public boolean[][] getObstacles() {
        return obstacles;
    }

    @Override
    public List<NavigationState> generateSuccessors() {
        List<NavigationState> toReturn = new ArrayList<>(4);
        if (location.x + 1 < xSize && !obstacles[location.x + 1][location.y]) {
            toReturn.add(new NavigationState(new Point(location.x + 1, location.y), goal, xSize, ySize, obstacles));
        }
        if (location.y + 1 < ySize && !obstacles[location.x][location.y + 1]) {
            toReturn.add(new NavigationState(new Point(location.x, location.y + 1), goal, xSize, ySize, obstacles));
        }
        if (location.x > 0 && !obstacles[location.x - 1][location.y]) {
            toReturn.add(new NavigationState(new Point(location.x - 1, location.y), goal, xSize, ySize, obstacles));
        }
        if (location.y > 0 && !obstacles[location.x][location.y - 1]) {
            toReturn.add(new NavigationState(new Point(location.x, location.y - 1), goal, xSize, ySize, obstacles));
        }
        return toReturn;
    }

    public int getxSize() {
        return xSize;
    }

    public int getySize() {
        return ySize;
    }

    @Override
    public int getArcCost() {
        return 1;
    }

    @Override
    public int getCostFrom(NavigationState state) {
        return manhattanDistance(location, (state).getLocation());
    }

    @Override
    public int hashCode() {
        //Szudzik's function (tested for uniqueness for x and y values of upto 10000)
        return location.x >= location.y ? location.x * location.x + location.x + location.y : location.x + location.y * location.y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NavigationState)) {
            return false;
        }
        final NavigationState other = (NavigationState) obj;
        return this.getLocation().equals(other.getLocation());
    }

    @Override
    public String toString() {
        return String.format("x=%d y=%d", location.x, location.y);
    }
}
