package operators.physical;

import net.sf.jsqlparser.statement.select.OrderByElement;
import util.Helper;
import util.Tuple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

/**
 * Created by s on 3/11/19
 **/
public abstract class SortOperator extends UnaryOperator {

    List<Integer> orders;
    TupleComp tupleComp;

    public SortOperator(Operator child, List<?> orders) {
        super(child);
        this.orders = new ArrayList<>();
        if (!orders.isEmpty()) {
//            System.out.println("orders not empty");
//            for (Object i : orders)
//                System.out.println(i);
            if (orders.get(0) instanceof OrderByElement) {
                for (Object obj : orders) {
                    OrderByElement orderByElement = (OrderByElement) obj;
                    int order = Helper.getAttrIdx(orderByElement.toString(), child.schema());
                    this.orders.add(order);
                }
            }
            else if(orders.get(0) instanceof Integer) {
//                for (Integer i : this.orders)
//                    System.out.println(i);
                this.orders = (List<Integer>) orders;
            }
        }

        tupleComp = new TupleComp(this.orders);
    }

    public abstract void reset(long index);

    public class TupleComp implements Comparator<Tuple> {
        List<Integer> orders;
        HashSet<Integer> set;

        public TupleComp(List<Integer> orders) {
            this.orders = orders;
            set = new HashSet<>(orders);
        }

        @Override
        public int compare(Tuple tp1, Tuple tp2) {
            for (int i : orders) {
                int cmp = tp1.get(i) - tp2.get(i);
                if (cmp != 0) return cmp;
            }

            for (int i = 0; i < tp1.length(); i++) {
                if (set.contains(i)) continue;
                int cmp = tp1.get(i) - tp2.get(i);
                if (cmp != 0) return cmp;
            }
            return 0;
        }
    }
}
