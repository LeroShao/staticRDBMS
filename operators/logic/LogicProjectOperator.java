package operators.logic;

import net.sf.jsqlparser.statement.select.SelectItem;
import visitors.PhysicalPlanBuilder;

import java.util.List;

/**
 * Created by s on 3/11/19
 **/
public class LogicProjectOperator extends LogicUnaryOperator {
    public List<SelectItem> selectItems;

    public LogicProjectOperator(LogicOperator lop, List<SelectItem> selectItems) {
        super(lop);
        this.selectItems = selectItems;
    }

    @Override
    public void accept(PhysicalPlanBuilder ppb) {
        ppb.visit(this);
    }
}
