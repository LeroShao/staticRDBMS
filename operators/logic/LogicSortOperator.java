package operators.logic;

import net.sf.jsqlparser.statement.select.OrderByElement;
import visitors.PhysicalPlanBuilder;

import java.util.List;

/**
 * Created by s on 3/11/19
 **/
public class LogicSortOperator extends LogicUnaryOperator {
    public List<OrderByElement> orders;

    public LogicSortOperator(LogicOperator child, List<OrderByElement> orders) {
        super(child);
        this.orders = orders;
    }

    @Override
    public void accept(PhysicalPlanBuilder ppb) {
        ppb.visit(this);
    }
}
