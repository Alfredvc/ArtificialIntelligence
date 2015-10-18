package com.alfredvc.module4;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Player2048Test {
    @Ignore
    @Test
    public void benchmark() {
        //Player2048 p = new Player2048(3);
        FJPlayer2048 p = new FJPlayer2048(4, FJPlayer2048.Mode.PARALLEL);
        //ForkJoinPlayer2048 p = new ForkJoinPlayer2048(10);
        long start = System.nanoTime();
        p.play();
        long finish = System.nanoTime();
        System.out.println("Took : " + (finish - start)/1000000);
        System.out.println(Arrays.toString(p.getLogic().counters));
        while (true);
    }
}
