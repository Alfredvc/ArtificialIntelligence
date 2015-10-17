package com.alfredvc.module4;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RecursiveTask;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class ForkJoinPlayer2048 {
    private Game2048 game;
    private Logic2048 logic;

    private final int maxDepth;

    private final char two;
    private final char four;

    private long[] counters = {0,0};

    private Map<Long, Eval> transpositionTable;

    private class Eval {
        public float eval;
        public int depth;

        public Eval(float eval, int depth) {
            this.eval = eval;
            this.depth = depth;
        }
    }

    private final ExecutorService executorService;

    private enum Move {
        UP, DOWN, LEFT, RIGHT, LOST
    }

    public ForkJoinPlayer2048(int depth) {
        this.transpositionTable = new HashMap<>();
        this.logic = new Logic2048();
        this.maxDepth = depth;
        two = 0x1;
        four = 0x2;
        this.executorService = Executors.newCachedThreadPool();
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
            int zeroes = Logic2048.getEmptyCountInBoard(currentBoard);
            if (zeroes > 16) {
                nextMove = Move.values()[moved % 4];
            } else {
                nextMove = getNextMove(currentBoard);
            }
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
                case LOST:
                    lost = true;
                    break;
            }
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
        EvaluateMoveTask up = new EvaluateMoveTask(0, maxDepth, currentBoard, logic.moveUp(currentBoard));
        EvaluateMoveTask down = new EvaluateMoveTask(0, maxDepth, currentBoard, logic.moveDown(currentBoard));
        EvaluateMoveTask left = new EvaluateMoveTask(0, maxDepth, currentBoard, logic.moveLeft(currentBoard));
        EvaluateMoveTask right = new EvaluateMoveTask(0, maxDepth, currentBoard, logic.moveRight(currentBoard));
        up.fork();
        down.fork();
        left.fork();
        right.fork();
        float[] evals = {up.join(), down.join(), left.join(), right.join()};
        float max = 0;
        int maxI = -1;
        for (int i = 0; i < 4; i++) {
            if (evals[i] > max){
                max = evals[i];
                maxI = i;
            }
        }
        return max == 0 ? Move.LOST : Move.values()[maxI];
    }
    
    private float maxValue(float... args) {
        float max = Float.MIN_VALUE;
        for (int i = 0; i < args.length; i++) {
            if (args[i] > max) max = args[i];
        }
        return max;
    }

    private float sum(float... args) {
        float total = 0;
        for (int i = 0; i < args.length; i++) {
            total += args[i];
        }
        return total;
    }

    private class EvaluateMoveTask extends RecursiveTask<Float> {
        int depth;
        int maxDepth;
        long currentBoard;
        long previousBoard;

        public EvaluateMoveTask(int depth, int maxDepth, long previousBoard, long currentBoard) {
            this.depth = depth;
            this.maxDepth = maxDepth;
            this.currentBoard = currentBoard;
            this.previousBoard = previousBoard;
        }

        @Override
        protected Float compute() {
            if (previousBoard == currentBoard) return 0.0f;
            if (depth > 1) {
                return evalProbability(depth + 1, maxDepth, currentBoard);
            } else {
                EvaluateMoveTask up = new EvaluateMoveTask(depth + 1, maxDepth, currentBoard, logic.moveUp(currentBoard));
                EvaluateMoveTask down = new EvaluateMoveTask(depth + 1, maxDepth, currentBoard, logic.moveDown(currentBoard));
                EvaluateMoveTask left = new EvaluateMoveTask(depth + 1, maxDepth, currentBoard, logic.moveLeft(currentBoard));
                EvaluateMoveTask right = new EvaluateMoveTask(depth + 1, maxDepth, currentBoard, logic.moveRight(currentBoard));
                up.fork();
                down.fork();
                left.fork();
                right.fork();
                return maxValue(up.join(), down.join(), left.join(), right.join());
            }
            
        }

        private float evalMove(int depth, int maxDepth, long board) {
            if (transpositionTable.containsKey(board)) {
                Eval eval = transpositionTable.get(board);
                if (eval.depth == depth) {
                    counters[1] = counters[1] + 1;
                    return eval.eval;
                }
            }
            int empty = Logic2048.getEmptyCountInBoard(board);
            float[] evals = new float[empty*2];
            for (int i = 0; i < empty; i++) {
                evals[2 * i] = 0.9f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(two, i, board));
                evals[2 * i + 1] = 0.1f * evalProbability(depth, maxDepth, Logic2048.setEmptyPositionTo(four, i, board));
            }
            float sum = sum(evals);
            counters[0] = counters[0] + 1;
            transpositionTable.put(board, new Eval(sum, depth));
            return sum;
        }

        private float evalProbability(int depth, int maxDepth, long board) {
            if (depth >= maxDepth) {
                return logic.evaluate(board);
            }
            float[] evals = {zeroOrEvalMove(depth+1, maxDepth, board, logic.moveUp(board)),
                    zeroOrEvalMove(depth+1, maxDepth, board, logic.moveDown(board)),
                    zeroOrEvalMove(depth+1, maxDepth, board, logic.moveLeft(board)),
                    zeroOrEvalMove(depth+1, maxDepth, board, logic.moveRight(board))};
            return maxValue(evals);
        }
        
        private float zeroOrEvalMove(int depth, int maxDepth, long board, long nextBoard) {
            return board == nextBoard ? 0 : evalMove(depth, maxDepth, nextBoard);
        }


    }
}
