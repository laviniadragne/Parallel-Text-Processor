package map;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class GenericMapTask<K extends GenericResultMap> implements Callable<K> {
    protected String nameFile;
    protected int id;

    protected int offset;
    protected int dimFrag;
    protected final int fileSize;

    protected String text;

    protected final ExecutorService tpe;
    protected final AtomicInteger inQueue;

    public GenericMapTask(String nameFile, int id, int offset, int dimFrag, int fileSize, ExecutorService tpe, AtomicInteger inQueue) {
        this.nameFile = nameFile;
        this.id = id;
        this.offset = offset;
        this.dimFrag = dimFrag;
        this.fileSize = fileSize;
        this.tpe = tpe;
        this.inQueue = inQueue;
    }

    public int getId() {
        return id;
    }

    public int getDimFrag() {
        return dimFrag;
    }


    // Citeste de la fisierul nameFile
    // de la un anumit offset dimfrag
    // bytes si ii stocheaza in variabila
    // text
    public void readFragment() throws IOException {

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(nameFile, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Citeste de la offset dimFrag bytes
        byte[] cbuf = new byte[getDimFrag()];

        try {
            assert raf != null;
            raf.seek(offset);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            raf.read(cbuf);
        } catch (IOException e) {
            e.printStackTrace();
        }

        text = new String(cbuf);

        raf.close();
    }

    public abstract void parseFragment();

    public abstract K constructMapResult();

    // Logica generala a operatiei de map
    // citeste, parseaza, construieste rezultatul
    public K call() throws IOException {
        readFragment();
        parseFragment();

        K res = constructMapResult();
        inQueue.decrementAndGet();

        return res;
    }
}
