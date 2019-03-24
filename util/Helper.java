package util;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.FromItem;
import visitors.JoinExpVisitor;
import visitors.SelExpVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by s on 2/20/19
 **/
public class Helper {
    /**
     * Obtain the selection result.
     * @param tp the tuple to be examined
     * @param exp the selection expression
     * @param selExpVisitor the selection condition visitor
     * @return the result (true / false)
     */
    public static boolean getSelRes(Tuple tp, Expression exp, SelExpVisitor selExpVisitor) {
        selExpVisitor.setTuple(tp);
        exp.accept(selExpVisitor);
        return selExpVisitor.getFinalCondition();
    }

    public static boolean getJoinRes(Tuple tp1, Tuple tp2, Expression exp, JoinExpVisitor jv) {
        jv.setTuple(tp1, tp2);
        exp.accept(jv);
        return jv.getFinalCondition();
    }

    public static String getColName(String tabCol) {
        return tabCol.split("\\.")[1];
    }

    public static int getAttrIdx(String attr, List<String> schema) {
        int idx = schema.indexOf(attr);
        if (idx != -1) return idx;

        for(int i = 0; i < schema.size(); i++) {
            String colName = getColName(schema.get(i));
            if(colName.equals(attr))
                return  i;
        }

        return -1;
    }

    public static Long getAttrVal(Tuple tp, String attr, List<String> schema) {
        int idx = getAttrIdx(attr, schema);
        if (idx != -1) return (long) tp.get(idx);
        return null;
    }

    public static String getOrigName(FromItem fromItem) {
        return fromItem.toString().split(" ")[0];
    }

    /*
    * decompose AND expression recursively into a list of binary expressions
    * */
    public static List<Expression> decomposeAnds(Expression expression) {
        List<Expression> ands = new ArrayList<>();
        while(expression instanceof AndExpression) {
            AndExpression and = (AndExpression) expression;
            ands.add(and.getRightExpression());
            expression = and.getLeftExpression();
        }
        ands.add(expression);
        Collections.reverse(ands);
        return ands;
    }

    /*
    *   extract the tables mentioned in a binary expression
    * */
    public static List<String> getExpTabs(Expression exp) {
        List<String> ret = new ArrayList<>();
        if (!(exp instanceof BinaryExpression))
            return ret;

        BinaryExpression be = (BinaryExpression) exp;
        Expression left = be.getLeftExpression();
        Expression right = be.getRightExpression();

        Column col;
        if (left instanceof Column) {
            col = (Column) left;
            if (col.getTable() == null) return null;
            ret.add(col.getTable().toString());
        }
        if (right instanceof Column) {
            col = (Column) right;
            if (col.getTable() == null) return null;
            ret.add(col.getTable().toString());
        }

        if (ret.size() == 2 && ret.get(0).equals(ret.get(1)))
            ret.remove(1);

        return ret;
    }

    public static Expression genAnds(List<Expression> expressions) {
        if(expressions.isEmpty()) return null;
        Expression res = expressions.get(0);
        for(int i = 1; i < expressions.size(); i++) {
            res = new AndExpression(res, expressions.get(i));
        }
        return res;
    }

    public static Expression processJoin(Expression expression,
                                         List<String> outerSchema, List<String> innerSchema,
                                         List<Integer> outerIndexes, List<Integer> innerIndexes) {
        outerIndexes.clear();
        innerIndexes.clear();
        if(expression == null) return null;

        List<Expression> expressions = decomposeAnds(expression);
        List<Expression> equals = new ArrayList<>();
        List<Expression> tmp = new ArrayList<>();

        for (Expression exp : expressions) {
            if(!(exp instanceof EqualsTo)) {
                tmp.add(exp);
                continue;
            }

            EqualsTo e = (EqualsTo) expression;
            String leftCol = e.getLeftExpression().toString();
            String rightCol = e.getRightExpression().toString();

            boolean outerHasL = outerSchema.contains(leftCol),
                    outerHasR = outerSchema.contains(rightCol),
                    innerHasL = innerSchema.contains(leftCol),
                    innerHasR = innerSchema.contains(rightCol);

            if(!(outerHasR && innerHasL || outerHasL && innerHasR)) {
                tmp.add(e);
                continue;
            }

            if(outerHasR) {
                e = new EqualsTo(e.getRightExpression(), e.getLeftExpression());
                String temp = leftCol;
                leftCol = rightCol;
                rightCol = temp;
            }

            int outerIdx = getAttrIdx(leftCol, outerSchema);
            int innerIdx = getAttrIdx(rightCol, innerSchema);
            outerIndexes.add(outerIdx);
            innerIndexes.add(innerIdx);
            equals.add(e);
        }
        equals.addAll(tmp);
        return genAnds(equals);
    }
}
