package com.alfredvc.module4;

import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.function.Function;

public class Logic2048 {


    private long posRandomSeed = 1;
    private Random posRandom;
    private long numRandomSeed = 33;
    private Random numRandom;

    private final List<Function<Long, Float>> heuristicFunctions;

    public Logic2048(List<Function<Long, Float>> heuristicFunctions){
        this.heuristicFunctions = heuristicFunctions;
        this.posRandom = new Random(posRandomSeed);
        this.numRandom = new Random(numRandomSeed);
    }

    //TODO: Caching?
    public Float evaluate(long inputBoard) {
        float total = 0;
        for (int i = 0; i < heuristicFunctions.size(); i++) {
            total += heuristicFunctions.get(i).apply(inputBoard);
        }
        return total;
    }

    public long moveUp(long inputBoard) {
        return _moveUp(inputBoard);
    }

    public long moveDown(long inputBoard){
        return _moveDown(inputBoard);
    }

    public long moveLeft(long inputBoard) {
        return _moveLeft(inputBoard);
    }

    public long moveRight(long inputBoard) {
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
        return getBoardFromRows(_moveRowLeft(r0), _moveRowLeft(r1), _moveRowLeft(r2), _moveRowLeft(r3));
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
        return _reverse(_moveLeft(_reverse(inputBoard)));
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

    public long generateRandomTwoOrFour(long board) {
        char toSet = (char) (numRandom.nextFloat() < 0.9 ? 1 : 2);
        int zeroes = Logic2048.getEmptyCountInBoard(board);
        if (zeroes == 0) return board;
        int cell = posRandom.nextInt(zeroes);
        return setEmptyPositionTo(toSet, cell, board);
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
