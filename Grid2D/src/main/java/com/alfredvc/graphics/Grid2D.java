package com.alfredvc.graphics;


import java.awt.*;
import java.util.List;

import javax.swing.*;

/**
 * Displays a grid in two dimensions, each cell in the grid can be given a Color.
 */
public final class Grid2D extends JPanel {
    private final int xSize;
    private final int ySize;
    private final Color defaultBackgroundColor;
    private Color[][] grid;

    /**
     * Creates a new Grid2D instance
     * @param gridHeight the grid height
     * @param gridWidth the grid width
     * @param gridCellLengthPixels the length of each grid cell in pixels
     * @param defaultBackgroundColor the color to be used as background
     */
    public Grid2D(int gridWidth,int gridHeight, int gridCellLengthPixels, Color defaultBackgroundColor) {
        this.ySize = gridHeight;
        this.xSize = gridWidth;
        this.defaultBackgroundColor = defaultBackgroundColor;
        this.grid = new Color[gridWidth][gridHeight];
        paintBackground();
        setPreferredSize(new Dimension(gridCellLengthPixels * gridWidth, gridCellLengthPixels * gridHeight));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.clearRect(0, 0, getWidth(), getHeight());
        int rectWidth = getWidth() / xSize;
        int rectHeight = getHeight() / ySize;

        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                g.setColor(grid[x][ySize - 1 - y]);
                g.fillRect(x * rectWidth, y * rectHeight, rectWidth, rectHeight);
            }
        }

    }

    /**
     * Sets all the given points to the given color
     *
     * For example:
     * Grid2D grid = new Grid2D(5,5,10,Color.WHITE);
     * Point a = new Point(0,0);
     * Point b = new Point(1,1);
     * Point c = new Point(1,1);
     * setPoints(Arrays.asList(a,b,c),Color.RED);
     *
     * @param points the points to be set
     * @param color the color to be set to
     */
    public void setPoints(List<Point> points, Color color) {
        for (Point point : points) {
            setPoint(point, color);
        }
    }

    /**
     * Sets a single point to the given color
     * @param point the point to be set
     * @param color the color to be set to
     */
    public void setPoint(Point point, Color color) {
        grid[point.x][point.y] = color;
    }

    private void paintBackground() {
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                grid[x][y] = defaultBackgroundColor;
            }
        }
    }

    /**
     * Retrieves the color for the given point
     * @param p the point on the grid
     * @return the color of the point
     */
    public Color getColor(Point p) {
        return grid[p.x][p.y];
    }


}
