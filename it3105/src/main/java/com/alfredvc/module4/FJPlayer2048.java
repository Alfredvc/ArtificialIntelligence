package com.alfredvc.module4;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class FJPlayer2048 {
    private Game2048 game;
    private Logic2048 logic;
    private final int maxDepth;

    private static final int TRANSPOSITION_TABLE_MAX_SIZE = 500_000;
    private static final int CONCURRENCY_LEVEL = 4;

    private static final double PROB_LIMIT = 0.0001;

    private final char two;
    private final char four;
    private final Logger l;

    private Random r;

    private long[] counters = {0,0};

    private ConcurrentHashMap<Long, BoardEval> transpositionTable;
    //private ForkJoinPool pool;

    private class BoardEval{
        public long eval;
        public int depth;
        public long board;
        public int boardHash;

        public BoardEval(long eval, int depth, long board, int boardHash) {
            this.eval = eval;
            this.depth = depth;
            this.board = board;
            this.boardHash = boardHash;
        }

        @Override
        public String toString() {
            return "BoardEval{" +
                    "eval=" + eval +
                    ", depth=" + depth +
                    ", board=" + board +
                    ", boardHash=" + boardHash +
                    '}';
        }
    }


    private enum Move {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    public FJPlayer2048(int depth) {
//        this.transpositionTable = CacheBuilder.newBuilder()
//                .concurrencyLevel(CONCURRENCY_LEVEL)
//                .maximumSize(TRANSPOSITION_TABLE_MAX_SIZE)
//                .build();
        //this.pool = new ForkJoinPool(CONCURRENCY_LEVEL);
        this.logic = new Logic2048();
        this.l = new Logger();
        this.maxDepth = depth;
        two = 0x1;
        four = 0x2;
        this.r = new Random();
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
        theWhile:
        while (!lost) {
            this.transpositionTable = new ConcurrentHashMap<>();
            l.reset();
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
                case NONE:
                    lost = true;
                    break theWhile;
            }
            lost = game.isLost();
            moved++;
            System.out.println(l);
        }
        game.repaint();
        System.out.println("You fucking lost...");
    }

    public Logic2048 getLogic() {
        return logic;
    }

    private Move getNextMove(long currentBoard) {
        MoveTask up = getMoveTask(0, maxDepth, currentBoard, logic.moveUp(currentBoard), 1.0);
        tryFork(up);
        MoveTask down = getMoveTask(0, maxDepth, currentBoard, logic.moveDown(currentBoard), 1.0);
        tryFork(down);
        MoveTask left = getMoveTask(0, maxDepth, currentBoard, logic.moveLeft(currentBoard), 1.0);
        tryFork(left);
        MoveTask right = getMoveTask(0, maxDepth, currentBoard, logic.moveRight(currentBoard), 1.0);
        tryFork(right);
        long[] evals = {zeroOrJoin(up), zeroOrJoin(down), zeroOrJoin(left), zeroOrJoin(right)};
        long max = Long.MIN_VALUE;
        int maxI = -1;
        for (int i = 0; i < 4; i++) {
            if (evals[i] > max){
                max = evals[i];
                maxI = i;
            }
        }
        return sum(evals) == 0 ? Move.NONE : Move.values()[maxI];
    }

    private long evalMove(int depth, int maxDepth, long board, double prob) {

        if (prob < PROB_LIMIT) {
            l.increase(l.LOW_PROB_EVAL);
            return logic.evaluate(board);
        }
        BoardEval boardEval = transpositionTable.get(board);
        if (boardEval != null && boardEval.depth == depth) {
            l.increase(l.CACHE_HIT);
            return boardEval.eval;
        }
        l.increase(l.CACHE_MISS);
        int empty = Logic2048.getEmptyCountInBoard(board);
        long[] evals;
        evals = new long[empty*2];
        prob = prob / empty;
        for (int i = 0; i < empty; i++) {
            evals[2*i] = (long) (0.9 * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo( two, i, board), prob * 0.9));
            evals[2*i +1] = (long) (0.1 * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(four, i, board), prob * 0.1));
        }
        long sum = sum(evals);
        BoardEval value = new BoardEval(sum, depth, board, Long.hashCode(board));
