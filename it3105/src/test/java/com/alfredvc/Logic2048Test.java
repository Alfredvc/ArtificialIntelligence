package com.alfredvc;

import com.alfredvc.module4.Logic2048;

import org.junit.Test;

import java.util.Random;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by erpa_ on 10/8/2015.
 */
public class Logic2048Test {
    @Test
    public void testTranspose(){
        long board = 0x0123456789abcdefl;
        System.out.println(Logic2048.getBoardString(board) + "\n\nTransposed\n");
        System.out.println(Logic2048.getBoardString(Logic2048._transpose(board)));
    }

    @Test
    public void testMoveLeft(){
        char r1 = 0x000a;
        char r1e = 0xa000;
        char r2 = 0xaabb;
        char r2e = 0xbc00;
        char r3 = 0xaccb;
        char r3e = 0xadb0;
        char r4 = 0x0101;
        char r4e = 0x2000;
        long board = (((long)r1) << 48) | (((long)r2) << 32) | (((long)r3) << 16) | r4;
        long movedBoard = (((long)r1e) << 48) | (((long)r2e) << 32) | (((long)r3e) << 16) | r4e;

        assertThat(new Logic2048(null)._moveRowLeft(r1), is(r1e));
        assertThat(new Logic2048(null)._moveRowLeft(r2), is(r2e));
        assertThat(new Logic2048(null)._moveRowLeft(r3), is(r3e));
        assertThat(new Logic2048(null)._moveRowLeft(r4), is(r4e));

        assertThat(new Logic2048(null).moveLeft(board), is(movedBoard));
    }

    @Test
    public void testReverseBytes() {
        long board = 0x0123456789abcdefl;
        System.out.println(Logic2048.getBoardString(board));
        System.out.println();
        System.out.println(Logic2048.getBoardString(Logic2048._reverse(board)));
    }

    @Test
    public void setEmptyPosTo(){
        Random r = new Random(5);
        char c = (char) 0xC;
        for (int i = 0; i < 1000; i++){
            long board = r.nextLong();
            int zeroCount = Logic2048.getEmptyCountInBoard(board);
            if (zeroCount == 0) continue;
            int pos = r.nextInt(zeroCount);
            long changedBoard = Logic2048.setEmptyPositionTo(c, pos, board);
            assertThat(Logic2048.getEmptyCountInBoard(changedBoard), equalTo(zeroCount-1));
        }
    }

    @Test
    public void countEmptyTest(){
        long[] boards = {
                0x1111111111111111L,
                0x1010101010101010L,
                0x0000000000000000L,
                0x0000000011111111L,
                0xffffffff0000ffffL,
                0xffff0fffffffffffL
        };
        int[] zeroes = {0, 8, 16, 8, 4, 1};
        for (int i = 0; i < boards.length; i++) {
            assertThat(Logic2048.getEmptyCountInBoard(boards[i]), equalTo(zeroes[i]));
        }
    }

    @Test
    public void intensiveTransposeTest() {
        Random r = new Random(4);
        for( int i = 0; i < 10000; i++) {
            long board = r.nextLong();
            long doubletransposed = Logic2048._transpose(Logic2048._transpose(board));
            assertThat(board, equalTo(doubletransposed));
        }
    }
    @Test
    public void intensiveReverseTest() {
        Random r = new Random(4);
        for( int i = 0; i < 10000; i++) {
            long board = r.nextLong();
            long doubleReversed = Logic2048._reverse(Logic2048._reverse(board));
            assertThat(board, equalTo(doubleReversed));
        }
    }

    @Test
    public void intensiveGenerateTwoOrFour() {
        Random r = new Random(4);
        Logic2048 b = new Logic2048(null);
        for( int i = 0; i < 10000; i++) {
            long board = r.nextLong();
            int zeroes = Logic2048.getEmptyCountInBoard(board);
            long newBoard = b.generateRandomTwoOrFour(board);
            if (zeroes == 0) {
                assertThat("No zeroes, shouldn't do anything", newBoard, equalTo(board));
            } else {
                assertThat(Logic2048.getEmptyCountInBoard(newBoard), equalTo(zeroes - 1));
            }
        }
    }

}
