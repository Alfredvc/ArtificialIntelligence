package com.alfredvc.module4;

import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;

public class Logic2048 {

    private char[] left;
    private char[] right;

    private static final int EMPTY_CELL_WEIGHT = 16;

    public long[] counters = {0,0,0,0,0};

    private double[] emptyCells;

    public static final double[] DEFAULT_PARAMS = {2, 3, 2, 2, 60};
    private double[] params = DEFAULT_PARAMS;

    private float[] evals;

    private Random posRandom;
    private Random numRandom;

    public Logic2048(double[] params) {
        this();
        this.params = params;
    }

    public Logic2048(int posRandom, int numRandom) {
        this.posRandom = new Random(posRandom);
        this.numRandom = new Random(numRandom);
        precalculate();
    }

    public Logic2048(){
        this.posRandom = new Random();
        this.numRandom = new Random();
        precalculate();
    }

    private void precalculate(){
        left = new char[65536];
        right = new char[65536];
        emptyCells = new double[65536];
        evals = new float[65536];
        char c = 0;
        while (true) {
            left[c] = _moveRowLeft(c);
            right[c] = reverseRow(_moveRowLeft(reverseRow(c)));
            emptyCells[c] = myCalculateEval(c);
            if (c == 65535) break;
            c++;
        }
    }

    private double myCalculateEval(char inRow){
        double total = 100000;
        char[] row = { (char) (inRow >> 12 & 0xf),
                (char) (inRow >> 8 & 0xf),
                (char) (inRow >> 4 & 0xf),
                (char) (inRow & 0xf)};

        double perfectRight = 0;
        double perfectLeft = 0;
        double same = 0;

        for (int i = 1; i < 4; i++) {
            if (row[i-1] == row[i]) same += Math.pow(row[i], 2);
        }
        total += same * 3;

        if (row[0] > row[1] && row[1] > row[2] && row[2] > row[3]) total += Math.pow(row[0], 1.9)*1.4;
        if (row[0] < row[1] && row[1] < row[2] && row[2] < row[3]) total += Math.pow(row[3], 1.9)*1.4;
        if (row[0] > row[1] && row[0] > row[2] && row[0] > row[3]) total += Math.pow(row[0], 1.7)*1.5;
        if (row[3] > row[1] && row[3] > row[2] && row[3] > row[0]) total += Math.pow(row[3], 1.7)*1.5;

        total += calculateEmptyCountInRow(inRow) * 40;
        return total;
    }

    private double calculateEval(char inRow){
        double NO_MOVE_PENALTY = 200000.0f;
        double MONOTONICITY_POWER = 4.0f;
        double MONOTONICITY_WEIGHT = 47.0f;
        double SUM_POWER = 3.5f;
        double SUM_WEIGHT = 11.0f;
        double MERGES_WEIGHT = 700.0f;
        double EMPTY_WEIGHT = 270.0f;

        double sum = 0;
        int empty = 0;
        int merges = 0;

        char[] row = { (char) (inRow >> 12 & 0xf),
                (char) (inRow >> 8 & 0xf),
                (char) (inRow >> 4 & 0xf),
                (char) (inRow & 0xf)};

        int prev = 0;
        int counter = 0;
        for (int i = 0; i < 4; ++i) {
            int rank = row[i];
            sum += Math.pow(rank, SUM_POWER);
            if (rank == 0) {
                empty++;
            } else {
                if (prev == rank) {
                    counter++;
                } else if (counter > 0) {
                    if (prev < 2) {
                        merges += 1 + counter;
                    }
                    counter = 0;
                }
                prev = rank;
            }
        }
        if (counter > 0 && prev < 2) {
            merges += 1 + counter;
        }

        double monotonicity_left = 0;
        double monotonicity_right = 0;
        for (int i = 1; i < 4; ++i) {
            if (row[i-1] > row[i]) {
                monotonicity_left += Math.pow(row[i-1], MONOTONICITY_POWER) - Math.pow(row[i], MONOTONICITY_POWER);
            } else {
                monotonicity_right += Math.pow(row[i], MONOTONICITY_POWER) - Math.pow(row[i-1], MONOTONICITY_POWER);
            }
        }

        return  NO_MOVE_PENALTY +
                EMPTY_WEIGHT * empty +
                MERGES_WEIGHT * merges -
                MONOTONICITY_WEIGHT * Double.min(monotonicity_left, monotonicity_right)
                - SUM_WEIGHT * sum;

    }

    public double evaluate(long inputBoard) {
        char r0 = (char) ((inputBoard >> 48) & 0xffffL);
        char r1 = (char) ((inputBoard >> 32) & 0xffffL);
        char r2 = (char) ((inputBoard >> 16) & 0xffffL);
        char r3 = (char) (inputBoard & 0xffffL);

        long transposed = _transpose(inputBoard);
        char c0 = (char) ((transposed >> 48) & 0xffffL);
        char c1 = (char) ((transposed >> 32) & 0xffffL);
        char c2 = (char) ((transposed >> 16) & 0xffffL);
        char c3 = (char) (transposed & 0xffffL);
        counters[4] = counters[4] + 1;

        return emptyCells[r0] + emptyCells[r1] + emptyCells[r2] + emptyCells[r3]
                + emptyCells[c0] + emptyCells[c1] + emptyCells[c2] + emptyCells[c3];
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
        char r0 = (char) ((inputBoard >> 48) & 0xffffL);
        char r1 = (char) ((inputBoard >> 32) & 0xffffL);
        char r2 = (char) ((inputBoard >> 16) & 0xffffL);
        char r3 = (char) (inputBoard & 0xffffL);
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
        char r0 = (char) ((inputBoard >> 48) & 0xffffL);
        char r1 = (char) ((inputBoard >> 32) & 0xffffL);
        char r2 = (char) ((inputBoard >> 16) & 0xffffL);
        char r3 = (char) (inputBoard & 0xffffL);
        return getBoardFromRows(right[r0], right[r1], right[r2], right[r3]);
    }

    public static long _reverse(long inputBoard){
        char r0 = (char) ((inputBoard >> 48) & 0xffffL);
        char r1 = (char) ((inputBoard >> 32) & 0xffffL);
        char r2 = (char) ((inputBoard >> 16) & 0xffffL);
        char r3 = (char) (inputBoard & 0xffffL);
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

    public long score(long inputBoard) {
        long sum = 0;
        while (inputBoard != 0) {
            sum += 2 << (((int) inputBoard & 0xF) - 1);
            inputBoard = inputBoard >>> 4;
        }
        return sum;
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
        char toSet = (char) (numRandom.nextInt(10) < 9 ? 1 : 2);
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
