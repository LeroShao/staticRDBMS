package operators.physical;

import net.sf.jsqlparser.expression.Expression;
import util.Helper;
import util.Tuple;
import visitors.JoinExpVisitor;


/**
 * Created by s on 3/11/19
 **/
public abstract class JoinOperator extends BinaryOperator {
    protected Tuple curLeft, curRight;
    protected Expression expression;
    protected JoinExpVisitor jv;

    public JoinOperator(Operator left, Operator right, Expression expression) {
        super(left, right);
        this.expression = expression;
        this.jv = new JoinExpVisitor(left.schema(), right.schema());
    }

    @Override
    public Tuple getNextTuple() {
        Tuple res = null;
        while(curLeft != null && curRight != null) {
            if(expression == null
                    || Helper.getJoinRes(curLeft, curRight, expression, jv))
                res = joinTp(curLeft, curRight);
            next();
            if(res != null) return res;
        }
        return null;
    }

    protected abstract void next();

    public Tuple joinTp(Tuple tuple1, Tuple tuple2) {
        int[] cols = new int[tuple1.length() + tuple2.length()];
        int i = 0;
        for (int val : tuple1.cols)
            cols[i++] = val;
        for (int val : tuple2.cols)
            cols[i++] = val;

        return new Tuple(cols);
    }
}
