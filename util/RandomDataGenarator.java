package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

/**
 * Created by s on 3/16/19
 **/
public class RandomDataGenarator {
    public static void gen(String path, int numOfTuples, int numOfAttrs, int range) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(path));
        Random rand = new Random();
        int count = 0;
        while (count < numOfTuples) {
            count++;
            int[] cols = new int[numOfAttrs];
            for (int i = 0; i < cols.length; i++)
                cols[i] = rand.nextInt(range);
            Tuple tp = new Tuple(cols);
            bw.write(tp.toString());
            bw.newLine();
        }
        bw.close();
    }
}
