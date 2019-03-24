package nio;

import util.Tuple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by s on 3/11/19
 **/
public class BinaryTupleWriter implements TupleWriter {
    private File file;
    private FileChannel fc;
    private ByteBuffer buffer;
    private static final int Buffer_Size = 4096;
    private static final int Col_Len = 4;
    private int numOfTuples;
    private int numOfAttr;
    private int tupleNumLimit;
    private boolean firstWrite;

    public BinaryTupleWriter(String filePath) throws FileNotFoundException {
        file =  new File(filePath);
        fc = new FileOutputStream(file).getChannel();
        buffer = buffer.allocate(Buffer_Size);
        firstWrite = true;
        numOfTuples = 0;
    }
    /**
     *
     * @param tuple
     * @throws IOException
     */
    @Override
    public void write(Tuple tuple) throws IOException {
        if(firstWrite) {
            numOfAttr = tuple.length();
            tupleNumLimit = (Buffer_Size - 8)/(numOfAttr * Col_Len);
            buffer.putInt(numOfAttr);
            // default # of tuple number, will be updated when close the ByteBuffer
            buffer.putInt(0);
            firstWrite = false;
        }
        if(numOfTuples < tupleNumLimit) {
            for(int i = 0; i < tuple.length(); i++) {
                buffer.putInt(tuple.get(i));
            }
            numOfTuples++;
        }
        else {
            // pad the remaining of the buffer w/ zeroes
            pad(buffer);
            // update numOfTuples int the end
            buffer.putInt(4, numOfTuples);
            buffer.clear();
            fc.write(buffer);

            // reallocate a new buffer
            firstWrite = true;
            numOfTuples = 0;
            buffer.clear();
            buffer.put(new byte[Buffer_Size]);
            buffer.clear();
            write(tuple);
        }
    }

    @Override
    public void close() throws IOException {
        pad(buffer);
        buffer.putInt(4, numOfTuples);
        buffer.clear();
        fc.write(buffer);
        fc.close();
    }

    public void pad(ByteBuffer buffer) {
        while (buffer.hasRemaining())
            buffer.putInt(0);
    }
}
