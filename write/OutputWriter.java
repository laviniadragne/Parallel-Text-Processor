package write;

import reduce.ResultReduce;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class OutputWriter {
    private final String outputFile;
    private final int noDoc;
    private final List<ResultReduce> finalRes;

    public OutputWriter(String outputFile, int noDoc, List<ResultReduce> finalRes) {
        this.outputFile = outputFile;
        this.noDoc = noDoc;
        this.finalRes = finalRes;
    }

    public void write() throws IOException {
        // Scriu in fisierul de iesire
        FileWriter myWriter = new FileWriter(outputFile);

        // Scriu linie cu linie in fisier
        for (int i = 0; i < noDoc; i++) {
            String line = new File(finalRes.get(i).getFilename()).getName() + "," + String.format("%.2f", finalRes.get(i).getRank()) + "," +
                    finalRes.get(i).getLenMax() + "," + finalRes.get(i).getAppearances() + "\n";
            myWriter.write(line);
        }

        myWriter.close();
    }
}
