package com.alfredvc.module4;

import java.util.Random;
import java.util.StringJoiner;

public class Logic2048 {

    private char[] left;
    private char[] right;

    private static final int EMPTY_CELL_WEIGHT = 16;

    public long[] counters = {0,0,0,0,0};

    private int[] emptyCells;

    private float[] evals;

    private long posRandomSeed = 1337;
    private Random posRandom;
    private long numRandomSeed = 1234567891;
    private Random numRandom;


    public Logic2048(){
        this.posRandom = new Random(posRandomSeed);
        this.numRandom = new Random(numRandomSeed);
        precalculate();
    }

    private void precalculate(){
        left = new char[65536];
        right = new char[65536];
        emptyCells = new int[65536];
        evals = new float[65536];
        char c = 0;
        while (true) {
            left[c] = _moveRowLeft(c);
            right[c] = reverseRow(_moveRowLeft(reverseRow(c)));
            emptyCells[c] = calculateEval(c);
            if (c == 65535) break;
            c++;
        }
    }

    private int calculateEval(char row){
        float SCORE_LOST_PENALTY = 200000.0f;
        float SCORE_MONOTONICITY_POWER = 4.0f;
        float SCORE_MONOTONICITY_WEIGHT = 47.0f;
        float SCORE_SUM_POWER = 3.5f;
        float SCORE_SUM_WEIGHT = 11.0f;
        float SCORE_MERGES_WEIGHT = 700.0f;
        float SCORE_EMPTY_WEIGHT = 270.0f;
        // Heuristic score
        float sum = 0;
        int empty = 0;
        int merges = 0;

        char[] line = { (char) (row >> 12 & 0xf),
        (char) (row >> 8 & 0xf),
        (char) (row >> 4 & 0xf),
        (char) (row & 0xf)};

        int prev = 0;
        int counter = 0;
        for (int i = 0; i < 4; ++i) {
            int rank = line[i];
            sum += Math.pow(rank, SCORE_SUM_POWER);
            if (rank == 0) {
                empty++;
            } else {
                if (prev == rank) {
                    counter++;
                } else if (counter > 0) {
                    merges += 1 + counter;
                    counter = 0;
                }
                prev = rank;
            }
        }
        if (counter > 0) {
            merges += 1 + counter;
        }

        float monotonicity_left = 0;
        float monotonicity_right = 0;
        for (int i = 1; i < 4; ++i) {
            if (line[i-1] > line[i]) {
                monotonicity_left += Math.pow(line[i-1], SCORE_MONOTONICITY_POWER) - Math.pow(line[i], SCORE_MONOTONICITY_POWER);
            } else {
                monotonicity_right += Math.pow(line[i], SCORE_MONOTONICITY_POWER) - Math.pow(line[i-1], SCORE_MONOTONICITY_POWER);
            }
        }

        return (int) (SCORE_LOST_PENALTY +
                SCORE_EMPTY_WEIGHT * empty +
                SCORE_MERGES_WEIGHT * merges -
                SCORE_MONOTONICITY_WEIGHT * Float.min(monotonicity_left, monotonicity_right) -
                SCORE_SUM_WEIGHT * sum);

    }

    public long evaluate(long inputBoard) {
        char r0 = (char) ((inputBoard >> 48) & 0xffffl);
        char r1 = (char) ((inputBoard >> 32) & 0xffffl);
        char r2 = (char) ((inputBoard >> 16) & 0xffffl);
        char r3 = (char) (inputBoard & 0xffffl);

        long transposed = _transpose(inputBoard);
        char c0 = (char) ((transposed >> 48) & 0xffffl);
        char c1 = (char) ((transposed >> 32) & 0xffffl);
        char c2 = (char) ((transposed >> 16) & 0xffffl);
        char c3 = (char) (transposed & 0xffffl);
        counters[4] = counters[4] + 1;

        return emptyCells[r0] + emptyCells[r1] + emptyCells[r2] + emptyCells[r3]
                + emptyCells[c0] + emptyCells[c1] + emptyCells[c2] + emptyCells[c3];
                //+ getFactorR0(r0) + getFactorR1(r1) + getFactorR2(r2);
    }

    public long moveUp(long inputBoard) {
        counters[0] = counters[0] +1;
        return _moveUp(inputBoard);
    }

    public long moveDown(long inputBoard){
        counters[1] = counters[1] +1;
        return _moveDown(inputBoard);
    }

    public long moveLeft(long inputBoard) {
        counters[2] = counters[2] +1;
        return _moveLeft(inputBoard);
    }

    public long moveRight(long inputBoard) {
        counters[3] = counters[3] +1;
        return _moveRight(inputBoard);
    }

    private long _moveUp(long inputBoard){
        return _transpose(_moveLeft(_transpose(inputBoard)));
    }
    private long _moveDown(long inputBoard) {
        return _transpose(_moveRight(_transpose(inputBoard)));
    }
    private long _moveLeft(long inputBoard) {
        char r0 = (char) ((inputBoard >> 48) & 0xffffl);
        char r1 = (char) ((inputBoard >> 32) & 0xffffl);
        char r2 = (char) ((inputBoard >> 16) & 0xffffl);
        char r3 = (char) (inputBoard & 0xffffl);
        return getBoardFromRows(left[r0], left[r1], left[r2], left[r3]);
    }

