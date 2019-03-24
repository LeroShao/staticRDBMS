package operators.logic;

/**
 * Created by s on 3/10/19
 **/
public abstract class LogicUnaryOperator extends LogicOperator {
    public LogicOperator child;

    public LogicUnaryOperator(LogicOperator child) {
        this.child = child;
    }
}
