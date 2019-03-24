package visitors;

import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.*;

/**
 * An abstract "concrete" class of the expression visitor
 */
public abstract class ConcreteExpVisitor extends AbstractExpVisitor{

	protected long curNumericVal;
	protected boolean curCondition = false;

	public boolean getFinalCondition() {
		return curCondition;
	}

	@Override
	public void visit(LongValue arg0) {
		curNumericVal = arg0.getValue();
	}

	@Override
	public void visit(EqualsTo arg0) {
		arg0.getLeftExpression().accept(this);
		long leftVal = curNumericVal;
		arg0.getRightExpression().accept(this);
		long rightVal = curNumericVal;

		curCondition = (leftVal == rightVal);
	}

	@Override
	public void visit(GreaterThan arg0) {
		arg0.getLeftExpression().accept(this);
		long leftVal = curNumericVal;
		arg0.getRightExpression().accept(this);
		long rightVal = curNumericVal;

		curCondition = (leftVal > rightVal);
	}

	@Override
	public void visit(GreaterThanEquals arg0) {
		arg0.getLeftExpression().accept(this);
		long leftVal = curNumericVal;
		arg0.getRightExpression().accept(this);
		long rightVal = curNumericVal;

		curCondition = (leftVal >= rightVal);
	}

	@Override
	public void visit(MinorThan arg0) {
		arg0.getLeftExpression().accept(this);
		long leftVal = curNumericVal;
		arg0.getRightExpression().accept(this);
		long rightVal = curNumericVal;

		curCondition = (leftVal < rightVal);
	}

	@Override
	public void visit(MinorThanEquals arg0) {
		arg0.getLeftExpression().accept(this);
		long leftVal = curNumericVal;
		arg0.getRightExpression().accept(this);
		long rightVal = curNumericVal;

		curCondition = (leftVal <= rightVal);
	}

	@Override
	public void visit(NotEqualsTo arg0) {
		arg0.getLeftExpression().accept(this);
		long leftVal = curNumericVal;
		arg0.getRightExpression().accept(this);
		long rightVal = curNumericVal;

		curCondition = (leftVal != rightVal);
	}

	@Override
	public void visit(AndExpression arg0) {
		arg0.getLeftExpression().accept(this);
		boolean leftCondition = curCondition;
		arg0.getRightExpression().accept(this);
		boolean rightCondition = curCondition;

		curCondition = (leftCondition && rightCondition);
	}

}
