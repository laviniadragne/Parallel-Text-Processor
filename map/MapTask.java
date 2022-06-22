package map;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

public class MapTask extends GenericMapTask<ResultMap> {

    private final HashMap<Integer, Integer> resultMap = new HashMap<>();
    private final ArrayList<String> resultList = new ArrayList<>();

    private List<String> words;

    public MapTask(ExecutorService tpe, AtomicInteger inQueue, String nameFile, int id, int offset,
                   int dimFrag, int fileSize) {
        super(nameFile, id, offset, dimFrag, fileSize, tpe, inQueue);
    }


    public int getDimFrag() {
        return dimFrag;
    }


    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    @Override
    public void parseFragment() {
        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(nameFile, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        assert raf != null;

        // Imparte intr-un vector de tokeni textul, dupa delimitatori
        String[] tokens = text.split("\\P{LD}+");

        words = new ArrayList<>(Arrays.asList(tokens));

        // Sterg toate ""
        words.removeAll(Arrays.asList("", null));

        // Delimitatorii de parsare
        String bounded = ";:/?˜\\.,><‘[]{}()!@#$%ˆ&- +’`'=*”| \r\n\t";

        // Elimin primul cuvant, daca este cazul
        if ((offset - 1) >= 0) {
            try {
                // Ultimul caracter din chunk-ul din stanga
                raf.seek((offset - 1));
                char c = (char) raf.readByte();

                // Primul caracter din fragmentul actual
                raf.seek(offset);
                char firstChar = (char) raf.readByte();

                if (bounded.indexOf(c) == -1 && bounded.indexOf(firstChar) == -1) {
                    words.remove(0);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Adaug ultimul cuvant daca este cazul
        if (offset + dimFrag < fileSize) {
            char c = 0;
            char lastChar = 0;

            try {
                // Ultimul caracter din fragmentul actual
                raf.seek(offset + dimFrag - 1);
                try {
                    lastChar = (char) raf.readByte();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Primul caracter din urmatorul fragment
                raf.seek(offset + dimFrag);
                try {
                    c = (char) raf.readByte();
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } catch (IOException e) {
                e.printStackTrace();
            }


            // Adaug litera cu litera ultimul cuvant complet din fragment
            StringBuilder newWord = new StringBuilder(words.get(words.size() - 1));
            int cnt = offset + dimFrag;

            if (bounded.indexOf(lastChar) == -1) {
                while (bounded.indexOf(c) == -1) {
                    newWord.append(c);
                    cnt++;
                    if (cnt < fileSize) {
                        try {
                            raf.seek(cnt);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            c = (char) raf.readByte();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        break;
                    }
                }
                words.set(words.size() - 1, newWord.toString());
            }
        }
    }


    public ResultMap constructMapResult() {

        // Construiesc map-ul
        int maxDim = 0;
        for (String word : words) {
            if (word != null && !word.equals("")) {
                // Daca cuvantul nu a fost deja procesat
                if (!resultMap.containsKey(word.length())) {
                    Integer count = 0;
                    for (String s : words) {
                        if (s != null && !s.equals("")) {
                            if (word.length() == s.length()) {
                                count++;
                            }
                        }
                    }
                    // adaug cuvantul
                    resultMap.put(word.length(), count);
                }

                if (word.length() > maxDim) {
                    maxDim = word.length();
                    // golesc lista si il adaug
                    if (resultList.size() > 0) {
                        resultList.clear();
                        resultList.add(word);
                    }
                    // lista era deja goala
                    else {
                        resultList.add(word);
                    }
                } else if (word.length() == maxDim) {
                    resultList.add(word);
                }
            }
        }

        return new ResultMap(nameFile, resultMap, resultList);
    }
}
