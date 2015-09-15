package com.alfredvc.module2;

import com.alfredvc.FunctionParser;
import com.alfredvc.ParsedFunction;
import com.alfredvc.constraint_satisfaction.Constraint;
import com.alfredvc.constraint_satisfaction.ConstraintSatisfaction;
import com.alfredvc.constraint_satisfaction.ConstraintSatisfactionResult;
import com.alfredvc.constraint_satisfaction.Variable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Hello world!
 */
public class Module2 {

    private static String notEqualFunction = "boolean(Integer a,b) -> a != b";
    private Module2DataHolder dataHolder;

    JTextArea textArea;
    JTextField textField;
    JButton loadButton;
    JButton resetButton;
    JButton startButton;
    JPanel placeholderGraph;
    Graph2D graph2D;
    JFrame frame;
    JPanel container;
    GraphController graphController;
    JLabel generatedNodes;
    JLabel solutionLength;
    JLabel violatedConstraints;
    JLabel verticesWithoutColor;
    JLabel poppedNodes;
    JLabel colorSetSizeLabel;
    JTextField refreshPeriod;



    public static void main(String[] args) {
        new Module2().run();
    }

    public Module2() {
        container = new JPanel();
        container.setLayout(new GridBagLayout());

        JPanel panel1 = new JPanel();
        panel1.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel1.setLayout(new GridBagLayout());
        textArea = new JTextArea(20, 10);
        JScrollPane scrollPane = new JScrollPane(textArea);
        textField = new JTextField();
        colorSetSizeLabel = new JLabel("Color set size: ");


        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 0;
        panel1.add(colorSetSizeLabel, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        c.gridwidth = 2;
        panel1.add(textField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 1;
        panel1.add(scrollPane, c);

        loadButton = new JButton("Load");
        loadButton.addActionListener(e -> load());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        panel1.add(loadButton, c);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> reset());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 2;
        panel1.add(resetButton, c);

        startButton = new JButton("Start");
        startButton.addActionListener(e -> start());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 2;
        panel1.add(startButton, c);

        JLabel solutionLengthLabel = new JLabel("Solution length:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 3;
        panel1.add(solutionLengthLabel, c);

        solutionLength = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 3;
        panel1.add(solutionLength, c);

        JLabel nodeCountLabel = new JLabel("Generated nodes:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 4;
        panel1.add(nodeCountLabel, c);

        generatedNodes = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 4;
        panel1.add(generatedNodes, c);

        JLabel violatedConstraintsLabel = new JLabel("Violated constraints: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 5;
        panel1.add(violatedConstraintsLabel, c);

        violatedConstraints = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 5;
        panel1.add(violatedConstraints, c);

        JLabel verticesWithoutColorLabel = new JLabel("Vertices without color: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 6;
        panel1.add(verticesWithoutColorLabel, c);

        verticesWithoutColor = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 6;
        panel1.add(verticesWithoutColor, c);

        JLabel poppedNodesLabel = new JLabel("Popped nodes: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 7;
        panel1.add(poppedNodesLabel, c);

        poppedNodes = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 7;
        panel1.add(poppedNodes, c);

        JLabel refreshPeriodLabel = new JLabel("Refresh period(ms):");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 8;
        panel1.add(refreshPeriodLabel, c);

        refreshPeriod = new JTextField("25");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 8;
        panel1.add(refreshPeriod, c);

        placeholderGraph = new JPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1 / 6f;
        constraints.weighty = 1.00;
        container.add(panel1, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 5 / 6f;
        constraints.weighty = 1.00;
        container.add(placeholderGraph, constraints);
        graphController = new GraphController();
    }

    private void reset() {
        if (graph2D != null) graph2D.setVisible(false);
        if (placeholderGraph != null) placeholderGraph.setVisible(true);
        generatedNodes.setText("0");
        solutionLength.setText("0");
        graphController.cancelNow();
    }

    private void load() {
        reset();
        String text = textArea.getText();
        int colorSetSize = Integer.parseInt(textField.getText());


        dataHolder = parseInput(text, colorSetSize);
        graphController.setVariables(dataHolder.getVariables());

        placeholderGraph.setVisible(false);

        for (int i = 0; i < container.getComponentCount(); i++) {
            if (container.getComponent(i).equals(graph2D)) {
                container.remove(i);
            }
        }

        graph2D = new Graph2D(dataHolder.getPoints(), dataHolder.getLines());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 5 / 6f;
        constraints.weighty = 1.00;
        container.add(graph2D, constraints);
        graph2D.setVisible(true);

    }

    private void start() {
        ConstraintSatisfaction<Integer> constraintSatisfaction = new ConstraintSatisfaction<>(dataHolder.getConstraints(), dataHolder.getVariables());
        constraintSatisfaction.addCurrentVariableDomainChangeListener(graphController);
        graphController.run(graph2D, Integer.parseInt(refreshPeriod.getText()));
        new Thread(() -> {
            ConstraintSatisfactionResult<Integer> result = constraintSatisfaction.solve();
            graphController.cancelWhenFinished();
            solutionLength.setText(result.getSolutionLength() + "");
            generatedNodes.setText(result.generatedNodes() + "");
            violatedConstraints.setText(result.getViolatedConstraints() + "");
            verticesWithoutColor.setText(result.getVariablesWithDomainNotEqualToOne() + "");
            poppedNodes.setText(result.getNodesPoppedFromTheAgenda() + "");
        }).start();
    }

    private void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Module 2");
            frame.add(container);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
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

    private class GraphController implements ConstraintSatisfaction.CurrentVariableDomainChangeListener<Integer> {
        private final LinkedList<BitSet[]> solutionsToDraw;
        private List<Variable<Integer>> variables;
        private java.util.Timer timer;
        private UpdateTask updateTask;
        boolean shouldFinish;

        public GraphController() {
            this.solutionsToDraw = new LinkedList<>();
            this.shouldFinish = false;
        }

        public void setVariables(List<Variable<Integer>> variables) {
            this.variables = variables;
        }

        @Override
        public void currentSolutionChanged(BitSet[] bitSet) {
            solutionsToDraw.offerLast(bitSet);
        }

        public void run(Graph2D graph2D, int refreshPeriod) {
            this.timer = new java.util.Timer();
            this.updateTask = new UpdateTask(graph2D, solutionsToDraw, variables, timer);
            this.timer.scheduleAtFixedRate(updateTask, 0, refreshPeriod);
        }

        public void cancelWhenFinished(){
            this.updateTask.setShouldCancel();
        }

        public void cancelNow() {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer.purge();
            }
            this.solutionsToDraw.clear();
            this.variables = new ArrayList<>();
        }

        private class UpdateTask extends TimerTask {
            private final Graph2D graph2D;
            private final List<Variable<Integer>> vars;
            private final LinkedList<BitSet[]> solutionsToDraw;
            private final AtomicInteger atomicInteger;
            private final java.util.Timer timer;
            private boolean shouldCancel;

            public UpdateTask(Graph2D graph2D, LinkedList<BitSet[]> solutionsToDraw, List<Variable<Integer>> vars, java.util.Timer timer) {
                this.graph2D = graph2D;
                this.vars = vars;
                this.solutionsToDraw = solutionsToDraw;
                this.atomicInteger = new AtomicInteger();
                this.timer = timer;
                this.shouldCancel = false;
            }

            public void setShouldCancel() {
                this.shouldCancel = true;
            }

            @Override
            public void run() {
                if (!solutionsToDraw.isEmpty()) {
                    atomicInteger.incrementAndGet();
                    BitSet[] solutionToDraw = solutionsToDraw.pollFirst();
                    this.graph2D.setAllWhite();
                    for (int i = 0; i < solutionToDraw.length; i++) {
                        if (solutionToDraw[i].cardinality() == 1) {
                            Variable<Integer> toDraw = vars.get(i);
                            int indexToDraw = solutionToDraw[i].stream().findFirst().getAsInt();
                            this.graph2D.setPointColor(Integer.parseInt(toDraw.getName()), toDraw.getDomain().get(indexToDraw));
                        }
                    }
                    this.graph2D.repaint();
                } else if (shouldCancel) {
                    timer.cancel();
                    timer.purge();
                }
            }
        }
    }

}
