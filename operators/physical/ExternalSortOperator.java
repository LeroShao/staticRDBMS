package operators.physical;

import nio.BinaryTupleReader;
import nio.BinaryTupleWriter;
import nio.TupleReader;
import nio.TupleWriter;
import util.Catalog;
import util.Tuple;

import java.io.File;
import java.util.*;

/**
 * Created by s on 3/11/19
 **/
public class ExternalSortOperator extends SortOperator{
    //TODO: more understanding needed
    private TupleReader tpReader;
    private List<TupleReader> tupleReaders;
    private int tpsPerPage;
    private String id = UUID.randomUUID().toString().substring(8);
    private final String localDir = Catalog.tmpDir + id + File.separator;

    public ExternalSortOperator(Operator child, List<?> orders) {
        super(child, orders);

        new File(localDir).mkdirs();
        tpsPerPage = Catalog.PAGESIZE / (Catalog.ATTRSIZE * schema().size());
        int tpsPerRun = tpsPerPage * Catalog.sortBuffPgs;
        List<Tuple> tps = new ArrayList<>(tpsPerRun);
        tupleReaders = new ArrayList<>(Catalog.sortBuffPgs - 1);

        int run = 0;
        while(true) {
            try {
                tps.clear();
                int count = 0;
                Tuple tp;
                while (count++ < tpsPerRun &&
                        (tp = child.getNextTuple()) != null)
                    tps.add(tp);
                if(tps.isEmpty()) break;

                Collections.sort(tps, tupleComp);

                TupleWriter tw = new BinaryTupleWriter(fileName(0, run++));
                for (Tuple tuple : tps)
                    tw.write(tuple);
                tw.close();

                if(tps.size() < tpsPerRun) break;
            }catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
        if(run == 0)
            return;

        int curPass = 1;
        while (run > 1) {
            run = merge(curPass, run);

            File oriFile = new File(fileName(curPass - 1, 0));
            File newFile = new File(localDir + "sorted");
            oriFile.renameTo(newFile);
        }

        try {
            tpReader = new BinaryTupleReader(localDir + "sorted");
        }catch (Exception e) {
            e.printStackTrace();
            tpReader = null;
        }
    }

    private int merge(int curPass, int lastRuns) {
        int curRuns = 0;
        int i = 0;

        while (i < lastRuns) {
            tupleReaders.clear();

            int max = Math.min(i + Catalog.sortBuffPgs - 1, lastRuns);
            for(int j = i; j < max; j++) {
                try {
                    TupleReader tpReader = new BinaryTupleReader(fileName(curPass - 1, j));
                    tupleReaders.add(tpReader);
                }catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }

            try {
                TupleWriter tpWriter = new BinaryTupleWriter(fileName(curPass, curRuns++));
                PriorityQueue<Tuple> pq = new PriorityQueue<>(Catalog.sortBuffPgs - 1, tupleComp);

                for (TupleReader tpReader : tupleReaders) {
                    Tuple tp = tpReader.read();
                    if(tp != null) {
                        tp.tpReader = tpReader;
                        pq.add(tp);
                    }
                }

                while (!pq.isEmpty()) {
                    Tuple tp = pq.poll();
                    tpWriter.write(tp);
                    TupleReader tpReader = tp.tpReader;
                    tp = tpReader.read();
                    if(tp != null) {
                        tp.tpReader = tpReader;
                        pq.add(tp);
                    }
                }

                tpWriter.close();
                for (TupleReader tpReader : tupleReaders)
                    tpReader.close();

                for (int j = i; j < max; j++) {
                    File file = new File(fileName(curPass - 1, j));
                    file.delete();
                }

            }catch (Exception e) {
                e.printStackTrace();
                break;
            }

            i += Catalog.sortBuffPgs - 1;
        }

        return curRuns;
    }

    @Override
    public Tuple getNextTuple() {
        if(tpReader == null)
            return null;
        try {
            return tpReader.read();
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void reset(long index) {
        if(tpReader == null)
            return;
        try {
            tpReader.reset(index);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reset() {
        if(tpReader == null)
            return;
        try {
            tpReader.reset();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String fileName(int pass, int run) {
        return localDir + pass + "_" + run;
    }

}
