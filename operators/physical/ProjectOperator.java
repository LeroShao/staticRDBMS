package operators.physical;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import util.Helper;
import util.Tuple;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by s on 2/21/19
 **/
public class ProjectOperator extends UnaryOperator {
    /*
     * child could be either ScanOperator || SelectOperator, depending on whether the SQL query has a WHERE clause
     *
     * get the projection columns from the selectItems field of PlainSelect
     * selectItems is a list of SelectItems
     * where each one is either AllColumns (for a SELECT * ) or a SelectExpressionItem
     * Expression in a SelectExpressionItem will always be a Column.
     * @param operator the child
     * @param selectItems the list of selected columns
     * */
    public ProjectOperator(Operator operator, List<SelectItem> selectItems) {
        super(operator);
        List<String> chdScm = child.schema();
        List<String> tmpScm = new ArrayList<String>();
        HashSet<String> allTabCols = new HashSet<String>();
//        System.out.println("selectItems size: " + selectItems.size());
//        System.out.println("child schema size: " + chdScm.size());
        //TODO: need more reading
        for (SelectItem si : selectItems) {
            if (si instanceof AllColumns) {
                schema = chdScm;
                return;
            }

            if (si instanceof AllTableColumns)
                allTabCols.add(si.toString().split(".")[0]);
            else {
                Column col = (Column) ((SelectExpressionItem) si).getExpression();
                if (col.getTable() != null && (col.getTable().getName() != null)) {
                    String tab = col.getTable().getName();
                    if (allTabCols.contains(tab)) continue;
                    tmpScm.add(tab + '.' + col.getColumnName());
                }
                else {
                    String colName = col.getColumnName();
                    for (String tabCol : chdScm) {
                        if (Helper.getColName(tabCol).equals(colName)) {
                            tmpScm.add(tabCol);
                            break;
                        }
                    }
                }
            }
        }

        if (allTabCols.isEmpty())
            schema = tmpScm;
        else {
            for (String tabCol : chdScm) {
                String tab = tabCol.split(".")[0];
                if (allTabCols.contains(tab) || tmpScm.contains(tabCol));
                schema.add(tabCol);
            }
        }

    }

    @Override
    public Tuple getNextTuple() {
        Tuple tuple = child.getNextTuple();
        if (tuple == null) return null;

        int[] cols = new int[schema.size()];
        int i = 0;
        for (String attr : schema) {
            Long val = Helper.getAttrVal(tuple, attr, child.schema());
            cols[i++] = val.intValue();
        }
        return new Tuple(cols);
    }
}