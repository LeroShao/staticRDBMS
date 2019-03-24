package operators.physical;

import net.sf.jsqlparser.expression.Expression;
import util.Helper;
import util.Tuple;
import visitors.JoinExpVisitor;

import java.util.Comparator;
import java.util.List;

/**
 * Created by s on 3/13/19
 **/
public class SMJOperator extends JoinOperator{
    List<Integer> lOrders;
    List<Integer> rOrders;
    TupleComp tupleComp;
    int curRightIdx;
    int partitionIdx;

    public SMJOperator(Operator left, Operator right, Expression expression, List<Integer> lOrders, List<Integer> rOrders) {
        super(left, right, expression);
        this.lOrders = lOrders;
        this.rOrders = rOrders;
        tupleComp = new TupleComp(lOrders, rOrders);
        this.jv = new JoinExpVisitor(left.schema(), right.schema());
        curLeft = left.getNextTuple();
        curRight = right.getNextTuple();
    }

    @Override
    protected void next() {
        throw new UnsupportedOperationException("Operation not supported");
    }

    public class TupleComp implements Comparator<Tuple> {
        List<Integer> lOrders;
        List<Integer> rOrders;

        public TupleComp(List<Integer> lOrders, List<Integer> rOrders) {
            this.lOrders = lOrders;
            this.rOrders = rOrders;
        }
        @Override
        public int compare(Tuple o1, Tuple o2) {
            for (int i = 0; i < lOrders.size(); i++) {
                int lVal = o1.cols[lOrders.get(i)];
                int rVal = o2.cols[rOrders.get(i)];
                int cmp = Integer.compare(lVal, rVal);
                if(cmp != 0) return cmp;
            }
            return 0;
        }
    }

    @Override
    public Tuple getNextTuple() {
//        System.out.println("from getNextTuple method of SMJ");

        while (curLeft != null && curRight != null) {
            if(tupleComp.compare(curLeft, curRight) < 0) {
                curLeft = left.getNextTuple();
                continue;
            }
            if(tupleComp.compare(curLeft, curRight) > 0) {
                curRight = right.getNextTuple();
                curRightIdx++;
                partitionIdx = curRightIdx;
                continue;
            }

            Tuple res = null;
            if(expression == null ||
                    Helper.getJoinRes(curLeft, curRight, expression, jv))
                res = joinTp(curLeft, curRight);

            curRight = right.getNextTuple();
            curRightIdx++;
            if(curRight == null) {
                curLeft = left.getNextTuple();
                ((SortOperator)right).reset(partitionIdx);
                curRight = right.getNextTuple();
                curRightIdx = partitionIdx;
            }

            if(res != null) return res;
        }

        return null;
    }
}
