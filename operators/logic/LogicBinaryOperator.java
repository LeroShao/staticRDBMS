package operators.logic;

/**
 * Created by s on 3/11/19
 **/
public abstract class LogicBinaryOperator extends LogicOperator {
    public LogicOperator left;
    public LogicOperator right;

    public LogicBinaryOperator(LogicOperator left, LogicOperator right) {
        this.left = left;
        this.right = right;
    }
}
