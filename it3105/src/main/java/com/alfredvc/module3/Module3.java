package com.alfredvc.module3;

import com.alfredvc.FunctionParser;
import com.alfredvc.constraint_satisfaction.Constraint;
import com.alfredvc.constraint_satisfaction.ConstraintSatisfaction;
import com.alfredvc.constraint_satisfaction.ConstraintSatisfactionResult;
import com.alfredvc.constraint_satisfaction.Variable;
import com.alfredvc.graphics.Grid2D;
import com.alfredvc.graphics.Grid2DBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import static com.alfredvc.module3.Module3Convenience.parseInput;

/**
 * Created by erpa_ on 9/25/2015.
 */
public class Module3 {
    private Module3Convenience.Module3DataHolder dataHolder;


    JTextArea textArea;
    JButton loadButton;
    JButton resetButton;
    JButton startButton;
    JPanel placeholderGrid;
    Grid2D grid2D;
    JFrame frame;
    JPanel container;
    GridController gridController;
    JLabel generatedNodes;
    JLabel solutionLength;
    JLabel violatedConstraints;
    JLabel undeterminedVariables;
    JLabel poppedNodes;
    JTextField refreshPeriod;
    Comparator<Constraint> constraintComparator;



    public static void main(String[] args) {
        new Module3().run();
    }

    public Module3() {
        constraintComparator = (c1, c2) -> c2.getRating() - c1.getRating();
        container = new JPanel();
        container.setLayout(new GridBagLayout());

        JPanel panel1 = new JPanel();
        panel1.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel1.setLayout(new GridBagLayout());
        textArea = new JTextArea(20, 10);
        JScrollPane scrollPane = new JScrollPane(textArea);


        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.0;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        panel1.add(scrollPane, c);

        loadButton = new JButton("Load");
        loadButton.addActionListener(e -> load());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        panel1.add(loadButton, c);

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> reset());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 1;
        panel1.add(resetButton, c);

        startButton = new JButton("Start");
        startButton.addActionListener(e -> start());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 1;
        panel1.add(startButton, c);

