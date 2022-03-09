import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;

import static org.apache.commons.math3.stat.inference.TestUtils.*;

public class Statistics {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("analysedgames.txt"));
        testindependence(reader, Classifier::hasNature);
        //bigstat();
    }

    public static void bigstat() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("analysedgames.txt"));

        String line;
        int cycles = 0;
        int overlap = 0;
        int nature = 0;
        int total = 0;
        while((line = reader.readLine()) != null) {
            JSONObject json = new JSONObject(line);
            MAGIIAN game = new MAGIIAN(json);
            if(Classifier.hasCycles((game))) {
                cycles++;
            }
            if(Classifier.hasOverlap(game)) {
                overlap++;
            }
            if(Classifier.hasNature(game)) {
                nature++;
            }
            total++;

        }
        System.out.println("Cycles: " + cycles);
        System.out.println("Overlap: " + overlap);
        System.out.println("Nature: " + nature);
        System.out.println("Total: " + total);
    }

    public static void testindependence(BufferedReader games, Function<MAGIIAN, Boolean> function) throws IOException {
        long[][] values = new long[2][2];
        String line;
        while((line = games.readLine()) != null) {
            JSONObject json = new JSONObject(line);
            MAGIIAN game = new MAGIIAN(json);
            if(function.apply(game)) {
                if(game.stabilises > 0) {
                    values[0][0]++;
                } else {
                    values[0][1]++;
                }
            } else {
                if(game.stabilises > 0) {
                    values[1][0]++;
                } else {
                    values[1][1]++;
                }
            }
        }
        System.out.println(values[0][0]);
        System.out.println(values[0][1]);
        System.out.println(values[1][0]);
        System.out.println(values[1][1]);
        System.out.println("Alpha 0.5: " + chiSquareTest(values, 0.5));
        System.out.println("Alpha 0.1: " + chiSquareTest(values, 0.1));
        System.out.println("Alpha 0.05: " + chiSquareTest(values, 0.05));
        System.out.println("Alpha 0.01: " + chiSquareTest(values, 0.01));
    }
}
