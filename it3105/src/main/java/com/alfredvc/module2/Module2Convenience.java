package com.alfredvc.module2;

import com.alfredvc.FunctionParser;
import com.alfredvc.ParsedFunction;
import com.alfredvc.constraint_satisfaction.Constraint;
import com.alfredvc.constraint_satisfaction.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by erpa_ on 10/4/2015.
 */
public class Module2Convenience {

    private static String notEqualFunction = "boolean(Integer a,b) -> a != b";

    public static class Module2DataHolder {
        private final List<DoublePoint> points;
        private final List<PointLine> lines;
        private final List<Constraint> constraints;
        private final List<Variable<Integer>> variables;
        private final int colorSetSize;

        public Module2DataHolder(List<DoublePoint> points, List<PointLine> lines, List<Constraint> constraints, List<Variable<Integer>> variables, int colorSetSize) {
            this.points = points;
            this.lines = lines;
            this.constraints = constraints;
            this.variables = variables;
            this.colorSetSize = colorSetSize;
        }

        public List<DoublePoint> getPoints() {
            return points;
        }

        public List<PointLine> getLines() {
            return lines;
        }

        public List<Constraint> getConstraints() {
            return constraints;
        }

        public List<Variable<Integer>> getVariables() {
            return variables;
        }

        public int getColorSetSize() {
            return colorSetSize;
        }
    }
    public static Module2DataHolder parseInput(String input, int colorSetSize) {

        //All constraints in this problem use the same function
        ParsedFunction function = FunctionParser.fromString(notEqualFunction);

        String[] inputLines = input.split("\\n");
        int vertices = Integer.parseInt(inputLines[0].split("\\s")[0]);
        int edges = Integer.parseInt(inputLines[0].split("\\s")[1]);

        List<DoublePoint> points = new ArrayList<>(vertices);
        List<PointLine> lines = new ArrayList<>(edges);
        List<Constraint> constraints = new ArrayList<>(edges);
        List<Variable<Integer>> variables = new ArrayList<>(vertices);

        List<Integer> domain = new ArrayList<>(colorSetSize);
        for (int i = 0; i < colorSetSize; i++) {
            domain.add(i+1);
        }


        for (int i = 1; i < vertices + 1; i++) {
            String[] line = inputLines[i].split("\\s");
            points.add(new DoublePoint(Double.parseDouble(line[1]), Double.parseDouble(line[2])));
            variables.add(new Variable<>(line[0], domain));
        }

        for (int i = vertices + 1; i < vertices + edges + 1; i++) {
            String[] line = inputLines[i].split("\\s");
            lines.add(new PointLine(points.get(Integer.parseInt(line[0])), points.get(Integer.parseInt(line[1]))));
            constraints.add(new Constraint(function, Arrays.asList(line[0], line[1])));
        }
        return new Module2DataHolder(points, lines, constraints, variables, colorSetSize);
    }
}
