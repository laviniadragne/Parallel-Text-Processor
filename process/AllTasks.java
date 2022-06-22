package process;

import map.MapTask;
import map.ResultMap;
import reduce.ReduceTask;
import reduce.ResultReduce;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AllTasks extends GenericAllTasks<ResultMap, ResultReduce> {
    private final int dimFrag;
    private final String[] docs;

    public AllTasks(int noWorkers, int noDoc, int dimFrag, String[] docs) {
        super(noWorkers, noDoc);
        this.dimFrag = dimFrag;
        this.docs = docs;
    }

    @Override
    public void createMapTasks() {

        // Task-urile de map
        for (int i = 0; i < noDoc; i++) {
            String fileName = docs[i];
            int fileSize = (int) new File(fileName).length();
            int offset;
            // Pentru fiecare fragment ce trebuie procesat
            // creez un task de map
            for (offset = 0; offset < (fileSize - dimFrag); offset += dimFrag) {
                mapTasks.add(new MapTask(tpe, inQueue, fileName, i, offset, dimFrag, fileSize));
            }

            mapTasks.add(new MapTask(tpe, inQueue, fileName, i, offset, (fileSize - offset), fileSize));
        }
    }

    @Override
    public void createReduceTasks(List<List<Future<ResultMap>>> mapFutures) {

        // Task-urile de reduce, pentru fiecare document un task
        for (int i = 0; i < noDoc; i++) {
            List<Future<ResultMap>> futureDoc = mapFutures.get(i);
            reduceTasks.add(new ReduceTask(docs[i], i, futureDoc, tpe, inQueue));
        }
    }

    public void createFinalRes(List<Future<ResultReduce>> reduceFutures) throws ExecutionException, InterruptedException {
        finalRes = new ArrayList<>();

        // Fortez obtinerea rezultatelor de la operatia de reduce
        for (Future<ResultReduce> f : reduceFutures) {
            finalRes.add(f.get());
        }
    }
}
