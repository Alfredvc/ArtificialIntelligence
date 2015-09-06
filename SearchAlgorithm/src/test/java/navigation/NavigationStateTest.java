package navigation;

import navigation.NavigationState;
import org.junit.Test;
import search_algorithm.State;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsIn.isIn;
import static org.junit.Assert.*;


/**
 * Created by erpa_ on 8/27/2015.
 */
public class NavigationStateTest {
    @Test
    public void generateSuccessorTest_1(){
        boolean[][] obstacles = {{false, false},
                                {false, false}};
        NavigationState navigationState = new NavigationState(new Point(0,0), new Point(1,1), 2, 2, obstacles);
        List<State> states = navigationState.generateSuccessors();
        assertThat(states.size(), is(2));
        assertThat(((NavigationState) states.get(0)).getLocation().x, is(1));
        assertThat(((NavigationState) states.get(0)).getLocation().y, is(0));

        assertThat(((NavigationState) states.get(1)).getLocation().x, is(0));
        assertThat(((NavigationState) states.get(1)).getLocation().y, is(1));
    }

    @Test
    public void generateSuccessorTest_2(){
        boolean[][] obstacles = {{false, false, false},
                                {false, false, false},
                                {false, false, false}};
        NavigationState navigationState = new NavigationState(new Point(1,1), new Point(1,1), 3, 3, obstacles);
        List<State> states = navigationState.generateSuccessors();
        assertThat(states.size(), is(4));
        assertThat(((NavigationState) states.get(0)).getLocation().x, is(2));
        assertThat(((NavigationState) states.get(0)).getLocation().y, is(1));

        assertThat(((NavigationState) states.get(1)).getLocation().x, is(1));
        assertThat(((NavigationState) states.get(1)).getLocation().y, is(2));

        assertThat(((NavigationState) states.get(2)).getLocation().x, is(0));
        assertThat(((NavigationState) states.get(2)).getLocation().y, is(1));

        assertThat(((NavigationState) states.get(3)).getLocation().x, is(1));
        assertThat(((NavigationState) states.get(3)).getLocation().y, is(0));
    }

    @Test
    public void generateSuccessorTest_3(){
        boolean[][] obstacles = {{false, false, false},
                                {false, false, true},
                                {false, true, false}};

        Point state1 = new Point(0, 1);
        Point state2 = new Point(1, 0);

        NavigationState navigationState = new NavigationState(new Point(1,1), new Point(1,1), 3, 3, obstacles);
        List<State> states = navigationState.generateSuccessors();
        assertThat(states.size(), is(2));
        assertThat(((NavigationState) states.get(0)).getLocation(), is(state1));
        assertThat(((NavigationState) states.get(1)).getLocation(), is(state2));
    }

    @Test
    public void generateFromTextTest(){
        String input = "(6,6)\n" +
                "(1,0) (5,5)\n" +
                "(3,2,2,2)\n" +
                "(0,3,1,3)\n" +
                "(2,0,4,2)\n" +
                "(2,5,2,1)";
        int xSize = 6;
        int ySize = 6;
        Point start = new Point(1,0);
        Point goal = new Point(5,5);
        int expectedObstacleCount = 17;
        NavigationState navigationState = NavigationState.fromString(input);
        assertThat(navigationState.getxSize(), is(xSize));
        assertThat(navigationState.getySize(), is(ySize));
        assertThat(navigationState.getLocation(), is(start));
        assertThat(navigationState.getGoal(), is(goal));
        int obstacleCount = 0;
        for (boolean[] row : navigationState.getObstacles()) {
            for (boolean cell : row) {
                if (cell)  {
                    obstacleCount++;
                }
            }
        }
        assertThat(obstacleCount, is(expectedObstacleCount));
    }

    @Test
    public void generateSuccessorsTest_4(){
        List<Point> expectedSuccessors = Arrays.asList(
            new Point(0,0),  new Point(1,1)
        );
        String input = "(6,6)\n" +
                "(1,0) (5,5)\n" +
                "(3,2,2,2)\n" +
                "(0,3,1,3)\n" +
                "(2,0,4,2)\n" +
                "(2,5,2,1)";
        NavigationState navigationState = NavigationState.fromString(input);
        List<State> successors = navigationState.generateSuccessors();
        assertThat(successors.size(), is(2));
        assertThat(((NavigationState)successors.get(0)).getLocation(), isIn(expectedSuccessors));
        assertThat(((NavigationState)successors.get(1)).getLocation(), isIn(expectedSuccessors));
    }

    @Test
    public void hashCodeTest(){
        int xySize = 10000;
        int count = 1000000;
        boolean[][] obstacles = {{false}};
        final Random random = new Random();
        HashMap<Integer, NavigationState> navigationStates = new HashMap<>();
        for (int i = 0; i < count; i++) {
            Point toAdd = new Point(random.nextInt(xySize), random.nextInt(xySize));
            NavigationState navigationState = new NavigationState(toAdd, toAdd, 1,1, obstacles);
            if (navigationStates.containsKey(navigationState.hashCode())) {
                NavigationState existing = navigationStates.get(navigationState.hashCode());
                if (!(existing).equals(navigationState)) {
                    fail("State: " + existing + " Hash : " + existing.hashCode() + " - State: " + navigationState + " Hash: " + navigationState.hashCode());
                }
            } else {
                navigationStates.put(navigationState.hashCode(), navigationState);
            }
        }
    }
}
