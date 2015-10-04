package com.alfredvc.module3;

import com.alfredvc.FunctionParser;
import com.alfredvc.constraint_satisfaction.Constraint;
import com.alfredvc.constraint_satisfaction.Variable;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Created by erpa_ on 10/4/2015.
 */
public class Module3Convenience {



    private static String methodBody = "int currentConstraint = 0;\n" +
            "int currentLength = 0;\n" +
            "for (int i = 0; i < vars.length; i++) {\n" +
            "    if (currentConstraint == c.length && vars[i]) {\n" +
            "        return false;\n" +
            "    }\n" +
            "    if (currentLength > 0) {\n" +
            "        if (vars[i]){\n" +
            "            currentLength++;\n" +
            "        } else {\n" +
            "            if (currentConstraint >= c.length || currentLength != c[currentConstraint]) return false;\n" +
            "            currentConstraint++;\n" +
            "            currentLength = 0;\n" +
            "        }\n" +
            "    } else {\n" +
            "        if (vars[i]) currentLength++;\n" +
            "    }\n" +
            "}\n" +
            "if (currentConstraint == c.length ||\n" +
            "        (currentConstraint == c.length - 1 && currentLength == c[currentConstraint]) ) {\n" +
            "    return true;\n" +
            "} else {\n" +
            "    return false;\n" +
            "}";

    public static class Module3DataHolder {
        private final List<Constraint> constraints;
        private final List<Variable<Boolean>> variables;
        private final Map<String, Point> nameToPointMap;
        private final int width;
        private final int height;

        public Module3DataHolder(List<Constraint> constraints, List<Variable<Boolean>> variables, int width, int height, Map<String, Point> nameToPointMap) {
            this.constraints = constraints;
            this.variables = variables;
            this.width = width;
            this.height = height;
            this.nameToPointMap = nameToPointMap;
        }

        public Map<String, Point> getNameToPointMap() {
            return nameToPointMap;
        }

        public List<Constraint> getConstraints() {
            return constraints;
        }

        public List<Variable<Boolean>> getVariables() {
            return variables;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static Module3DataHolder parseInput(String input){
        List<Variable<Boolean>> variables = new ArrayList<>();
        List<Constraint> constraints = new ArrayList<>();
        Map<String, Point> nameToPointMap = new HashMap<>();
        String[] lines = input.split("\n");
        int width = Integer.parseInt(lines[0].split(" ")[0]);
        int height = Integer.parseInt(lines[0].split(" ")[1]);

        List<Boolean> booleans = Arrays.asList(Boolean.FALSE, Boolean.TRUE);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String varName = "v" + x + "_" + y;
                variables.add(new Variable<>(varName, booleans));
                nameToPointMap.put(varName, new Point(x, height - y -1));
            }
        }

        //Row constraints
        for (int i = 1; i < height+1; i++) {
            String[] c = lines[i].trim().split(" ");
            int rating = c.length - 1;
            for (String str : c) rating += Integer.parseInt(str);
            StringJoiner cJoiner = new StringJoiner(",", "{", "};");
            for (String s : c) cJoiner.add(s);

            StringJoiner vJoiner = new StringJoiner(",");
            for( int a = 0; a < width; a++) {
                vJoiner.add("v"+a +"_"+ (height - i));
            }

            String constraintString = getConstraintString(cJoiner, vJoiner);
            Constraint toAdd = new Constraint(FunctionParser.fromString(constraintString));
            toAdd.setRating(rating + 1);
            constraints.add(toAdd);
        }

        //Column constraints
        for (int i = height+1; i < lines.length; i++) {
            String[] c = lines[i].trim().split(" ");
            int rating = c.length - 1;
            for (String str : c) rating += Integer.parseInt(str);
            StringJoiner cJoiner = new StringJoiner(",", "{", "};");
            for (String s : c) cJoiner.add(s);

            StringJoiner vJoiner = new StringJoiner(",");
            for( int a = 0; a < height; a++) {
                vJoiner.add("v"+(i - height - 1)+"_"+a);
            }

            String constraintString = getConstraintString(cJoiner, vJoiner);
            Constraint toAdd = new Constraint(FunctionParser.fromString(constraintString));
            toAdd.setRating(rating);
            constraints.add(toAdd);
        }

        return new Module3DataHolder(constraints, variables, width, height, nameToPointMap);
    }

    private static String getConstraintString(StringJoiner cJoiner, StringJoiner vJoiner) {
        return "boolean(Boolean " + vJoiner.toString() + ")->int c[] ="
                + cJoiner.toString() + "\nboolean[] vars = {" + vJoiner.toString() +"};\n"
                + methodBody;
    }
}
