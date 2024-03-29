package com.alfredvc.module4;

import com.google.common.io.Files;

import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.SimpleBounds;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.CMAESOptimizer;
import org.apache.commons.math3.random.MersenneTwister;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by erpa_ on 10/10/2015.
 */
public class Player2048Test {

    @Ignore
    @Test
    public void optimize() throws IOException {
        int maxIters = 150;
        final MultivariateOptimizer optimizer=new CMAESOptimizer(maxIters, 1E-9, true, 3, 50, new MersenneTwister(42), false, (iteration, previous, current) -> false);

        final PointValuePair result=optimizer.optimize(GoalType.MAXIMIZE, new MaxEval(maxIters), new InitialGuess(Logic2048.DEFAULT_PARAMS), new ObjectiveFunction(point ->
            new FJPlayer2048(4, FJPlayer2048.Mode.PARALLEL, new Logic2048(point), false).play().getFinalScore()
        ), new CMAESOptimizer.Sigma(new double[]{5, 30, 5, 5, 500}), new CMAESOptimizer.PopulationSize(7), new SimpleBounds(new double[]{0,0,0,0,0},new double[]{10, 100, 10 , 10, 10000}));
        System.out.println(Arrays.toString(result.getPoint()));
        FJPlayer2048 p = new FJPlayer2048(7, FJPlayer2048.Mode.PARALLEL, new Logic2048(result.getPoint()), true);
        System.out.println(p.play());
        System.in.read();
    }

    @Ignore
    @Test
    public void testOne() throws IOException {
        FJPlayer2048 p = new FJPlayer2048(7, FJPlayer2048.Mode.PARALLEL, new Logic2048(), true);
        System.out.println(p.play());
        System.in.read();
    }

    @Ignore
    @Test
    public void benchmark() {
        int tries = 4;
        FJPlayer2048.FinalStats[] finalStats = new FJPlayer2048.FinalStats[tries];
        int sum = 0;
        for (int i = 0; i < tries; i++) {

            FJPlayer2048 p = new FJPlayer2048(10, FJPlayer2048.Mode.PARALLEL, new Logic2048(i,i), false);
            finalStats[i] = p.play();
            System.out.println(finalStats[i]);
            sum += finalStats[i].finalScore;
            p.close();
        }
        System.out.println(sum / tries);
        while (true);
    }

    @Ignore
    @Test
    public void multiBench() throws ExecutionException, InterruptedException, IOException {
        ForkJoinPool pool = new ForkJoinPool();
        int iterationsPerDepth = 16;
        int parallelism = 8;
        ArrayList<ForkJoinTask<FJPlayer2048.FinalStats>> tasks;
        File out = new File("out.txt");
        for(int i = 0; i < 12; i++) {
            List<FJPlayer2048.FinalStats> finalStats = new ArrayList<>(iterationsPerDepth);
            for (int a = 0; a < iterationsPerDepth/parallelism; a++) {
                tasks = new ArrayList<>(parallelism);
                for (int y = 0; y < parallelism; y++) {
                    tasks.add(new FinalStatsRecursiveTask(i));
                }
                List<Future<FJPlayer2048.FinalStats>> futures = tasks.stream().map(pool::submit).collect(Collectors.toList());
                pool.awaitQuiescence(99999999, TimeUnit.DAYS);
                for (Future<FJPlayer2048.FinalStats> f : futures) {
                    finalStats.add(f.get());
                }

            }
            String toWrite = "iterations=" + iterationsPerDepth + ", depth=" + i + ", " + fromList(finalStats).toString() + "\n";
            System.out.print(toWrite);
            Files.append(toWrite, out, Charset.forName("UTF-8"));
        }
    }

    private static class FinalStatsRecursiveTask extends RecursiveTask<FJPlayer2048.FinalStats> {
        private final int i;

        public FinalStatsRecursiveTask(int i) {
            this.i = i;
        }

        @Override
        protected FJPlayer2048.FinalStats compute() {
            FJPlayer2048 p = new FJPlayer2048(i, FJPlayer2048.Mode.SERIAL, new Logic2048(), false);
            return p.play();
        }
    }

    public FJPlayer2048.FinalStats fromList(List<FJPlayer2048.FinalStats> list) {
        double time = 0;
        long moves = 0;
        long score = 0;
        for(FJPlayer2048.FinalStats s : list) {
            time+=s.getTimeTaken();
            moves+=s.getMovesMade();
            score+=s.getFinalScore();
        }
        return new FJPlayer2048.FinalStats(time/list.size(), moves/list.size(), score/list.size());
    }
}
