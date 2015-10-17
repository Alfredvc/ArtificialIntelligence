package com.alfredvc.module4;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Module4 {
    public static void main (String args[]) throws InterruptedException {
        Player2048 player2048 =  new Player2048(3);
        Thread.sleep(1000);
        player2048.play();
    }
}
