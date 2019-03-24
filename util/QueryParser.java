package util;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by s on 2/24/19
 **/
public class QueryParser {
    public Select select;
    public PlainSelect ps;
    public List<SelectItem> selectItems;
    public FromItem fromItem;
    public Expression where;
    public Distinct distinct;
    public List<Join> joins;
    public List<OrderByElement> orderByElements;

    public List<String> froms;
    public List<Expression> ands;
    public HashMap<String, List<Expression>> selectConds, joinConds;
    public HashMap<String, Expression> fnSelectCond, fnJoinCond;

    public QueryParser(Statement stmt) {
        select = (Select) stmt;
        ps = (PlainSelect) select.getSelectBody();
        selectItems = ps.getSelectItems();
        fromItem = ps.getFromItem();
        where = ps.getWhere();
        distinct = ps.getDistinct();
        joins = ps.getJoins();
        orderByElements = ps.getOrderByElements();
        Catalog.getCatalog();
        Catalog.resetDirs("/Users/lirongshao/Desktop/Interpreter/staticRDBMS/samples/input", "/Users/lirongshao/Desktop/Interpreter/staticRDBMS/samples/output");

        Catalog.aliases.clear();
        /*
        * froms stores table aliases/table names
        * */
        froms = new ArrayList<>();
        if(fromItem.getAlias() != null) {
            Catalog.aliases.put(fromItem.getAlias(), Helper.getOrigName(fromItem));
            froms.add(fromItem.getAlias());
        }
        else
            froms.add(fromItem.toString());

        if(joins != null) {
            for(Join join : joins) {
                FromItem ri = join.getRightItem();
                if(ri.getAlias() != null) {
                    Catalog.aliases.put(ri.getAlias(), Helper.getOrigName(ri));
                    froms.add(ri.getAlias());
                }
                else
                    froms.add(ri.toString());
            }
        }

        selectConds = new HashMap<>();
        joinConds = new HashMap<>();
        for (String tableName : froms) {
            selectConds.put(tableName, new ArrayList<>());
            joinConds.put(tableName, new ArrayList<>());
        }

        /*
        * ands: expression from WHERE clause
        * */
        ands = Helper.decomposeAnds(where);
        for(Expression expression : ands) {
            List<String> tableNames = Helper.getExpTabs(expression);

            int idx = lastIdx(tableNames);

            if(tableNames == null) {
                joinConds.get(froms.get(froms.size() - 1)).add(expression);
            }
            else if(tableNames.size() <= 1) {
                selectConds.get(froms.get(idx)).add(expression);
            }
            else {
                joinConds.get(froms.get(idx)).add(expression);
            }
        }

        fnJoinCond = new HashMap<>();
        fnSelectCond = new HashMap<>();
        for(String tableName : froms) {
            fnJoinCond.put(tableName, Helper.genAnds(joinConds.get(tableName)));
            fnSelectCond.put(tableName, Helper.genAnds(selectConds.get(tableName)));
        }


    }

    public int lastIdx(List<String> tableNames) {
        if(tableNames == null) return froms.size() - 1;
        int idx = 0;
        for(String tableName : tableNames) {
            idx = Math.max(idx, froms.indexOf(tableName));
        }
        return idx;
    }

    public Expression getJoinCond(int idx) {
        return fnJoinCond.get(froms.get(idx));
    }

    public Expression getSelectCond(int idx) {
        return fnSelectCond.get(froms.get(idx));
    }

    public Table getTable(int idx) {
        return Catalog.getTable(froms.get(idx));
    }
}
