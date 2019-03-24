package nio;

import util.Tuple;

import java.io.*;

/**
 * reads tuples from a file which contains normal characters
 * representing the tuples
 *
 * main purpose of the class is debugging utilities
 *
 * ReadableTupleReader uses Java's standard I/O,
 * NIO is not used,
 * operations on pages are not supported
 *
 * input file format: every file contains zero or more tuples
 * a tuple is a line in the file with column values separated by commas
 * e.g., 9,20,31
 *
 * Created by s on 3/11/19
 **/
public class ReadableTupleReader implements TupleReader {
    private File file;
    private BufferedReader br;

    public ReadableTupleReader(File file) throws FileNotFoundException {
        this.file = file;
        this.br = new BufferedReader(new FileReader(this.file));
    }

    public ReadableTupleReader(String fileName) throws FileNotFoundException {
        this(new File(fileName));
    }

    @Override
    public Long getIndex() throws IOException {
        throw new UnsupportedOperationException("operation not supported int normal tuple reader");
    }

    @Override
    public Tuple read() throws IOException {
        String line = br.readLine();
        if(line == null) return null;
        String[] columns = line.split(",");
        int len = columns.length;
        int[] cols = new int[len];
        for(int i = 0; i < len; i++) {
            cols[i] = Integer.valueOf(columns[i]);
        }
        return new Tuple(cols);
    }

    @Override
    public void reset(Long index) throws IOException {
        throw new UnsupportedOperationException("operation not supported int normal tuple reader");
    }

    @Override
    public void reset() throws IOException {
        if(br != null)
            br.close();
        br = new BufferedReader(new FileReader(file));
    }

    @Override
    public void close() throws IOException {
        br.close();
    }
}