        JLabel solutionLengthLabel = new JLabel("Solution length:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        panel1.add(solutionLengthLabel, c);

        solutionLength = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 2;
        panel1.add(solutionLength, c);

        JLabel nodeCountLabel = new JLabel("Generated nodes:");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 3;
        panel1.add(nodeCountLabel, c);

        generatedNodes = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 3;
        panel1.add(generatedNodes, c);

        JLabel violatedConstraintsLabel = new JLabel("Violated constraints: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 4;
        panel1.add(violatedConstraintsLabel, c);

        violatedConstraints = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 4;
        panel1.add(violatedConstraints, c);

        JLabel undeterminedVariablesLabel = new JLabel("Undetermined variables: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 5;
        panel1.add(undeterminedVariablesLabel, c);

        undeterminedVariables = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 5;
        panel1.add(undeterminedVariables, c);

        JLabel poppedNodesLabel = new JLabel("Popped nodes: ");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 6;
        panel1.add(poppedNodesLabel, c);

        poppedNodes = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 6;
        panel1.add(poppedNodes, c);

        JLabel refreshPeriodLabel = new JLabel("Refresh period(ms):");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 7;
        panel1.add(refreshPeriodLabel, c);

        refreshPeriod = new JTextField("25");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 7;
        panel1.add(refreshPeriod, c);

        placeholderGrid = new Grid2DBuilder()
                .setBackgroundColor(Color.white)
                .setGridWidth(100)
                .setGridHeight(100)
                .createGrid2D();

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1 / 6f;
        constraints.weighty = 1.00;
        container.add(panel1, constraints);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 5 / 6f;
        constraints.weighty = 1.00;
        container.add(placeholderGrid, constraints);
        gridController = new GridController();
    }

    private void reset() {
        if (grid2D != null) grid2D.setVisible(false);
        if (placeholderGrid != null) placeholderGrid.setVisible(true);
        generatedNodes.setText("0");
        solutionLength.setText("0");
        poppedNodes.setText("0");
        gridController.cancelNow();
    }

    private void load() {
        reset();
        String text = textArea.getText();


        dataHolder = parseInput(text);
        gridController.setVariables(dataHolder.getVariables());

        placeholderGrid.setVisible(false);

        for (int i = 0; i < container.getComponentCount(); i++) {
            if (container.getComponent(i).equals(grid2D)) {
                container.remove(i);
            }
        }

        grid2D = new Grid2DBuilder()
                .setGridWidth(dataHolder.getWidth())
                .setGridHeight(dataHolder.getHeight())
                .setBackgroundColor(Color.white)
                .createGrid2D();
        grid2D.setSize(new Dimension(600, 700));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 5 / 6f;
        constraints.weighty = 1.00;
        container.add(grid2D, constraints);
        grid2D.setVisible(true);

    }

    private void start() {
        ConstraintSatisfaction<Boolean> constraintSatisfaction = new ConstraintSatisfaction<>(dataHolder.getConstraints(), dataHolder.getVariables(), constraintComparator);
        constraintSatisfaction.addCurrentVariableDomainChangeListener(gridController);
        gridController.run(grid2D, Integer.parseInt(refreshPeriod.getText()), dataHolder.getNameToPointMap());
        new Thread(() -> {
            long start = System.nanoTime();
            ConstraintSatisfactionResult<Boolean> result = constraintSatisfaction.solve();
            long end = System.nanoTime();
            //System.out.println("Took " + (end - start) / 1000000 + " ms");
            gridController.cancelWhenFinished();
            solutionLength.setText(result.getSolutionLength() + "");
            generatedNodes.setText(result.generatedNodes() + "");
            violatedConstraints.setText(result.getViolatedConstraints() + "");
            undeterminedVariables.setText(result.getVariablesWithDomainNotEqualToOne() + "");
            poppedNodes.setText(result.getNodesPoppedFromTheAgenda() + "");
        }).start();
    }

    private void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Module 3");
            frame.add(container);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private class GridController implements ConstraintSatisfaction.CurrentVariableDomainChangeListener<Boolean> {
        private final LinkedList<BitSet[]> solutionsToDraw;
        private List<Variable<Boolean>> variables;
        private java.util.Timer timer;
        private UpdateTask updateTask;
        boolean shouldFinish;

        public GridController() {
            this.solutionsToDraw = new LinkedList<>();
            this.shouldFinish = false;
        }

        public void setVariables(List<Variable<Boolean>> variables) {
            this.variables = variables;
        }

        @Override
        public void currentSolutionChanged(BitSet[] bitSet) {
            solutionsToDraw.offerLast(bitSet);
        }

        public void run(Grid2D grid2D, int refreshPeriod, Map<String, Point> nameToPointMap) {
            this.timer = new java.util.Timer();
            this.updateTask = new UpdateTask(grid2D, solutionsToDraw, variables, timer, nameToPointMap);
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
            private final Grid2D grid2D;
            private final List<Variable<Boolean>> vars;
            private final LinkedList<BitSet[]> solutionsToDraw;
            private final AtomicInteger atomicInteger;
            private final java.util.Timer timer;
            private final Map<String, Point> nameToPointMap;
            private boolean shouldCancel;

            public UpdateTask(Grid2D grid2D, LinkedList<BitSet[]> solutionsToDraw,
                              List<Variable<Boolean>> vars, java.util.Timer timer, Map<String, Point> nameToPointMap) {
                this.grid2D = grid2D;
                this.vars = vars;
                this.solutionsToDraw = solutionsToDraw;
                this.atomicInteger = new AtomicInteger();
                this.timer = timer;
                this.shouldCancel = false;
                this.nameToPointMap = nameToPointMap;
            }

            public void setShouldCancel() {
                this.shouldCancel = true;
            }

            @Override
            public void run() {
                if (!solutionsToDraw.isEmpty()) {
                    atomicInteger.incrementAndGet();
                    BitSet[] solutionToDraw = solutionsToDraw.pollFirst();
                    for (int i = 0; i < solutionToDraw.length; i++) {
                        Variable<Boolean> toDraw = vars.get(i);
                        if (solutionToDraw[i].cardinality() == 1) {
                            boolean b = solutionToDraw[i].stream().findFirst().getAsInt() == 0 ? false : true;
                            this.grid2D.setPoint(nameToPointMap.get(toDraw.getName()), b ? Color.BLUE : Color.RED);
                        } else if (solutionToDraw[i].cardinality() == 2) {
                            this.grid2D.setPoint(nameToPointMap.get(toDraw.getName()), Color.BLACK);
                        }
                    }
                    this.grid2D.repaint();
                } else if (shouldCancel) {
                    timer.cancel();
                    timer.purge();
                }
            }
        }
    }
}
