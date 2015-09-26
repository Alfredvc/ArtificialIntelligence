package com.alfredvc.graphics;


import java.awt.*;

/**
 * Builder used to create valid Grid2D objects
 */
public class Grid2DBuilder {
    private int gridHeight;
    private int gridWidth;
    private Color backgroundColor = Color.white;
    private int gridSizeInPixels = 5;

    public Grid2DBuilder() {

    }
    public Grid2DBuilder setGridHeight(int gridHeight) {
        this.gridHeight = gridHeight;
        return this;
    }

    public Grid2DBuilder setGridWidth(int gridWidth) {
        this.gridWidth = gridWidth;
        return this;
    }

    public Grid2DBuilder setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Grid2DBuilder setGridSizeInPixels(int gridSizeInPixels) {
        this.gridSizeInPixels = gridSizeInPixels;
        return this;
    }

    public Grid2D createGrid2D() {
        if (gridHeight <0 || gridWidth < 0 || gridSizeInPixels < 1 || backgroundColor == null ) {
            throw new IllegalStateException("Valid Grid2D cannot be created with given parameters");
        }
        return new Grid2D(gridWidth, gridHeight, gridSizeInPixels, backgroundColor);
    }
}