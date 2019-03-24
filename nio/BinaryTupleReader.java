package nio;

import util.Tuple;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by s on 3/11/19
 **/
public class BinaryTupleReader implements TupleReader {
    private File file;
    private static final int Buffer_Size = 4096;
    private static final int Col_Len = 4;
    private FileChannel fc;
    private ByteBuffer buffer;
    private int numOfAttr;
    private int numOfTuples; // num of tuples in a page
    private long curTupleIndex;
    private boolean needNewPage;
    private boolean endOfFile;
    private List<Long> indexes; // stores the total # of
                              // tuples buffered till each page

    public BinaryTupleReader(File file) throws IOException {
        this.file = file;
        fc = new FileInputStream(this.file).getChannel();
        buffer = ByteBuffer.allocate(Buffer_Size);
        curTupleIndex = 0;
        needNewPage = true;
        endOfFile = false;
        indexes = new ArrayList<>();
        indexes.add(0l);
    }

    public BinaryTupleReader(String filePath) throws IOException{
        this(new File(filePath));
    }

    @Override
    public Long getIndex() throws IOException {
        return curTupleIndex;
    }

    @Override
    public Tuple read() throws IOException {
        while (!endOfFile) {
            if(needNewPage) {
                try {
                    fetchPage();
                }catch (Exception e) {
                    break;
                }
            }
            // buffer.hasRemaining: Tells whether there are any
            // elements between the current position and the limit
            if(buffer.hasRemaining()) {
                int[] cols = new int[numOfAttr];
                for(int i = 0; i < numOfAttr; i++)
                    cols[i] = buffer.getInt();
                curTupleIndex++;
                return new Tuple(cols);
            }

            // does not have remaining
            erase();
            needNewPage = true;
        }

        return null;
    }


    @Override
    public void reset(Long index) throws IOException, IndexOutOfBoundsException {
        // precondition: the index should not exceed the # of tuples buffered
        if(index > curTupleIndex || index < 0)
            throw new IndexOutOfBoundsException();
        long pageIndex = Collections.binarySearch(indexes, Long.valueOf(index + 1));
        pageIndex = pageIndex < 0 ? - (pageIndex + 1) : pageIndex;
        // The new position, a non-negative integer counting
        // the number of bytes from the beginning of the file
        fc.position((pageIndex - 1) * Buffer_Size);

        erase();
        needNewPage = true;
        endOfFile = false;
        indexes.subList(0, (int)pageIndex);
        curTupleIndex = index;
        long tuplesBuffered = indexes.get(indexes.size() - 1);
        int newPosition = (int)((index - tuplesBuffered) * numOfAttr + 2) * Col_Len;
        try {
            fetchPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        buffer.position(newPosition);
    }

    @Override
    public void reset() throws IOException {
        close();
        fc = new FileInputStream(file).getChannel();
        buffer = ByteBuffer.allocate(Buffer_Size);
        curTupleIndex = 0;
        needNewPage = true;
        endOfFile = false;
        indexes = new ArrayList<>();
        indexes.add(0l);
    }

    @Override
    public void close() throws IOException {
        fc.close();
    }

    private void erase() {

        // Clears this buffer.
        // The position is set to zero,
        // the limit is set to the capacity,
        // and the mark is discarded.
        //
        buffer.clear();
        // fill the buffer w/ 0s
        buffer.put(new byte[Buffer_Size]);
        buffer.clear();
    }

    private void fetchPage() throws IOException{
        endOfFile = (fc.read(buffer) < 0);
        needNewPage = false;

        if(endOfFile) throw new EOFException();

        // Flips this buffer.
        // The limit is set to the current position
        // the position is set to zero.
        // If the mark is defined then it is discarded.
        buffer.flip();
        // metadata
        numOfAttr = buffer.getInt();
        numOfTuples = buffer.getInt();
        indexes.add(indexes.get(indexes.size() - 1) + numOfTuples);

        buffer.limit((numOfAttr * numOfTuples + 2) * Col_Len);
    }
}
