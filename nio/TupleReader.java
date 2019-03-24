package nio;

import util.Tuple;

import java.io.IOException;

/**
 * a reader that reads tuple from a database table file
 * will implement TupleReader interface
 * Created by s on 3/11/19
 **/
public interface TupleReader {

    /* get the index of the tuple
    * */
    public Long getIndex() throws IOException;

    /*
    * read the next tuple from the table
    * */
    public Tuple read() throws IOException;

    /* reset the reader to the specified index
    * */
    public void reset(Long index) throws IOException;

    /*
    * reset the reader to the beginning
    * */
    public void reset() throws IOException;

    /*
    * close the reader
    * */
    public void close() throws IOException;
}
