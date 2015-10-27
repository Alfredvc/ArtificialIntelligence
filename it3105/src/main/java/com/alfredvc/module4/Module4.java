package com.alfredvc.module4;

import java.io.IOException;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Module4 {
    public static void main (String args[]) throws InterruptedException, IOException {
        FJPlayer2048 p = new FJPlayer2048(7, FJPlayer2048.Mode.PARALLEL, new Logic2048(), true);
        System.out.println(p.play());
        System.in.read();
    }
}
