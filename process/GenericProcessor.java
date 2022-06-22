package process;

import map.GenericResultMap;
import reduce.GenericResultReduce;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class GenericProcessor<K extends GenericResultMap, V extends GenericResultReduce> {
    GenericAllTasks<K, V> allTasks;

    protected final int noDoc;

    protected final AtomicInteger inQueue;
    protected final ExecutorService tpe;

    public GenericProcessor(int noDoc, ExecutorService tpe, AtomicInteger inQueue) {
        this.noDoc = noDoc;
        this.tpe = tpe;
        this.inQueue = inQueue;
    }

    public void setAllTasks(GenericAllTasks<K, V> allTasks) {
        this.allTasks = allTasks;
    }

    public List<List<Future<K>>> processMapTasks() {
        // Lista mare cu listele de future-uri pentru toate doc-urile
        List<List<Future<K>>> mapFutures = Collections.synchronizedList(new ArrayList<>());

        // Fiecare document va avea o lista sincronizata de dictionare
        // si liste cu cuvinte de lungime maxima
        for (int i = 0; i < noDoc; i++) {
            List<Future<K>> mapFuture = Collections.synchronizedList(new ArrayList<>());
            mapFutures.add(mapFuture);
        }

        // Parcurg lista de task-uri de map si dau submit,
        // adaugand rezultatul de tip Future al fiecarui task
        // in lista mare de rezultate ale operatiei de map
        for (int i = 0; i < allTasks.getMapTasks().size(); i++) {
            inQueue.incrementAndGet();
            Future<K> res =  tpe.submit(allTasks.getMapTasks().get(i));
            mapFutures.get(allTasks.getMapTasks().get(i).getId()).add(res);
        }

        return mapFutures;
    }

    public List<Future<V>> processReduceTasks() {

        // Task-urile de reduce
        List<Future<V>> reduceFutures = Collections.synchronizedList(new ArrayList<>());

        // Salvez rezultatele de tip Future, ale task-urilor reduce,
        // intr-o lista
        for (int i = 0; i < allTasks.getReduceTasks().size(); i++) {
            inQueue.incrementAndGet();
            Future<V> res = tpe.submit(allTasks.getReduceTasks().get(i));
            reduceFutures.add(res);
        }

        return reduceFutures;
    }
}
