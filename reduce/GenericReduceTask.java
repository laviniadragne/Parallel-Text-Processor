package reduce;

import map.GenericResultMap;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GenericReduceTask<K extends GenericResultMap, V extends GenericResultReduce> implements Callable<V> {
    protected final String fileName;
    protected int id;

    protected List<Future<K>> inputFromMap;

    protected final ExecutorService tpe;
    protected final AtomicInteger inQueue;

    public GenericReduceTask(String filename, int id, List<Future<K>> inputFromMap, ExecutorService tpe, AtomicInteger inQueue) {
        this.fileName = filename;
        this.id = id;

        this.inputFromMap = inputFromMap;

        this.tpe = tpe;
        this.inQueue = inQueue;
    }

    public abstract V constructReduceResult();

    // Contine logica generala a operatiei
    // de reduce
    @Override
    public V call() {
        V res = constructReduceResult();

        int left = inQueue.decrementAndGet();
        if (left == 0) {
            tpe.shutdown();
        }

        return res;
    }
}
