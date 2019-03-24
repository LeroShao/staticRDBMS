package nio;

import util.Tuple;

import java.io.IOException;

/**
 * a writer that writes tuple into a database table file
 * will implement TupleWriter interface
 * Created by s on 3/11/19
 **/
public interface TupleWriter {

    /*
    * write the specified tuple to byteBuffer
    *
    * */
    public void write(Tuple tuple) throws IOException;

    /*
    * close the writer
    *
    * */
    public void close() throws IOException;

}
