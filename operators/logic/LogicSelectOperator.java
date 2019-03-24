package operators.logic;

import net.sf.jsqlparser.expression.Expression;
import visitors.PhysicalPlanBuilder;

/**
 * Created by s on 3/11/19
 **/
public class LogicSelectOperator extends LogicUnaryOperator {
    public Expression expression;

    public LogicSelectOperator(LogicOperator child, Expression expression) {
        super(child);
        this.child = child;
        this.expression = expression;
    }

    @Override
    public void accept(PhysicalPlanBuilder ppb) {
        ppb.visit(this);
    }
}
