package operators.physical;

import net.sf.jsqlparser.expression.Expression;
import util.Tuple;
import visitors.JoinExpVisitor;

import java.util.ArrayList;

/**
 * Created by s on 2/23/19
 **/
public class TNLJOperator extends JoinOperator{
    public JoinExpVisitor joinExpVisitor;
    public Tuple curLeft;
    public Tuple curRight;
    public Expression expression;

    public TNLJOperator(Operator left, Operator right, Expression expression) {
        super(left, right, expression);
        curLeft = left.getNextTuple();
        curRight = right.getNextTuple();
        schema = new ArrayList<>();
    }

    @Override
    public void next() {
        if(curLeft == null) {
            return;
        }
        if(curRight != null) {
            curRight = right.getNextTuple();
        }

        if(curRight == null) {
            curLeft = left.getNextTuple();
            right.reset();
            curRight = right.getNextTuple();
        }
    }

//    private Tuple joinTuple(Tuple tuple1, Tuple tuple2) {
//        int[] cols = new int[tuple1.length() + tuple2.length()];
//        int i = 0;
//
//        for(int col : tuple1.cols)
//            cols[i++] = col;
//        for(int col : tuple2.cols)
//            cols[i++] = col;
//
//        return new Tuple(cols);
//    }

}
