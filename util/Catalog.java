package util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Catalog keeps track of information such as
//where a file for a given table is located,
//what the schema of different tables is, and so on

//the catalog is a global entity that various components
//of the system may want to access, 
//so it adapts the Singleton Pattern which means at most one instance 
//of this class could exist

public class Catalog {
	private static Catalog catalog;

	public static final int ATTRSIZE = 4;
	public static final int PAGESIZE = 4096;

	public static String inputDir = "/Users/lirongshao/Desktop/Interpreter/staticRDBMS/samples" + File.separator + "input";
	public static String outputDir = "/Users/lirongshao/Desktop/Interpreter/staticRDBMS/samples" + File.separator + "output";
	public static String tmpDir = "/Users/lirongshao/Desktop/Interpreter/staticRDBMS/samples" +File.separator;
	public static String qryPath;
	public static String dbDir;
	public static String dataDir;
	public static String schemaPath;
	
	public static HashMap<String, List<String>> schemas = new HashMap<>();
	public static HashMap<String, String> aliases = new HashMap<>();

	public enum JoinMethod {
		TNLJ, BNLJ, SMJ;
	}

	public static JoinMethod joinMethod = JoinMethod.SMJ;
	public static Integer joinBuffPgs = 3;

	public enum SortMethod {
		INMEMSORT, EXTERNALSORT;
	}

	public static SortMethod sortMethod = SortMethod.INMEMSORT;
	public static Integer sortBuffPgs = null;

	/** 
	 * intentionally make the constructor private, which 
	 * avoids instances being created outside the class
	 */
	private Catalog() {
		resetDirs(inputDir, outputDir);
	}
	
	/**	
	 * makes sure there is only one instance exist
	 * @return the instance of the class
	 */
	public static Catalog getCatalog() {
		if(catalog == null) {
			catalog = new Catalog();
		}
		return catalog;
	}
	
	public static void resetDirs(String _inputDir, String _outputDir) {
		if(_inputDir != null) {
			inputDir = _inputDir + File.separator;
			qryPath = inputDir + "queries.sql";
			dbDir = inputDir + "db" + File.separator;
			dataDir = dbDir + "data" + File.separator;
			schemaPath = dbDir + "schema.txt";
		}
		if(_outputDir != null) {
			outputDir = _outputDir + File.separator;
		}
		resetSchemas();
	}
	
	private static void resetSchemas() {
		BufferedReader br;
		schemas.clear();
		try {
			br = new BufferedReader(new FileReader(schemaPath));
			String line;
			while ((line = br.readLine()) != null) {
				String[] schema = line.split(" ");
				String key = schema[0];
				List<String> val = new ArrayList<>();
				for (int i = 1; i < schema.length; i++) {
					val.add(schema[i]);
				}
				schemas.put(key, val);
			}
			
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String tablePath(String tableName) {
		return dataDir + tableName + ".txt";
	}
	
	public static String origName(String tableName) {
		if(aliases.containsKey(tableName))
			tableName = aliases.get(tableName);
		return tableName;
	}
	
	public static List<String> getSchema(String tableName) {
		tableName = origName(tableName);
		return schemas.get(tableName);
	}
	
	/**
	 * create a table with alias, schema and BufferedReader
	 * @param tableName
	 * @return the created table
	 */
	public static Table getTable(String tableName) {
		BufferedReader br = getTableReader(tableName);

		if(br == null) return null;
		return new Table(tableName, getSchema(tableName), br);
	}
	
	public static BufferedReader getTableReader(String tableName) {
		tableName = origName(tableName);
		try {
			return new BufferedReader(new FileReader(tablePath(tableName)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
