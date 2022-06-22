package map;

import java.util.ArrayList;
import java.util.HashMap;

public class ResultMap extends GenericResultMap {
    private final HashMap<Integer, Integer> resultMap;
    private final ArrayList<String> resultList;

    public ResultMap(String fileName, HashMap<Integer, Integer> resultMap, ArrayList<String> resultList) {
        super(fileName);
        this.resultMap = resultMap;
        this.resultList = resultList;
    }

    public HashMap<Integer, Integer> getResultMap() {
        return resultMap;
    }

    public ArrayList<String> getResultList() {
        return resultList;
    }

    @Override
    public String toString() {
        return "ResultMap{" +
                "resultMap=" + resultMap +
                ", resultList=" + resultList +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
