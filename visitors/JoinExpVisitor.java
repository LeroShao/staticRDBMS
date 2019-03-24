package visitors;

import net.sf.jsqlparser.schema.Column;
import util.Helper;
import util.Tuple;

import java.util.List;

/**
 * join expression visitor, stores 2 tuples, and 2 corresponding schemas
 * Created by s on 2/20/19
 **/
public class JoinExpVisitor extends ConcreteExpVisitor{
    private Tuple tuple1, tuple2;
    private List<String> schema1, schema2;

    public JoinExpVisitor(List<String> schema1, List<String> schema2) {
            this.schema1 = schema1;
            this.schema2 = schema2;
    }

    public void setTuple(Tuple tuple1, Tuple tuple2) {
        this.tuple1 = tuple1;
        this.tuple2 = tuple2;
    }

    @Override
    public void visit(Column arg0) {
        Long val = Helper.getAttrVal(tuple1, arg0.toString(), schema1);
        if(val == null)
            val = Helper.getAttrVal(tuple2, arg0.toString(), schema2);
        curNumericVal = val;
    }
}
