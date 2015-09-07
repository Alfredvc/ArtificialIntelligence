package modules;

import com.alfredvc.graphics.Grid2D;
import com.alfredvc.graphics.Grid2DBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import a_star.AStar;
import bfs.BFS;
import dfs.DFS;
import navigation.NavigationState;
import search_algorithm.Node;
import search_algorithm.SearchAlgorithm;
import search_algorithm.SearchAlgorithmResult;

/**
 * Created by erpa_ on 8/28/2015.
 */
public class Module1 {

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
                .setxSize(100)
                .setySize(100)
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

    public void run() {
        SwingUtilities.invokeLater(() -> {
            frame = new JFrame("Module 1");
            frame.add(container);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }

    private void load() {
        reset();
        String text = textArea.getText();

        state = NavigationState.fromString(text);

        placeholderGrid.setVisible(false);

        for (int i = 0; i < container.getComponentCount(); i++) {
            if (container.getComponent(i).equals(grid2D)) {
                container.remove(i);
            }
        }

        grid2D = new Grid2DBuilder()
                .setxSize(state.getxSize())
                .setySize(state.getySize())
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
        SearchAlgorithm searchAlgorithm;
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
        searchAlgorithm.addNodeEvaluateListener(gridController);
        gridController.run(grid2D, Integer.parseInt(refreshPeriod.getText()));
        SearchAlgorithmResult result = searchAlgorithm.search();
        nodeCount.setText(result.getGeneratedNodes() + "");
        solutionLength.setText(result.getSolutionLength() + "");
    }

    private class GridController implements SearchAlgorithm.NodeEvaluateListener {
        private final LinkedList<List<Point>> solutionsToDraw;
        private Timer timer;
        private TimerTask updateTask;

        public GridController() {
            this.solutionsToDraw = new LinkedList<>();
        }

        @Override
        public void onNodeEvaluated(Node node) {
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
}
