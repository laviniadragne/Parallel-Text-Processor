package process;

import map.GenericMapTask;
import map.GenericResultMap;
import reduce.GenericReduceTask;
import reduce.GenericResultReduce;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GenericAllTasks<K extends GenericResultMap, V extends GenericResultReduce> {
    protected List<GenericMapTask<K>> mapTasks;
    protected List<GenericReduceTask<K, V>> reduceTasks;

    protected List<V> finalRes;

    protected final int noWorkers;
    protected final int noDoc;

    protected final AtomicInteger inQueue;
    protected final ExecutorService tpe;

    public GenericAllTasks(int noWorkers, int noDoc) {
        this.noDoc = noDoc;
        this.noWorkers = noWorkers;

        inQueue = new AtomicInteger(0);
        tpe = Executors.newFixedThreadPool(noWorkers);

        mapTasks = new ArrayList<>();
        reduceTasks = new ArrayList<>();
    }

    public List<V> getFinalRes() {
        return finalRes;
    }

    public List<GenericMapTask<K>> getMapTasks() {
        return mapTasks;
    }

    public List<GenericReduceTask<K, V>> getReduceTasks() {
        return reduceTasks;
    }

    public AtomicInteger getInQueue() {
        return inQueue;
    }

    public ExecutorService getTpe() {
        return tpe;
    }

    // Creata o lista de mapTasks
    public abstract void createMapTasks();

    // Pe baza rezultatelor task-urilor de map, creez task-urile de reduce
    public abstract void createReduceTasks(List<List<Future<K>>> mapFutures);

    // Prelucrez rezultatele de la reduce tasks
    public abstract void createFinalRes(List<Future<V>> reduceFutures) throws ExecutionException, InterruptedException;
}
