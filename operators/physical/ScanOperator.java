package operators.physical;

import util.Table;
import util.Tuple;

import java.util.ArrayList;
import java.util.List;

public class ScanOperator extends Operator{	
	Table table;
	
	/**
	 * construct a scan operator for a table
	 */
	public ScanOperator(Table table) {
		this.table = table;
		schema = new ArrayList<>();
		if (table == null || table.schema == null){
			System.out.println("table empty:" + (table == null)
					+ "\nschema empty: " + (table.schema == null));
		}
		for (String col : table.schema) {
			schema.add(table.name + '.' + col);
		}
	}

	@Override
	public Tuple getNextTuple() {
		return table.nextTuple();
	}

	@Override
	public void reset() {
		table.reset();
	}

	@Override
	public List<String> schema() {
		return schema;
	}
}
