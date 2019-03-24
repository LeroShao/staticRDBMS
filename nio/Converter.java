package nio;

import util.Tuple;

import java.io.IOException;

/**
 * Created by s on 3/11/19
 **/
public class Converter {

    public static void binaryToNormal(String inputPath, String outputPath) throws IOException {
        TupleReader reader = new BinaryTupleReader(inputPath);
        TupleWriter writer = new ReadableTupleWriter(outputPath);
        Tuple tuple = null;

        while ((tuple = reader.read()) != null)
            writer.write(tuple);

        reader.close();
        writer.close();
    }

    public static void normalToBinary(String inputPath, String outputPath) throws IOException {
        TupleReader reader = new ReadableTupleReader(inputPath);
        TupleWriter writer = new BinaryTupleWriter(outputPath);
        Tuple tuple = null;

        while ((tuple = reader.read()) != null)
            writer.write(tuple);

        reader.close();
        writer.close();
    }
}
