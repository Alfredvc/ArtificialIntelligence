package com.alfredvc.module4;


import java.util.*;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Player2048 {
    private Game2048 game;
    private Logic2048 logic;
    private final int maxDepth;

    private static final int TRANSPOSITION_TABLE_MAX_SIZE = 500_000;

    private static final double PROB_LIMIT = 0.0001;

    private final char two;
    private final char four;
    private final Logger l;

    private Random r;

    private long[] counters = {0,0};

    private Map<Long, BoardEval> transpositionTable;

    private class BoardEval{
        public double eval;
        public int depth;

        public BoardEval(double eval, int depth) {
            this.eval = eval;
            this.depth = depth;
        }

        @Override
        public int hashCode() {
            int result = Double.hashCode(eval);
            result = 31 * result + depth;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) throw new NullPointerException();
            if (!(obj instanceof BoardEval)) throw new IllegalArgumentException();
            BoardEval other = (BoardEval) obj;
            return eval == other.eval && depth == other.depth;

        }
    }


    private enum Move {
        UP, DOWN, LEFT, RIGHT, NONE
    }

    public Player2048 (int depth) {
        this.transpositionTable = createTranspositionTable(TRANSPOSITION_TABLE_MAX_SIZE);
        this.logic = new Logic2048();
        this.l = new Logger();
        this.maxDepth = depth;
        two = 0x1;
        four = 0x2;
        this.r = new Random();
    }

    public static Map<Long, BoardEval> createTranspositionTable(final int maxEntries) {
        return new LinkedHashMap<Long, BoardEval>(maxEntries*10/7, 0.7f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, BoardEval> eldest) {
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
        theWhile:
        while (!lost) {
            transpositionTable = createTranspositionTable(TRANSPOSITION_TABLE_MAX_SIZE);
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

    private double evalMove(int depth, int maxDepth, long board, double prob) {
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
        int empty = Logic2048.getEmptyCountInBoard(board);
        double[] evals;
        evals = new double[empty*2];
        prob = prob / empty;
            for (int i = 0; i < empty; i++) {
                evals[2*i] = 0.9f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo( two, i, board), prob * 0.9);
                evals[2*i +1] = 0.1f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(four, i, board), prob * 0.1);
            }
        double sum = sum(evals);
        transpositionTable.put(board, new BoardEval(sum, depth));
        return sum;
    }

    private double evalProbability(int depth, int maxDepth, long board, double prob) {
        if (depth >= maxDepth) {
            l.increase(l.LEAF_EVALS);
            return logic.evaluate(board);
        }
        double[] evals = {zeroOrEvalMove(depth+1, maxDepth, board, logic.moveUp(board), prob),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveDown(board), prob),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveLeft(board), prob),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveRight(board), prob)};
        return maxValue(evals);
    }

    private double zeroOrEvalMove(int depth, int maxDepth, long board, long nextBoard, double prob) {
        if (board == nextBoard) {
            l.increase(l.NO_MOVE_EVAL);
            return 0;
        }
        return evalMove(depth, maxDepth, nextBoard, prob);
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
