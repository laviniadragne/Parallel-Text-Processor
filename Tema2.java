import map.ResultMap;
import process.AllTasks;
import process.GenericAllTasks;
import process.GenericProcessor;
import read.InputReader;
import reduce.ResultReduce;
import write.OutputWriter;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

public class Tema2 {
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
        if (args.length < 3) {
            System.err.println("Usage: Tema2 <workers> <in_file> <out_file>");
            return;
        }

        int noWorkers = Integer.parseInt(args[0]);
        String inputFile = args[1];
        String outputFile = args[2];

        // Creez clase care se va ocupa cu cititul datelor de intrare
        InputReader reader = new InputReader(inputFile);
        reader.read();
        int noDoc = reader.getNoDoc();

        // Clasele care se ocupa de logica Map-Reduce
        GenericAllTasks<ResultMap, ResultReduce> allTasks = new AllTasks(noWorkers, noDoc, reader.getDimFrag(), reader.getDocs());
        GenericProcessor<ResultMap, ResultReduce> processor = new GenericProcessor<>(noDoc, allTasks.getTpe(), allTasks.getInQueue());

        processor.setAllTasks(allTasks);

        // Creez task-urile de map
        allTasks.createMapTasks();
        List<List<Future<ResultMap>>> mapFutures =  processor.processMapTasks();

        // Creez task-urile de reduce
        allTasks.createReduceTasks(mapFutures);
        List<Future<ResultReduce>> reduceFutures = processor.processReduceTasks();

        // Lista cu rezultatele reduce-ului
        allTasks.createFinalRes(reduceFutures);
        List<ResultReduce> finalRes = allTasks.getFinalRes();

        // Sortare
        Collections.sort(finalRes);

        // Creez clasa care se va ocupa cu scrierea datelor de iesire
        OutputWriter writer = new OutputWriter(outputFile, noDoc, finalRes);
        writer.write();
    }
}