    public char _moveRowLeft(char row) {
        char c0 = (char) (row >> 12 & 0xf);
        char c1 = (char) (row >> 8 & 0xf);
        char c2 = (char) (row >> 4 & 0xf);
        char c3 = (char) (row & 0xf);

        for (int i = 0; i < 4; i++) {
            if (c2 == 0 && c3 != 0) {
                c2 = c3;
                c3 = 0;
            }
            if (c1 == 0 && c2 != 0) {
                c1 = c2;
                c2 = 0;
            }
            if (c0 == 0 && c1 != 0) {
                c0 = c1;
                c1 = 0;
            }
        }

        if (c0 == c1) {
            if(c0 != 0xf && c0 != 0) {
                c0++;
            }
            c1 = c2;
            c2 = c3;
            c3 = 0;
            if (c1 == c2) {
                if (c1 != 0xf && c1 != 0) {
                    c1 =(char) (c2+1);
                    c2 = 0;
                }
            }
        } else if (c1 == c2 && c1 != 0 && c1 != 0xf) {
            c1++;
            c2 = c3;
            c3 = 0;
        } else if (c2 == c3 && c2 != 0 && c2 != 0xf) {
            c2++;
            c3 = 0;
        }

        return getRowFromCells(c0, c1, c2, c3);
    }

    private static char getRowFromCells(char c0, char c1, char c2, char c3) {
        return (char) ((c0 << 12) | (c1 << 8) | (c2 << 4) | c3 );
    }

    private long _moveRight(long inputBoard) {
        char r0 = (char) ((inputBoard >> 48) & 0xffffl);
        char r1 = (char) ((inputBoard >> 32) & 0xffffl);
        char r2 = (char) ((inputBoard >> 16) & 0xffffl);
        char r3 = (char) (inputBoard & 0xffffl);
        return getBoardFromRows(right[r0], right[r1], right[r2], right[r3]);
    }

    public static long _reverse(long inputBoard){
        char r0 = (char) ((inputBoard >> 48) & 0xffffl);
        char r1 = (char) ((inputBoard >> 32) & 0xffffl);
        char r2 = (char) ((inputBoard >> 16) & 0xffffl);
        char r3 = (char) (inputBoard & 0xffffl);
        return getBoardFromRows(reverseRow(r3), reverseRow(r2), reverseRow(r1), reverseRow(r0));
    }

    private static char reverseRow(char row) {
        char c0 = (char) (row >> 12 & 0xf);
        char c1 = (char) (row >> 8 & 0xf);
        char c2 = (char) (row >> 4 & 0xf);
        char c3 = (char) (row & 0xf);

        return getRowFromCells(c3, c2, c1, c0);
    }

    public static long _transpose(long inputBoard) {
        long a1 = inputBoard & 0xF0F00F0FF0F00F0FL;
        long a2 = inputBoard & 0x0000F0F00000F0F0L;
        long a3 = inputBoard & 0x0F0F00000F0F0000L;
        long a = a1 | (a2 << 12) | (a3 >> 12);
        long b1 = a & 0xFF00FF0000FF00FFL;
        long b2 = a & 0x00FF00FF00000000L;
        long b3 = a & 0x00000000FF00FF00L;
        return b1 | (b2 >> 24) | (b3 << 24);
    }

    private float getFactorR0(char row) {
        int c0 = (row >> 12 & 0xf);
        int c1 = (row >> 8 & 0xf);
        int c2 = (row >> 4 & 0xf);
        int c3 = (row & 0xf);
        return 8 * c0  + 4 * c1 + 2 * c2;
    }
    private float getFactorR1(char row) {
        int c0 = (row >> 12 & 0xf);
        int c1 = (row >> 8 & 0xf);
        int c2 = (row >> 4 & 0xf);
        int c3 = (row & 0xf);
        return 4 * c0 + 2 * c1;
    }
    private float getFactorR2(char row) {
        int c0 = (row >> 12 & 0xf);
        int c1 = (row >> 8 & 0xf);
        int c2 = (row >> 4 & 0xf);
        int c3 = (row & 0xf);
        return 2 * c0;
    }



    public static String getBoardString(long inputBoard) {
        String s = Long.toHexString(inputBoard);
        while (s.length() != 16) s= "0"+s;
        StringJoiner j = new StringJoiner("\n");
        j.add(s.substring(0,4));
        j.add(s.substring(4,8));
        j.add(s.substring(8,12));
        j.add(s.substring(12));
        return j.toString();
    }

    private static long getBoardFromRows(char c0, char c1, char c2, char c3) {
        return (((long)c0) << 48) | (((long)c1) << 32) | (((long)c2) << 16) | c3;
    }

    public static int getEmptyCountInBoard(long board) {
        int count = 0;
        for( int i = 0; i < 16; i++) {
            count += ((board >> i*4) & 0xf) == 0 ? 1 : 0;
        }
        return count;
    }

    public static int calculateEmptyCountInRow(char row) {
        int count = 0;
        for( int i = 0; i < 4; i++) {
            count += ((row >> i*4) & 0xf) == 0 ? 1 : 0;
        }
        return count;
    }

    public long generateRandomTwoOrFour(long board) {
        char toSet = (char) (numRandom.nextFloat() < 0.9 ? 1 : 2);
        int zeroes = Logic2048.getEmptyCountInBoard(board);
        if (zeroes == 0) return board;
        int cell = posRandom.nextInt(zeroes);
        return setEmptyPositionTo(toSet, cell, board);
    }

    public boolean isBoardLost(long board) {
        return (moveLeft(board) == moveRight(board)) &&
                (moveRight(board) == moveUp(board)) &&
                (moveUp(board) == moveDown(board));
    }

    public static long setEmptyPositionTo(char c, int indexOfEmptyPos, long board) {
        int count = -1;
        for( int i = 0; i < 16; i++) {
            count += ((board >> i*4) & 0xf) == 0 ? 1 : 0;
            if (count == indexOfEmptyPos) {
                count = i;
                break;
            }
        }
        return board | (((long)c) & 0xf) << (4*count);
    }
}
