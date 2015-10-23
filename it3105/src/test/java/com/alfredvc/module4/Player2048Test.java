package com.alfredvc.module4;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Player2048Test {
    @Ignore
    @Test
    public void testOne() throws IOException {
        FJPlayer2048 p = new FJPlayer2048(4, FJPlayer2048.Mode.PARALLEL, new Logic2048());
        System.out.println(p.play());
        System.in.read();
    }

    @Ignore
    @Test
    public void benchmark() {
        int tries = 4;
        FJPlayer2048.FinalStats[] finalStats = new FJPlayer2048.FinalStats[tries];
        int sum = 0;
        for (int i = 0; i < tries; i++) {

            FJPlayer2048 p = new FJPlayer2048(5, FJPlayer2048.Mode.PARALLEL, new Logic2048(i,i));
            finalStats[i] = p.play();
            System.out.println(finalStats[i]);
            sum += finalStats[i].finalScore;
            p.close();
        }
        System.out.println(sum/ tries);
        while (true);
    }
}
