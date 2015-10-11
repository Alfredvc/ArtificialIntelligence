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
        //24816, 23505, 2.435
        Player2048 p = new Player2048(4);
        //ForkJoinPlayer2048 p = new ForkJoinPlayer2048(3);
        long start = System.nanoTime();
        p.play();
        long finish = System.nanoTime();
        System.out.println("Took : " + (finish - start)/1000000);
        System.out.println(Arrays.toString(p.getLogic().counters));
        while (true);
    }
}
