import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;

import static org.apache.commons.math3.stat.inference.TestUtils.*;

public class Statistics {
    public static void main(String[] args) throws IOException {
        testindependences();
        bigstat();
    }


    public static void testindependences() throws IOException {
        System.out.println("Has cycles:");
        testindependence(Classifier::hasCycles);

        System.out.println("Has nature:");
        testindependence(Classifier::hasNature);

        System.out.println("Has subset overlap only:");
        testindependence(Classifier::hasOverlapSubsetOnly);

        System.out.println("Has overlap with no subsets only:");
        testindependence(Classifier::hasOverlapNoSubsetOnly);

        System.out.println("Has both kinds of overlap:");
        testindependence(Classifier::hasOverlapBoth);

        System.out.println("Has L0 valens:");
        testindependence(Classifier::hasL0Valens);
    }

    public static void bigstat() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("analysedgames.txt"));

        String line;
        int cycles = 0;
        int overlapSubset = 0;
        int overlapNoSubset = 0;
        int overlapBoth = 0;
        int noOverlap = 0;
        int nature = 0;
        int l0valens = 0;
        int total = 0;
        while((line = reader.readLine()) != null) {
            JSONObject json = new JSONObject(line);
            MAGIIAN game = new MAGIIAN(json);
            if(Classifier.hasCycles((game))) {
                cycles++;
            }
            if(Classifier.hasOverlapSubsetOnly(game)) {
                overlapSubset++;
            }
            if(Classifier.hasOverlapNoSubsetOnly(game)) {
                overlapNoSubset++;
            }
            if(Classifier.hasOverlapBoth(game)) {
                overlapBoth++;
            }
            if(Classifier.hasNoOverlap(game)) {
                noOverlap++;
            }
            if(Classifier.hasNature(game)) {
                nature++;
            }
            if(Classifier.hasL0Valens(game)) {
                l0valens++;
            }
            total++;

        }
        System.out.println("Cycles: " + cycles);
        System.out.println("Overlap Subset: " + overlapSubset);
        System.out.println("Overlap no Subset: " + overlapNoSubset);
        System.out.println("No overlap: " + noOverlap);
        System.out.println("Overlap both: " + overlapBoth);
        System.out.println("Nature: " + nature);
        System.out.println("L0 Valens: " + l0valens);
        System.out.println("Total: " + total);
    }

    public static void testindependence(Function<MAGIIAN, Boolean> function) throws IOException {
        BufferedReader games = new BufferedReader(new FileReader("analysedgames.txt"));
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
        /*System.out.println(values[0][0]);
        System.out.println(values[0][1]);
        System.out.println(values[1][0]);
        System.out.println(values[1][1]);
        System.out.println("Alpha 0.5: " + chiSquareTest(values, 0.5));*/
        System.out.println("Alpha 0.1: " + chiSquareTest(values, 0.1));
        System.out.println("Alpha 0.05: " + chiSquareTest(values, 0.05));
        System.out.println("Alpha 0.01: " + chiSquareTest(values, 0.01));
        System.out.println();
        games.close();
    }
}
