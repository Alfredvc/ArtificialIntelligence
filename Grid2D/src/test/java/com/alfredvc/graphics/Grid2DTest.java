package com.alfredvc.graphics;

import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit test for simple Grid2D.
 */
public class Grid2DTest {
    @Test
    public void grid2DTest_1() {
        Random r = new Random();
        int size = 100;
        Color backgroundColor = Color.blue;
        Color pointColor = Color.red;
        Grid2DBuilder builder = new Grid2DBuilder()
                .setBackgroundColor(backgroundColor)
                .setGridWidth(size)
                .setGridHeight(size);
        final Grid2D grid2D = builder.createGrid2D();

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                assertThat(grid2D.getColor(new Point(x, y)), is(backgroundColor));
            }
        }

        List<Point> points = new ArrayList<>();
        for (int i = 0; i < size * size / 10; i++) {
            points.add(new Point(r.nextInt(size - 1), r.nextInt(size - 1)));
        }
        grid2D.setPoints(points, pointColor);

        for (Point p : points) {
            assertThat(grid2D.getColor(p), is(pointColor));
        }

    }

}
