package operators.physical;

import nio.TupleWriter;
import util.Tuple;

import java.util.List;

public abstract class Operator {

    /**
     * return the next tuple or null if no tuple left
     *
     * @return the next tuple
     */
    public abstract Tuple getNextTuple();

    /**
     * reset the operator to the start
     */
    public abstract void reset();

    protected List<String> schema;

    public abstract List<String> schema();

    /**
     * repeatedly calls getNextTuple until the next tuple is null
     * write each tuple to a suitable PrintStream
     *
     * @param tw the print stream
     */
    public void dump(TupleWriter tw) {
        Tuple tp = null;
        while ((tp = getNextTuple()) != null) {
            try {
                tw.write(tp);
            }catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

}
