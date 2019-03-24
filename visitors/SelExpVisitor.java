package visitors;

import net.sf.jsqlparser.schema.Column;
import util.Helper;
import util.Tuple;

import java.util.List;

/**
 * Created by s on 2/20/19
 **/
public class SelExpVisitor extends ConcreteExpVisitor{
    private Tuple tuple;
    private List<String> schema;

    public SelExpVisitor(List<String> schema) {
        this.schema = schema;
    }

    @Override
    public void visit(Column arg0) {
        curNumericVal = Helper.getAttrVal(tuple, arg0.toString(), schema);
    }

    public void setTuple(Tuple tuple) {
        this.tuple = tuple;
    }
}
