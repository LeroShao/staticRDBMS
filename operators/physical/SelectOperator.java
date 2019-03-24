package operators.physical;

import net.sf.jsqlparser.expression.Expression;
import util.Helper;
import util.Tuple;
import visitors.SelExpVisitor;

/**
 * Created by s on 2/20/19
 **/
public class SelectOperator extends UnaryOperator {
    SelExpVisitor sv;
    Expression exp;

    public SelectOperator(ScanOperator sop, Expression exp) {
        super(sop);
        this.exp = exp;
        sv = new SelExpVisitor(schema());
    }

    @Override
    public Tuple getNextTuple() {
        Tuple tp;
        while ((tp = child.getNextTuple()) != null) {
            if (exp == null || Helper.getSelRes(tp, exp, sv))
                return tp;
                /*  public static boolean getSelRes(Tuple tp, Expression exp, SelExpVisitor selExpVisitor) {
                        selExpVisitor.setTuple(tp);
                        exp.accept(selExpVisitor);
                        // --> selExpVisitor.visit(exp);
                        return selExpVisitor.getFinalCondition();
                }*/

        }
        return null;
    }
}
