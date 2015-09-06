import org.junit.Ignore;
import org.junit.Test;

import java.util.Random;

/**
 * Created by erpa_ on 9/3/2015.
 */
public class Generator {
    @Ignore
    @Test
    public void printGeneratedBoard() {
        System.out.println(generateBoard(499, 499, 5000));
    }
    public static String generateBoard(int xMax, int yMax, int obstacleCount) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < obstacleCount; i++) {
            int x = random.nextInt(xMax - 5);
            int y = random.nextInt(yMax - 5);
            builder.append('(')
                    .append(x)
                    .append(',')
                    .append(y)
                    .append(',')
                    .append(random.nextInt(5) + 1)
                    .append(',')
                    .append(random.nextInt(5) + 1)
                    .append(')');
        }
        return builder.toString();
    }
}
