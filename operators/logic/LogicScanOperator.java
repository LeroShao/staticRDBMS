package operators.logic;

import util.Table;
import visitors.PhysicalPlanBuilder;

/**
 * Created by s on 3/11/19
 **/
public class LogicScanOperator extends LogicOperator{
    public Table table;

    public LogicScanOperator(Table table) {
        this.table = table;
    }

    @Override
    public void accept(PhysicalPlanBuilder ppb) {
        ppb.visit(this);
    }
}
