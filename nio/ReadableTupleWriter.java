package nio;

import util.Tuple;

import java.io.*;

/**
 * Created by s on 3/11/19
 **/
public class ReadableTupleWriter implements TupleWriter {
    private File file;
    private BufferedWriter bw;

    public ReadableTupleWriter(File file) throws IOException {
        this.file = file;
        bw = new BufferedWriter(new FileWriter(this.file));
    }

    public ReadableTupleWriter(String fileName) throws IOException {
        this(new File(fileName));
    }

    @Override
    public void write(Tuple tuple) throws IOException {
        bw.write(tuple.toString());
        bw.newLine();
    }

    @Override
    public void close() throws IOException {
        bw.close();
    }
}