//        if (boardEval != null && boardEval.eval != sum && boardEval.depth == depth) {
//            if (boardEval.board != value.board || boardEval.boardHash != value.boardHash) throw new IllegalStateException();
//            System.out.format("%d - %d. / %d\n", boardEval.eval, value.eval, boardEval.eval - value.eval);
//        }
        transpositionTable.put(board, value);
        return sum;
    }

    private long evalProbability(int depth, int maxDepth, long board, double prob) {
        if (depth >= maxDepth) {
            l.increase(l.LEAF_EVALS);
            return logic.evaluate(board);
        }

        if (depth > 1) {
            long[] evals = {zeroOrEvalMove(depth+1, maxDepth, board, logic.moveUp(board), prob),
                    zeroOrEvalMove(depth+1, maxDepth, board, logic.moveDown(board), prob),
                    zeroOrEvalMove(depth+1, maxDepth, board, logic.moveLeft(board), prob),
                    zeroOrEvalMove(depth+1, maxDepth, board, logic.moveRight(board), prob)};
            return maxValue(evals);
        } else {
            MoveTask up = getMoveTask(depth + 1, maxDepth, board, logic.moveUp(board), prob);
            tryFork(up);
            MoveTask down = getMoveTask(depth + 1, maxDepth, board, logic.moveDown(board), prob);
            tryFork(down);
            MoveTask left = getMoveTask(depth + 1, maxDepth, board, logic.moveLeft(board), prob);
            tryFork(left);
            MoveTask right = getMoveTask(depth + 1, maxDepth, board, logic.moveRight(board), prob);
            tryFork(right);
            long[] evals = {zeroOrJoin(up), zeroOrJoin(down), zeroOrJoin(left), zeroOrJoin(right)};
            return maxValue(evals);
        }
    }

    private long zeroOrEvalMove(int depth, int maxDepth, long board, long nextBoard, double prob) {
        if (board == nextBoard) {
            l.increase(l.NO_MOVE_EVAL);
            return 0;
        }
        return evalMove(depth, maxDepth, nextBoard, prob);
    }

    private void tryFork(MoveTask task) {
        if (task != null) task.fork();
    }

    private long zeroOrJoin(MoveTask task){
        if (task == null) return 0;
        return task.join();
    }

    private MoveTask getMoveTask(int depth, int maxDepth, long board, long nextBoard, double prob){
        if (board == nextBoard) {
            l.increase(l.NO_MOVE_EVAL);
            return null;
        }
        return new MoveTask(depth, maxDepth, nextBoard, prob);
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

    private class MoveTask extends RecursiveTask<Long> {
        private int depth;
        private int maxDepth;
        private long board;
        private double prob;

        public MoveTask(int depth, int maxDepth, long board, double prob) {
            this.depth = depth;
            this.maxDepth = maxDepth;
            this.board = board;
            this.prob = prob;
        }

        @Override
        protected Long compute() {
            if (depth == maxDepth) return logic.evaluate(board);
            return evalMove(depth, maxDepth, board, prob);
        }
    }

    private class Logger {
        public final int CACHE_HIT = 0;
        public final int CACHE_MISS = 1;
        public final int LEAF_EVALS = 2;
        public final int LOW_PROB_EVAL = 3;
        public final int NO_MOVE_EVAL= 4;
        public final int MOVE_TIME = 5;


        private long[] counters;
        public Logger() {
           counters = new long[5];
        }

        public void increase(int type) {
            counters[type]++;
        }

        public void set(int type, long i) {
            counters[type] = i;
        }

        public void reset() {
            for (int i = 0; i < counters.length; i++) {
                counters[i] = 0;
            }
        }

        private long evals(){
            return counters[LEAF_EVALS] + counters[LOW_PROB_EVAL] + counters[NO_MOVE_EVAL];
        }

        private double percent(long a, long b){
            return a * 1.0 /  b * 1.0;
        }

        @Override
        public String toString() {
            return String.format("Cache hit: %f. Leaf: %f. LowProb: %f. NoMove: %f",
                    percent(counters[0], counters[1] + counters[0]), percent(counters[LEAF_EVALS], evals()),
                    percent(counters[LOW_PROB_EVAL], evals()), percent(counters[NO_MOVE_EVAL], evals()));
        }
    }
}
