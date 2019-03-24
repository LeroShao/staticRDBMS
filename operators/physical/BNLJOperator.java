package operators.physical;

import net.sf.jsqlparser.expression.Expression;
import util.Catalog;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s on 3/13/19
 **/
public class BNLJOperator extends JoinOperator{
    List<Tuple> outerBlk;
    private int tpsPerBlk;
    private int curOuterIdx;


    public BNLJOperator(Operator left, Operator right, Expression expression) {
        super(left, right, expression);

        int tupleSize = Catalog.ATTRSIZE * left.schema().size();
        tpsPerBlk = Catalog.joinBuffPgs * (Catalog.PAGESIZE / tupleSize);
        outerBlk = new ArrayList<>(tpsPerBlk);
        curOuterIdx = 0;

        next();
    }

    @Override
    protected void next() {
        if(curRight != null) {
            setOuterIdx(++curOuterIdx);
            if(curLeft != null)
                return;

            curRight = right.getNextTuple();
            if(curRight != null){
                setOuterIdx(0);
                return;
            }
        }

        readIntoBlk();
        right.reset();
        curRight = right.getNextTuple();
    }

    private void setOuterIdx(int idx) {
        curOuterIdx = idx;
        curLeft = curOuterIdx < outerBlk.size() ? outerBlk.get(curOuterIdx) : null;
    }

    private void readIntoBlk() {
        outerBlk.clear();
        int count = 0;
        Tuple tp;
        while (count++ < tpsPerBlk && (tp = left.getNextTuple()) != null) {
            outerBlk.add(tp);
        }
        setOuterIdx(0);
    }
}
