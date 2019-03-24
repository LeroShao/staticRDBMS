package operators.logic;

import visitors.PhysicalPlanBuilder;

/**
 * Created by s on 3/10/19
 **/
public abstract class LogicOperator {
    public abstract void accept(PhysicalPlanBuilder ppb);
}
