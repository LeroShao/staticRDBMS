package operators.physical;

import java.util.List;

/**
 * Created by s on 2/20/19
 **/
public abstract class UnaryOperator extends Operator {
    public Operator child;

    public UnaryOperator(Operator child) {
        this.child = child;
    }


    @Override
    public void reset() {
        child.reset();
    }

    @Override
    public List<String> schema() {
        if(this.schema != null) return schema;
        return child.schema();
    }
}
