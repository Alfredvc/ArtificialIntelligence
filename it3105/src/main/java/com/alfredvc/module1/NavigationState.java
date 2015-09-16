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

    private static final String patternString = "\\((.*?)\\)";
    private static final Pattern pattern = Pattern.compile(patternString);
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

    public static NavigationState fromString(String s) {
        Matcher matcher = pattern.matcher(s);
        List<String> input = new ArrayList<>();
        while (matcher.find()) {
            input.add(matcher.group().replaceAll("[()]", ""));
        }
        int xSize = intFromString(input.get(0).split(",")[0]);
        int ySize = intFromString(input.get(0).split(",")[1]);
        Point start = new Point(intFromString(input.get(1).split(",")[0]), intFromString(input.get(1).split(",")[1]));
        Point goal = new Point(intFromString(input.get(2).split(",")[0]), intFromString(input.get(2).split(",")[1]));
        List<Point> obstaclePoints = new ArrayList<>();

        for (int i = 3; i < input.size(); i++) {
            int x0 = intFromString(input.get(i).split(",")[0]);
            int y0 = intFromString(input.get(i).split(",")[1]);
            int dx = intFromString(input.get(i).split(",")[2]);
            int dy = intFromString(input.get(i).split(",")[3]);
            for (int x = 0; x < dx; x++) {
                for (int y = 0; y < dy; y++) {
                    obstaclePoints.add(new Point(x0 + x, y0 + y));
                }
            }
        }
        boolean[][] obstacles = obstacleArrayFromObstaclePoints(obstaclePoints, xSize, ySize);
        return new NavigationState(start, goal, xSize, ySize, obstacles);
    }

    private static int intFromString(String input) {
        return Integer.parseInt(input.trim());
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
