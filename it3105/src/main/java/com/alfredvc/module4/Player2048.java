package com.alfredvc.module4;


import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Player2048 {
    private Game2048 game;
    private Logic2048 logic;
    private final int maxDepth;

    private static final int TRANSPOSITION_TABLE_MAX_SIZE = 50_000;

    private final char two;
    private final char four;

    private Random r;

    private long[] counters = {0,0};

    private Map<BoardEval, Long> transpositionTable;

    private class BoardEval {
        public long board;
        public int depth;

        public BoardEval(long board, int depth) {
            this.board = board;
            this.depth = depth;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(board);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) throw new NullPointerException();
            if (!(obj instanceof BoardEval)) throw new IllegalArgumentException();
            BoardEval other = (BoardEval) obj;
            return board == other.board && depth == other.depth;

        }
    }


    private enum Move {
        UP, DOWN, LEFT, RIGHT
    }

    public Player2048 (int depth) {
        this.transpositionTable = createTranspositionTable(TRANSPOSITION_TABLE_MAX_SIZE);
        this.logic = new Logic2048();
        this.maxDepth = depth;
        two = 0x1;
        four = 0x2;
        this.r = new Random();
    }

    public static Map<BoardEval, Long> createTranspositionTable(final int maxEntries) {
        return new LinkedHashMap<BoardEval, Long>(maxEntries*10/7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<BoardEval, Long> eldest) {
                return size() > maxEntries;
            }
        };
    }

    public void play() {
        game = new Game2048(logic);
        game.start();

        game.autoRefresh(true);
        long currentBoard = game.getBoard();
        System.out.println("Started playing!");
        Move nextMove;
        int moved = 0;
        boolean lost = false;
        while (!lost) {
            nextMove = getNextMove(currentBoard);
            switch (nextMove) {
                case UP:
                    currentBoard = game.up();
                    break;
                case DOWN:
                    currentBoard = game.down();
                    break;
                case LEFT:
                    currentBoard = game.left();
                    break;
                case RIGHT:
                    currentBoard = game.right();
                    break;
            }
            lost = game.isLost();
            moved++;
        }
        game.setBoard(currentBoard);
        System.out.println("You fucking lost...");
        System.out.println(Arrays.toString(counters));
        System.out.println(counters[1] *1.0 / counters[0] * 1.0);
    }

    public Logic2048 getLogic() {
        return logic;
    }

    private Move getNextMove(long currentBoard) {
        long[] evals = {zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveUp(currentBoard)),
                zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveDown(currentBoard)),
                zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveLeft(currentBoard)),
                zeroOrEvalMove(0,maxDepth, currentBoard, logic.moveRight(currentBoard))};
        long max = Long.MIN_VALUE;
        int maxI = -1;
        for (int i = 0; i < 4; i++) {
            if (evals[i] > max){
                max = evals[i];
                maxI = i;
            }
        }
        return Move.values()[maxI];
    }

    private long evalMove(int depth, int maxDepth, long board) {
        BoardEval boardEval = new BoardEval(board, depth);
        if (transpositionTable.containsKey(boardEval)) {
            counters[1] = counters[1] + 1;
            return transpositionTable.get(boardEval);
        }
        int empty = Logic2048.getEmptyCountInBoard(board);
        long[] evals = new long[empty];
        for (int i = 0; i < empty; i++) {
            evals[i] = evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(r.nextFloat() < 10.9f ? two : four, i, board));
            //evals[2*i] = (long) (0.9f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo( two, i, board)));
            //evals[2*i +1] = (long) (0.1f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(four, i, board)));
        }
        long sum = sum(evals);
        counters[0] = counters[0] + 1;
        transpositionTable.put(boardEval, sum);
        return sum;
    }

    private long evalProbability(int depth, int maxDepth, long board) {
        if (depth >= maxDepth) {
            return logic.evaluate(board);
        }
        long[] evals = {zeroOrEvalMove(depth+1, maxDepth, board, logic.moveUp(board)),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveDown(board)),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveLeft(board)),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveRight(board))};
        return maxValue(evals);
    }

    private long zeroOrEvalMove(int depth, int maxDepth, long board, long nextBoard) {
        return board == nextBoard ? 0 : evalMove(depth, maxDepth, nextBoard);
    }

    private long maxValue(long... args) {
        long max = Long.MIN_VALUE;
        for (int i = 0; i < args.length; i++) {
            if (args[i] > max) max = args[i];
        }
        return max;
    }

    private long sum(long... args) {
        long total = 0;
        for (int i = 0; i < args.length; i++) {
            total += args[i];
        }
        return total;
    }
}
