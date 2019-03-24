package operators.logic;

import visitors.PhysicalPlanBuilder;

/**
 * Created by s on 3/11/19
 **/
public class LogicDuplicateEliminationOperator extends LogicUnaryOperator{
    public LogicDuplicateEliminationOperator(LogicOperator child) {
        super(child);
    }

    @Override
    public void accept(PhysicalPlanBuilder ppb) {
        ppb.visit(this);
    }
}
