package com.alfredvc.module4;


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

    private static final int TRANSPOSITION_TABLE_MAX_SIZE = 500_000;

    private static final double PROB_LIMIT = 0.0001;

    private final char two;
    private final char four;
    private final Logger l;

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
            int result = Long.hashCode(board);
            result = 31 * result + depth;
            return result;
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
        theWhile:
        while (!lost) {
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
        long[] evals = {zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveUp(currentBoard), 1.0f),
                zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveDown(currentBoard), 1.0f),
                zeroOrEvalMove(0, maxDepth, currentBoard, logic.moveLeft(currentBoard), 1.0f),
                zeroOrEvalMove(0,maxDepth, currentBoard, logic.moveRight(currentBoard), 1.0f)};
        long max = Long.MIN_VALUE;
        int maxI = -1;
        for (int i = 0; i < 4; i++) {
            if (evals[i] > max){
                max = evals[i];
                maxI = i;
            }
        }
        return maxI == -1 ? Move.NONE : Move.values()[maxI];
    }

    private long evalMove(int depth, int maxDepth, long board, double prob) {
        if (prob < PROB_LIMIT) {
            l.increase(l.LOW_PROB_EVAL);
            return logic.evaluate(board);
        }
        BoardEval boardEval = new BoardEval(board, depth);
        if (transpositionTable.containsKey(boardEval)) {
            l.increase(l.CACHE_HIT);
            return transpositionTable.get(boardEval);
        }
        l.increase(l.CACHE_MISS);
        int empty = Logic2048.getEmptyCountInBoard(board);
        long[] evals;
//        if (empty > 7) {
//            evals = new long[empty];
//            for (int i = 0; i < empty; i++) {
//                boolean useTwo= r.nextdouble() < 0.9f;
//                evals[i] = evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(useTwo? two : four, i, board), (useTwo? 0.9 : 0.1) * prob);
//            }
//        }
//        else {
            evals = new long[empty*2];
        prob = prob / empty;
            for (int i = 0; i < empty; i++) {
                evals[2*i] = (long) (0.9f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo( two, i, board), prob * 0.9));
                evals[2*i +1] = (long) (0.1f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(four, i, board), prob * 0.1));
            }
        //}
        long sum = sum(evals);
        transpositionTable.put(boardEval, sum);
        return sum;
    }

    private long evalProbability(int depth, int maxDepth, long board, double prob) {
        if (depth >= maxDepth) {
            l.increase(l.LEAF_EVALS);
            return logic.evaluate(board);
        }
        long[] evals = {zeroOrEvalMove(depth+1, maxDepth, board, logic.moveUp(board), prob),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveDown(board), prob),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveLeft(board), prob),
                zeroOrEvalMove(depth+1, maxDepth, board, logic.moveRight(board), prob)};
        return maxValue(evals);
    }

    private long zeroOrEvalMove(int depth, int maxDepth, long board, long nextBoard, double prob) {
        if (board == nextBoard) {
            l.increase(l.NO_MOVE_EVAL);
            return 0;
        }
        return board == nextBoard ? 0 : evalMove(depth, maxDepth, nextBoard, prob);
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
                    percent(counters[0], counters[1]), percent(counters[LEAF_EVALS], evals()),
                    percent(counters[LOW_PROB_EVAL], evals()), percent(counters[NO_MOVE_EVAL], evals()));
        }
    }
}
