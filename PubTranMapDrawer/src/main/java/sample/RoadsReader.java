package sample;

import com.opencsv.CSVReader;
import jdk.internal.util.xml.impl.ReaderUTF8;

import java.io.FileReader;
import java.util.*;

public class RoadsReader {

    public static final String
            K_INIT_NODE = "origin", K_TERM_NODE = "dest",
            K_CAPACITY = "capacity", K_LENGTH = "length",
            K_FREE_FLOW_TIME = "free-flow-time", K_B = "b",
            K_POWER = "power", K_SPEED_LIMIT = "speed-limit",
            K_TOLL = "toll", K_TYPE = "type";

    /**
     *
     * @param path - String path to the file
     * @return List of tables containing rows from origin-destination csv data
     *
     * ** Each row contains (separated by tab):
     *      Init Node       0
     *      Term Node       1
     *      Capacity        2
     *      Length          3
     *      Free Flow Time  4
     *      B               5
     *      Power           6
     *      Speed Limit     7
     *      Toll            8
     *      Type            9
     *      ;               10
     */

    public static List<Map<String, Double>> readFrom(String path) {
        final int HEADER_LINES = 9;

        String keys[] = new String[]{K_INIT_NODE, K_TERM_NODE, K_CAPACITY, K_LENGTH,
                K_FREE_FLOW_TIME, K_B, K_POWER, K_SPEED_LIMIT, K_TOLL, K_TYPE};

        CSVReader reader = null;
        List<Map<String, Double>> tablesList = new ArrayList<>();
        try {
            reader = new CSVReader(new FileReader(path), '\t');
            reader.skip(HEADER_LINES);
            Iterator<String[]> linesIterator = reader.iterator();

            Set<String> noDoubleRoads = new HashSet<>();
            for (int i = 0; linesIterator.hasNext() ; i++) {
                Map<String, Double> rowContentTable = new HashMap<>();
                String[] row = linesIterator.next();
                if(row[0].startsWith("~")){
                    continue;
                }
                for (int j = 0; j < keys.length; j++) {
                    rowContentTable.put(keys[j], Double.parseDouble(row[j + 1]));
                }

                String uniqueId = toUniqueKey(rowContentTable.get(K_INIT_NODE).intValue(),
                        rowContentTable.get(K_TERM_NODE).intValue());
                if(!noDoubleRoads.contains(uniqueId)){
                    noDoubleRoads.add(uniqueId);
                    tablesList.add(rowContentTable);
                }
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return tablesList;
    }

    private static String toUniqueKey(int initNode, int termNode){
        return Math.min(initNode, termNode) + "," + Math.max(initNode, termNode);
    }

    public static List<Road> loadRoads(String path, List<Node> nodes){
        List<Map<String, Double>> roadsAsMaps = readFrom(path);
        List<Road> roads = new ArrayList<>();
        int i = 1;
        for (Map<String, Double> roadMap : roadsAsMaps){
            roads.add(new Road(
                    i++,
                    nodes.get(roadMap.get(K_INIT_NODE).intValue() - 1),
                    nodes.get(roadMap.get(K_TERM_NODE).intValue() - 1),
                    roadMap.get(K_CAPACITY))
            );
        }

        return roads;
    }
}
