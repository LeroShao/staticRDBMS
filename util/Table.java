package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Table {
	public String name;
	public List<String> schema;
	private BufferedReader br;
	
	public Table(String name, List<String> schema, BufferedReader br) {
		this.name = name;
		this.schema = schema;
		this.br = br;
	}
	
	public Tuple nextTuple() {
		try {
			String line = br.readLine();
			if(line != null) {
				String[] elements = line.split(",");
				int[] cols = new int[elements.length];
				for(int i = 0; i < elements.length; i++) {
					cols[i] = Integer.parseInt(elements[i]);
				}

				return new Tuple(cols);
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void reset() {
		if(br != null) {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		br = Catalog.getTableReader(name);
	}
}
