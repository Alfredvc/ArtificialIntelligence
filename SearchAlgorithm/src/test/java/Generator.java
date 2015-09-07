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
        System.out.println(generateBoard(500, 500, 5000));
    }
    public static String generateBoard(int xSize, int ySize, int obstacleCount) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        builder.append("(")
                .append(xSize)
                .append(",")
                .append(ySize)
                .append(")")
                .append("(0,0)")
                .append("(")
                .append(xSize - 1)
                .append(",")
                .append(ySize - 1)
                .append(")");
        for(int i = 0; i < obstacleCount; i++) {
            int x = random.nextInt(xSize - 5);
            int y = random.nextInt(ySize - 5);
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
