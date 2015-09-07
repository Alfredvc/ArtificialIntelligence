package a_star;

import org.junit.Test;

import java.awt.*;
import java.util.Arrays;

import navigation.NavigationState;
import search_algorithm.Node;
import search_algorithm.SearchAlgorithmResult;

import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by erpa_ on 8/27/2015.
 */
public class AStarTest {
    @Test
    public void aStarNavigationTest_noObstacles() {
        Point parent1 = new Point(0, 0);
        Point parent2_1 = new Point(1, 0);
        Point parent2_2 = new Point(0, 1);
        Point goal = new Point(1, 1);

        boolean[][] obstacles = {{false, false},
                {false, false}};
        NavigationState navigationState = new NavigationState(new Point(0, 0), new Point(1, 1), 2, 2, obstacles);
        AStar aStar = new AStar(navigationState, Integer.MAX_VALUE);
        SearchAlgorithmResult result = aStar.search();
        assertThat(result.getStatus(), is(SearchAlgorithmResult.Status.SUCCEEDED));
        Node finalNode = result.getFinalNode();
        assertThat(((NavigationState) finalNode.getState()).getLocation(), equalTo(goal));
        assertThat(((NavigationState) finalNode.getParent().getState()).getLocation(), anyOf(equalTo(parent2_1), equalTo(parent2_2)));
        assertThat(((NavigationState) finalNode.getParent().getParent().getState()).getLocation(), equalTo(parent1));
    }

    @Test
    public void aStarNavigationTest_oneObstacles() {
        Point parent1 = new Point(0, 0);
        Point parent2_1 = new Point(1, 0);
        Point goal = new Point(1, 1);

        boolean[][] obstacles = NavigationState.obstacleArrayFromObstaclePoints(Arrays.asList(new Point(0, 1)), 2, 2);

        NavigationState navigationState = new NavigationState(new Point(0, 0), new Point(1, 1), 2, 2, obstacles);
        AStar aStar = new AStar(navigationState, Integer.MAX_VALUE);
        SearchAlgorithmResult result = aStar.search();
        assertThat(result.getStatus(), is(SearchAlgorithmResult.Status.SUCCEEDED));
        Node finalNode = result.getFinalNode();
        assertThat(((NavigationState) finalNode.getState()).getLocation(), equalTo(goal));
        assertThat(((NavigationState) finalNode.getParent().getState()).getLocation(), equalTo(parent2_1));
        assertThat(((NavigationState) finalNode.getParent().getParent().getState()).getLocation(), equalTo(parent1));
    }

    @Test
    public void aStarNavigationTest_notSolvable() {
        boolean[][] obstacles = NavigationState.obstacleArrayFromObstaclePoints(Arrays.asList(new Point(0, 1), new Point(1, 0)), 2, 2);
        NavigationState navigationState = new NavigationState(new Point(0, 0), new Point(1, 1), 2, 2, obstacles);
        AStar aStar = new AStar(navigationState, Integer.MAX_VALUE);
        SearchAlgorithmResult result = aStar.search();
        assertThat(result.getStatus(), is(SearchAlgorithmResult.Status.FAILED));
    }

    @Test
    public void aStarNavigationTest_exampleScenario() {
        String input = "(6,6)\n" +
                "(1,0) (5,5)\n" +
                "(3,2,2,2)\n" +
                "(0,3,1,3)\n" +
                "(2,0,4,2)\n" +
                "(2,5,2,1)";
        int expectedNodeCount = 10;
        NavigationState navigationState = NavigationState.fromString(input);
        AStar aStar = new AStar(navigationState, Integer.MAX_VALUE);
        SearchAlgorithmResult result = aStar.search();
        assertThat(result.getStatus(), is(SearchAlgorithmResult.Status.SUCCEEDED));
        int nodeCount = 1;
        Node currentNode = result.getFinalNode();
        while (currentNode.getParent() != null) {
            nodeCount++;
            currentNode = currentNode.getParent();
        }
        assertThat(nodeCount, is(expectedNodeCount));
    }

    @Test
    public void aStarNavigationTest_scenario1() {
        String input = "(10, 10) (0, 0) (9, 9) (2, 3, 5, 5) (8, 8, 2, 1)";

        int expectedNodeCount = 19;
        NavigationState navigationState = NavigationState.fromString(input);
        AStar aStar = new AStar(navigationState, Integer.MAX_VALUE);
        SearchAlgorithmResult result = aStar.search();
        assertThat(result.getStatus(), is(SearchAlgorithmResult.Status.SUCCEEDED));
        int nodeCount = 1;
        Node currentNode = result.getFinalNode();
        while (currentNode.getParent() != null) {
            nodeCount++;
            currentNode = currentNode.getParent();
        }
        assertThat(nodeCount, is(expectedNodeCount));
    }

    @Test
    public void aStarNavigationTest_scenario2() {
        String input = "(20, 20) (19, 3) (2, 18) (5, 5, 10, 10) (1, 2, 4, 1)";

        int expectedNodeCount = 33;
        NavigationState navigationState = NavigationState.fromString(input);
        AStar aStar = new AStar(navigationState, Integer.MAX_VALUE);
        SearchAlgorithmResult result = aStar.search();
        assertThat(result.getStatus(), is(SearchAlgorithmResult.Status.SUCCEEDED));
        int nodeCount = 1;
        Node currentNode = result.getFinalNode();
        while (currentNode.getParent() != null) {
            nodeCount++;
            currentNode = currentNode.getParent();
        }
        assertThat(nodeCount, is(expectedNodeCount));
    }

}
