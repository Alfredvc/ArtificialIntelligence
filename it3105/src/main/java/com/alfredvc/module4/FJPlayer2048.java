package com.alfredvc.module4;


import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RecursiveTask;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class FJPlayer2048 {
    private Game2048 game;
    private Logic2048 logic;
    private final int maxDepth;

    enum Mode {
        SERIAL, PARALLEL
    }

    private final Mode mode;

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
        public double eval;
        public int depth;

        public BoardEval(double eval, int depth) {
            this.eval = eval;
            this.depth = depth;
        }

        @Override
        public String toString() {
            return "BoardEval{" +
                    "eval=" + eval +
                    ", depth=" + depth +
                    '}';
        }
    }


    private enum Move {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    public FJPlayer2048(int depth, Mode mode) {
        //this.pool = new ForkJoinPool(CONCURRENCY_LEVEL);
        this.mode = mode;
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
            if (moved % 2 == 0)this.transpositionTable = new ConcurrentHashMap<>(500_000, 0.5f);
            l.reset();
            if (mode == Mode.SERIAL) nextMove = serialGetNextMove(currentBoard);
            else nextMove = getNextMove(currentBoard);
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

    private Move serialGetNextMove(long currentBoard) {
        double[] evals = {zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveUp(currentBoard), 1.0f),
                zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveDown(currentBoard), 1.0f),
                zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveLeft(currentBoard), 1.0f),
                zeroOrEvalMove(0,maxDepth, currentBoard, logic.moveRight(currentBoard), 1.0f)};
        double max = Double.MIN_VALUE;
        int maxI = -1;
        for (int i = 0; i < 4; i++) {
            if (evals[i] > max){
                max = evals[i];
                maxI = i;
            }
        }
        return maxI == -1 ? Move.NONE : Move.values()[maxI];
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
        double[] evals = {zeroOrJoin(up), zeroOrJoin(down), zeroOrJoin(left), zeroOrJoin(right)};
        double max = Double.MIN_VALUE;
        int maxI = -1;
        for (int i = 0; i < 4; i++) {
            if (evals[i] > max){
                max = evals[i];
                maxI = i;
            }
        }
        return sum(evals) == 0 ? Move.NONE : Move.values()[maxI];
    }

    private double evalMove(int depth, int maxDepth, long board, double prob) {
        BoardEval boardEval = transpositionTable.get(board);
        if (boardEval != null && boardEval.depth <= depth) {
            l.increase(l.CACHE_HIT);
            return boardEval.eval;
        }
        l.increase(l.CACHE_MISS);
        int empty = Logic2048.getEmptyCountInBoard(board);
        prob = prob / empty;
        double sum = 0;

        for (int i = 0; i < empty; i++) {
            sum += 0.9 * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo( two, i, board), prob * 0.9);
            sum += 0.1 * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(four, i, board), prob * 0.1);
        }
        sum /= empty;
        BoardEval value = new BoardEval(sum, depth);
//        if (boardEval != null && Math.abs(boardEval.eval - sum) > 1 && boardEval.depth == depth) {
//            if (boardEval.board != value.board || boardEval.boardHash != value.boardHash) throw new IllegalStateException();
//            System.out.format("%.1f - %.1f. / %.1f\n", boardEval.eval, value.eval, boardEval.eval - value.eval);
//        }
        transpositionTable.put(board, value);
        return sum;
    }

    private double evalProbability(int depth, int maxDepth, long board, double prob) {
        if (depth >= maxDepth) {
            l.increase(l.LEAF_EVALS);
            return logic.evaluate(board);
        }

        if (prob < PROB_LIMIT) {
            l.increase(l.LOW_PROB_EVAL);
            return logic.evaluate(board);
        }
        BoardEval boardEval = transpositionTable.get(board);
        if (boardEval != null && boardEval.depth <= depth) {
            l.increase(l.CACHE_HIT);
            return boardEval.eval;
        }
        l.increase(l.CACHE_MISS);

        if (depth > 1 || mode == Mode.SERIAL) {
            double[] evals = {zeroOrEvalMove(depth+1, maxDepth, board, logic.moveUp(board), prob),
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
            double[] evals = {zeroOrJoin(up), zeroOrJoin(down), zeroOrJoin(left), zeroOrJoin(right)};
            return maxValue(evals);
        }
    }

    private double zeroOrEvalMove(int depth, int maxDepth, long board, long nextBoard, double prob) {
        if (board == nextBoard) {
            l.increase(l.NO_MOVE_EVAL);
            return 0.0;
        }
        return evalMove(depth, maxDepth, nextBoard, prob);
    }

    private void tryFork(MoveTask task) {
        if (task != null) task.fork();
    }

    private double zeroOrJoin(MoveTask task){
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

    private double maxValue(double... args) {
        double max = Long.MIN_VALUE;
        for (int i = 0; i < args.length; i++) {
            if (args[i] > max) max = args[i];
        }
        return max;
    }

    private double sum(double... args) {
        double total = 0;
        for (int i = 0; i < args.length; i++) {
            total += args[i];
        }
        return total;
    }

    private class MoveTask extends RecursiveTask<Double> {
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
        protected Double compute() {
            if (depth >= maxDepth) return logic.evaluate(board);
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
