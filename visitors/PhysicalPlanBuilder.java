package visitors;

import net.sf.jsqlparser.expression.Expression;
import operators.logic.*;
import operators.physical.*;
import util.Catalog;
import util.Helper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s on 3/10/19
 **/
public class PhysicalPlanBuilder {
    private Operator pop;

    public Operator getPhysicalOperator() {
        return pop;
    }

    public void getChild(LogicUnaryOperator lop) {
        pop = null;
        lop.child.accept(this);
    }

    public Operator[] getLR(LogicBinaryOperator lop) {
        Operator[] LR = new Operator[2];

        pop = null;
        lop.left.accept(this);
        LR[0] = pop;

        pop = null;
        lop.right.accept(this);
        LR[1] = pop;

        return LR;
    }

    public void visit(LogicScanOperator lop) {
        pop = new ScanOperator(lop.table);
    }

    public void visit(LogicSelectOperator lop) {
        getChild(lop);

        pop = new SelectOperator((ScanOperator) pop, lop.expression);
    }

    public void visit(LogicProjectOperator lop) {
        getChild(lop);
        pop = new ProjectOperator(pop, lop.selectItems);
    }

    public void visit(LogicSortOperator lop) {
        getChild(lop);
        switch (Catalog.sortMethod) {
            case INMEMSORT:
                pop = new InMemSortOperator(pop, lop.orders);
                break;
            case EXTERNALSORT:
                pop = new ExternalSortOperator(pop, lop.orders);
                break;
        }
    }

    public void visit(LogicDuplicateEliminationOperator lop) {
        getChild(lop);
        pop = new DuplicateEliminationOperator(pop);
    }

    public void visit(LogicJoinOperator lop) {
        Operator[] LR = getLR(lop);
        switch (Catalog.joinMethod) {
            case SMJ:
                List<Integer> outerIdxs = new ArrayList<>();
                List<Integer> innerIdxs = new ArrayList<>();
                Expression processedExp = Helper.processJoin(lop.expression, LR[0].schema(), LR[1].schema(),
                        outerIdxs, innerIdxs);

                if (!outerIdxs.isEmpty()) {
                    lop.expression = processedExp;
                    if (Catalog.sortMethod == Catalog.SortMethod.INMEMSORT) {
                        LR[0] = new InMemSortOperator(LR[0], outerIdxs);
                        LR[1] = new InMemSortOperator(LR[1], innerIdxs);

                    } else {
                        LR[0] = new ExternalSortOperator(LR[0], outerIdxs);
                        LR[1] = new ExternalSortOperator(LR[1], innerIdxs);
                    }
                    pop = new SMJOperator(LR[0], LR[1], lop.expression, outerIdxs, innerIdxs);

                    pop.reset();
                } else {
                    // not equijoin
                    pop = new BNLJOperator(LR[0], LR[1], lop.expression);
                }
                break;
            case BNLJ:
                pop = new BNLJOperator(LR[0], LR[1], lop.expression);
                break;
            case TNLJ:
                pop = new TNLJOperator(LR[0], LR[1], lop.expression);
                break;
        }
    }
}
