package reduce;

import map.ResultMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ReduceTask extends GenericReduceTask<ResultMap, ResultReduce>  {
    private final HashMap<Integer, Integer> docHashmap;
    private final List<String> docList;

    private int lenMax;
    private int appearances;
    private double rank;

    private int totalWords;

    public ReduceTask(String fileName, int id, List<Future<ResultMap>> inputFromMap,
                      ExecutorService tpe, AtomicInteger inQueue) {
        super(fileName, id, inputFromMap, tpe, inQueue);
        rank = 0;

        docHashmap = new HashMap<>();
        docList = new ArrayList<>();
    }

    // Genereaza o lista cu toate numerele fibonacci
    // pana la al n-lea
    private List<Integer> generateFibo(Integer n) {
        int a, b = 0, c = 1;
        List<Integer> l = new ArrayList<>();

        l.add(0, 1);
        for (int i = 1; i <= n; i++) {
            a = b;
            b = c;
            c = a + b;
            l.add(i, c);
        }
        return l;
    }

    // Etapa de combinare
    private void combine() throws ExecutionException, InterruptedException {
        for (Future<ResultMap> abstractMap : inputFromMap) {

            // Fortez obtinerea rezultatului
            // de la task-ul de map
            ResultMap miniClass = abstractMap.get();
            HashMap<Integer, Integer> miniMap = miniClass.getResultMap();
            List<String> miniList = miniClass.getResultList();

            // Contorizez numarul total de cuvinte
            for (Map.Entry<Integer, Integer> entry : miniMap.entrySet()) {
                totalWords += entry.getValue();
            }

            // Daca key-urile sunt egale, valoare va fi suma dintre cele 2
            // value
            miniMap.forEach(
                    (key, value) -> docHashmap.merge(key, value, Integer::sum));

            // Gasesc cuvantul de lungime maxima
            // actualizand permant lista rezultat
            // si numarul de aparitii
            for (String localWord : miniList) {
                if (lenMax < localWord.length()) {
                    docList.clear();
                    docList.add(localWord);
                    lenMax = localWord.length();
                    appearances = 1;
                }
                else if (lenMax == localWord.length()) {
                    appearances++;
                    docList.add(localWord);
                }
            }
        }
    }

    private void constructRank(List<Integer> fibo) {
        // Parcurg dictionarul mare si calculez rangul
        for (Map.Entry<Integer, Integer> entry : docHashmap.entrySet()) {
            Integer lenWord = entry.getKey();
            Integer occurWord = entry.getValue();
            rank += fibo.get(lenWord) * occurWord;
        }

        rank = rank / totalWords;
    }

    @Override
    public ResultReduce constructReduceResult() {
        // Etapa de combinare
        try {
            combine();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // Generez numerele fibonaci pana la lenMax
        List<Integer> fibo = generateFibo(lenMax);

        // Etapa de procesare
        constructRank(fibo);

        return new ResultReduce(id, fileName, rank, lenMax, appearances, docHashmap, docList);
    }
}
