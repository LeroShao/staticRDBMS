package operators.physical;

import util.Tuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by s on 2/26/19
 **/
public class InMemSortOperator extends SortOperator {
    List<Tuple> tuples;
    private long curIdx;

    public InMemSortOperator(Operator child, List<?> orders) {
        super(child, orders);
        tuples = new ArrayList<>();

        Tuple tp;
        while ((tp = child.getNextTuple()) != null) {
            tuples.add(tp);
        }

        Collections.sort(tuples, tupleComp);

    }



    /*
    * since whole table is buffered in memory
    * we can keep track of the next index to be read
    * */
    @Override
    public Tuple getNextTuple() {
        if(curIdx >= tuples.size())
            return null;
        return tuples.get((int)curIdx++);
    }

    @Override
    public void reset() {
        curIdx = 0;
    }

    @Override
    public void reset(long index) {
        if(index < 0 || index > tuples.size())
            return;
        curIdx = index;
    }
}
