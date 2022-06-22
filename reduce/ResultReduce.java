package reduce;

import java.util.HashMap;
import java.util.List;

public class ResultReduce extends GenericResultReduce implements Comparable<ResultReduce> {
    private final int id;

    private final double rank;
    private final int lenMax;
    private final int appearances;

    private final HashMap<Integer, Integer> docHashmap;
    private final List<String> docList;

    public ResultReduce(int id, String fileName, double rank, int lenMax, int appearances,
                        HashMap<Integer, Integer> docHashmap, List<String> docList) {
        super(fileName);
        this.id = id;
        this.rank = rank;
        this.lenMax = lenMax;
        this.appearances = appearances;
        this.docHashmap = docHashmap;
        this.docList = docList;
    }

    public String getFilename() {
        return filename;
    }

    public double getRank() {
        return rank;
    }

    public int getLenMax() {
        return lenMax;
    }

    public int getAppearances() {
        return appearances;
    }


    @Override
    public int compareTo(ResultReduce u) {
        if (this.rank < u.rank)
            return 1;
        else if (u.rank < this.rank)
                return -1;
        else if (u.rank == this.rank) {
            return Integer.compare(u.id, this.id);
        }
        return 0;
    }

    @Override
    public String toString() {
        return "ResultReduce{" +
                "filename='" + filename + '\'' +
                ", rank=" + rank +
                ", lenMax=" + lenMax +
                ", appearances=" + appearances +
                ", id=" + id +
                ", docHashmap=" + docHashmap +
                ", docList=" + docList +
                '}';
    }
}
