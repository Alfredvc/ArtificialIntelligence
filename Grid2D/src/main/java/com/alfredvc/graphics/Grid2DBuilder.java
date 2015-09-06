package com.alfredvc.graphics;

import java.awt.*;

public class Grid2DBuilder {
    private int ySize;
    private int xSize;
    private Color backgroundColor = Color.white;
    private int gridSizePixels = 5;

    public Grid2DBuilder setySize(int ySize) {
        this.ySize = ySize;
        return this;
    }

    public Grid2DBuilder setxSize(int xSize) {
        this.xSize = xSize;
        return this;
    }

    public Grid2DBuilder setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Grid2DBuilder setGridSizePixels(int gridSizePixels) {
        this.gridSizePixels = gridSizePixels;
        return this;
    }

    public Grid2D createGrid2D() {
        return new Grid2D(ySize, xSize, gridSizePixels, backgroundColor);
    }
}