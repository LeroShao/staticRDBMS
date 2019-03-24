package client;

import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import nio.ReadableTupleWriter;
import nio.TupleWriter;
import util.Catalog;
import util.RandomDataGenarator;
import util.TreeBuilder;

import java.io.File;
import java.io.FileReader;

public class StaticRDBMS {
	public static void main(String[] args) {
        Catalog.getCatalog();

        String dataDir = Catalog.dataDir;

        try {
            RandomDataGenarator.gen(dataDir + "Sailors.txt", 5000, 3, 500);
            RandomDataGenarator.gen(dataDir + "Boats.txt", 5000, 3, 500);
            RandomDataGenarator.gen(dataDir + "Reserves.txt", 5000, 2, 500);
        }catch (Exception e) {
            e.printStackTrace();
        }

//        PrintStream printStream = System.out;

        try {
            CCJSqlParser parser = new CCJSqlParser(new FileReader(Catalog.qryPath));
            Statement statement;
            int count = 1;
            while ((statement = parser.Statement()) != null) {
                File file = new File(Catalog.outputDir + File.separator + "#" + count);
                TupleWriter tw = new ReadableTupleWriter(file.getAbsolutePath());
                TreeBuilder tb = new TreeBuilder(statement);
                long start = System.currentTimeMillis();
                tb.root.dump(tw);
                long end = System.currentTimeMillis();
                System.out.println("time for #" + count + ": " + (end - start) + "ms");
                count++;
                tw.close();
            }
        } catch (Exception e) {
            System.err.println("Exception occurred during parsing");
            e.printStackTrace();
        }
	}
}
