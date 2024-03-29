package com.alfredvc.module1;

import com.alfredvc.graphics.Grid2D;
import com.alfredvc.graphics.Grid2DBuilder;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import search_algorithm.AStar;
import search_algorithm.BFS;
import search_algorithm.DFS;
import search_algorithm.Node;
import search_algorithm.SearchAlgorithm;
import search_algorithm.SearchAlgorithmResult;

/**
 * Created by Alfredvc on 8/28/2015.
 */
public class Module1 {

    private static final String patternString = "\\((.*?)\\)";
    private static final Pattern pattern = Pattern.compile(patternString);
    private static final int MAX_NODES = 1000000;
    private static final int ASTAR = 0;
    private static final int BFS = 1;
    private static final int DFS = 2;
    JRadioButton aStarRadioButton;
    JRadioButton BFSRadioButton;
    JRadioButton DFSRadioButton;
    ButtonGroup radioButtons;
    JTextArea textArea;
    JButton loadButton;
    JButton resetButton;
    JButton startButton;
    Grid2D placeholderGrid;
    Grid2D grid2D;
    JFrame frame;
    JPanel container;
    NavigationState state;
    GridController gridController;
    JLabel nodeCount;
    JLabel solutionLength;
    JTextField refreshPeriod;


    public Module1() {
        initializeFontSize();
        container = new JPanel();
        container.setLayout(new GridBagLayout());

        JPanel panel1 = new JPanel();
        panel1.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel1.setLayout(new GridBagLayout());
        textArea = new JTextArea(20, 10);
        JScrollPane scrollPane = new JScrollPane(textArea);

        aStarRadioButton = new JRadioButton("A*");
        aStarRadioButton.setMnemonic(ASTAR);
        BFSRadioButton = new JRadioButton("BFS");
        BFSRadioButton.setMnemonic(BFS);
        DFSRadioButton = new JRadioButton("DFS");
        DFSRadioButton.setMnemonic(DFS);

        radioButtons = new ButtonGroup();
        radioButtons.add(aStarRadioButton);
        radioButtons.add(BFSRadioButton);
        radioButtons.add(DFSRadioButton);
        radioButtons.setSelected(aStarRadioButton.getModel(), true);

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        panel1.add(aStarRadioButton, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 1;
        c.gridy = 0;
        panel1.add(BFSRadioButton, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 0;
        panel1.add(DFSRadioButton, c);

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

        nodeCount = new JLabel("0");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 4;
        panel1.add(nodeCount, c);

        JLabel refreshPeriodLabel = new JLabel("Refresh period(ms):");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 5;
        panel1.add(refreshPeriodLabel, c);

        refreshPeriod = new JTextField("25");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 0.5;
        c.gridx = 2;
        c.gridy = 5;
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

    public static void main(String args[]) {
        new Module1().run();
    }

    private static int intFromString(String input) {
        return Integer.parseInt(input.trim());
    }

    public static NavigationState fromString(String s) {
        Matcher matcher = Module1.pattern.matcher(s);
        List<String> input = new ArrayList<>();
        while (matcher.find()) {
            input.add(matcher.group().replaceAll("[()]", ""));
        }
        int xSize = Module1.intFromString(input.get(0).split(",")[0]);
        int ySize = Module1.intFromString(input.get(0).split(",")[1]);
        Point start = new Point(Module1.intFromString(input.get(1).split(",")[0]), Module1.intFromString(input.get(1).split(",")[1]));
        Point goal = new Point(Module1.intFromString(input.get(2).split(",")[0]), Module1.intFromString(input.get(2).split(",")[1]));
        List<Point> obstaclePoints = new ArrayList<>();

        for (int i = 3; i < input.size(); i++) {
            int x0 = Module1.intFromString(input.get(i).split(",")[0]);
            int y0 = Module1.intFromString(input.get(i).split(",")[1]);
            int dx = Module1.intFromString(input.get(i).split(",")[2]);
            int dy = Module1.intFromString(input.get(i).split(",")[3]);
            for (int x = 0; x < dx; x++) {
                for (int y = 0; y < dy; y++) {
                    obstaclePoints.add(new Point(x0 + x, y0 + y));
                }
            }
        }
        boolean[][] obstacles = NavigationState.obstacleArrayFromObstaclePoints(obstaclePoints, xSize, ySize);
        return new NavigationState(start, goal, xSize, ySize, obstacles);
    }

    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Module 1");
            frame.add(container);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void load() {
        reset();
        String text = textArea.getText();

        state = fromString(text);

        placeholderGrid.setVisible(false);

        for (int i = 0; i < container.getComponentCount(); i++) {
            if (container.getComponent(i).equals(grid2D)) {
                container.remove(i);
            }
        }

        grid2D = new Grid2DBuilder()
                .setGridWidth(state.getxSize())
                .setGridHeight(state.getySize())
                .setBackgroundColor(Color.white)
                .createGrid2D();
        grid2D.setPoints(NavigationState.obstaclePointsFromObstacleArray(state.getObstacles()), Color.black);
        grid2D.setPoint(state.getLocation(), Color.green);
        grid2D.setPoint(state.getGoal(), Color.red);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 5 / 6f;
        constraints.weighty = 1.00;
        container.add(grid2D, constraints);
        grid2D.setVisible(true);

    }

    private void reset() {
        if (grid2D != null) grid2D.setVisible(false);
        if (placeholderGrid != null) placeholderGrid.setVisible(true);
        nodeCount.setText("0");
        solutionLength.setText("0");
        gridController.cancel();
    }

    private void start() {
        SearchAlgorithm<NavigationState> searchAlgorithm;
        switch (radioButtons.getSelection().getMnemonic()) {
            case ASTAR:
                searchAlgorithm = new AStar(state, Integer.MAX_VALUE);
                break;
            case BFS:
                searchAlgorithm = new BFS(state, MAX_NODES);
                break;
            case DFS:
                searchAlgorithm = new DFS(state, MAX_NODES);
                break;
            default:
                throw new IllegalStateException("Unreachable");
        }
        searchAlgorithm.addNodePopListener(gridController);
        gridController.run(grid2D, Integer.parseInt(refreshPeriod.getText()));
        SearchAlgorithmResult result = searchAlgorithm.search();
        nodeCount.setText(result.getGeneratedNodes() + "");
        solutionLength.setText(result.getSolutionLength() + "");
    }

    private class GridController implements SearchAlgorithm.NodePopListener {
        private final LinkedList<List<Point>> solutionsToDraw;
        private Timer timer;
        private TimerTask updateTask;

        public GridController() {
            this.solutionsToDraw = new LinkedList<>();
        }

        @Override
        public void onNodePopped(Node node) {
            List<Point> toAdd = new ArrayList<>();
            Node currentNode = node;
            while (currentNode.getParent() != null) {
                toAdd.add(((NavigationState) currentNode.getState()).getLocation());
                currentNode = currentNode.getParent();
            }
            toAdd.add(((NavigationState) currentNode.getState()).getLocation());
            solutionsToDraw.offerLast(toAdd);
        }

        public void run(Grid2D grid2D, int refreshPeriod) {
            this.timer = new Timer();
            this.updateTask = new UpdateTask(grid2D, solutionsToDraw, timer);
            this.timer.scheduleAtFixedRate(updateTask, 0, refreshPeriod);
        }

        public void cancel() {
            if (this.timer != null) {
                this.timer.cancel();
                this.timer.purge();
            }
            this.solutionsToDraw.clear();
        }

        private class UpdateTask extends TimerTask {
            private final Grid2D grid2D;
            private final LinkedList<List<Point>> solutionsToDraw;
            private final AtomicInteger atomicInteger;
            private final Timer timer;
            private List<Point> lastAddedPoints;

            public UpdateTask(Grid2D grid2D, LinkedList<List<Point>> solutionsToDraw, Timer timer) {
                this.grid2D = grid2D;
                this.solutionsToDraw = solutionsToDraw;
                this.lastAddedPoints = new ArrayList<>();
                this.atomicInteger = new AtomicInteger();
                this.timer = timer;
            }

            @Override
            public void run() {
                if (!solutionsToDraw.isEmpty()) {
                    atomicInteger.incrementAndGet();
                    this.grid2D.setPoints(lastAddedPoints, Color.white);
                    lastAddedPoints = solutionsToDraw.pollFirst();
                    this.grid2D.setPoints(lastAddedPoints, Color.red);
                    this.grid2D.repaint();
                } else if (atomicInteger.get() > 0) {
                    timer.cancel();
                    timer.purge();
                }
            }
        }
    }

    public static void initializeFontSize() {
        float multiplier = 2.0f;
        UIDefaults defaults = UIManager.getDefaults();
        int i = 0;
        for (Enumeration e = defaults.keys(); e.hasMoreElements(); i++) {
            Object key = e.nextElement();
            Object value = defaults.get(key);
            if (value instanceof Font) {
                Font font = (Font) value;
                int newSize = Math.round(font.getSize() * multiplier);
                if (value instanceof FontUIResource) {
                    defaults.put(key, new FontUIResource(font.getName(), font.getStyle(), newSize));
                } else {
                    defaults.put(key, new Font(font.getName(), font.getStyle(), newSize));
                }
            }
        }
    }
}
