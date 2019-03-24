package operators.logic;

import net.sf.jsqlparser.expression.Expression;
import visitors.PhysicalPlanBuilder;

/**
 * Created by s on 3/11/19
 **/
public class LogicJoinOperator extends LogicBinaryOperator {
    public Expression expression;

    public LogicJoinOperator(LogicOperator left, LogicOperator right, Expression expression) {
        super(left, right);
        this.expression = expression;
    }

    @Override
    public void accept(PhysicalPlanBuilder ppb) {
        ppb.visit(this);
    }
}
