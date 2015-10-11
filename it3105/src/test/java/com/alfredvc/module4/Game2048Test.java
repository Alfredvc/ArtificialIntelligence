package com.alfredvc.module4;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Game2048Test {
    @Ignore
    @Test
    public void test() throws InterruptedException {
        Game2048 g = new Game2048(new Logic2048());
        g.start();
        while (true);
    }
}
