package read;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InputReader {

    private final String inputFile;
    private int dimFrag = 0;
    private int noDoc = 0;
    private String[] docs;

    public InputReader(String inputFile) {
        this.inputFile = inputFile;
    }

    public int getDimFrag() {
        return dimFrag;
    }

    public int getNoDoc() {
        return noDoc;
    }

    public String[] getDocs() {
        return docs;
    }

    public void read() {

        try {
            Scanner scanner = new Scanner(new File(inputFile));
            // Citesc dimfrag
            if (scanner.hasNextLine()) {
                dimFrag = Integer.parseInt(scanner.nextLine());
            }
            else {
                System.out.println("Fisierul de input nu contine dimensiune_fragment");
                return;
            }

            // Citesc numarul de documente
            if (scanner.hasNextLine()) {
                noDoc = Integer.parseInt(scanner.nextLine());
            }
            else {
                System.out.println("Fisierul de input nu contine numarul de documente");
                return;
            }

            docs = new String[noDoc];
            int cnt = 0;

            // Retin numele documentelor
            while (scanner.hasNextLine()) {
                docs[cnt] = scanner.nextLine();
                cnt++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
