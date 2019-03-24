package operators.physical;

import util.Tuple;

/**
 * Created by s on 2/28/19
 **/
public class DuplicateEliminationOperator extends UnaryOperator {
    Tuple last = null;

    @Override
    public Tuple getNextTuple() {
        if (last == null) {
            last = child.getNextTuple();
            return last;
        } else {
            Tuple cur = null;
            while ((cur = child.getNextTuple()) != null)
                if (!cur.equals(last))
                    break;
            last = cur;
            return cur;
        }
    }

    public DuplicateEliminationOperator(Operator sortOperator) {
        super(sortOperator);
    }
}
