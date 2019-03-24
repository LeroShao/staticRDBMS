package util;

import net.sf.jsqlparser.statement.Statement;
import operators.logic.*;
import operators.physical.Operator;
import visitors.PhysicalPlanBuilder;

import java.util.ArrayList;

/**
 * parse the select statement, build operator tree
 * Created by s on 2/24/19
 **/
public class TreeBuilder {
    public Operator root;
    public QueryParser qp;

    public TreeBuilder(Statement stmt) {
        qp = new QueryParser(stmt);

        LogicOperator curNode = new LogicScanOperator(qp.getTable(0));
        if(qp.getSelectCond(0) != null) {
            curNode = new LogicSelectOperator(curNode, qp.getSelectCond(0));
        }
        for (int i = 1; i < qp.froms.size(); i++) {
            LogicOperator newOp = new LogicScanOperator(qp.getTable(i));
            if(qp.getSelectCond(i) != null) {
                newOp = new LogicSelectOperator(newOp, qp.getSelectCond(i));
            }
            curNode = new LogicJoinOperator(curNode, newOp, qp.getJoinCond(i));
        }
        if(qp.selectItems != null)
            curNode = new LogicProjectOperator(curNode, qp.selectItems);
        if(qp.orderByElements != null)
            curNode = new LogicSortOperator(curNode, qp.orderByElements);
        if(qp.distinct != null) {
            if(qp.orderByElements == null)
                curNode = new LogicSortOperator(curNode, new ArrayList<>());

            curNode = new LogicDuplicateEliminationOperator(curNode);
        }

        PhysicalPlanBuilder ppb = new PhysicalPlanBuilder();
        curNode.accept(ppb);
        root = ppb.getPhysicalOperator();
    }
}
