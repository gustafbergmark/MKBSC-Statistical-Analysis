import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.function.Function;

import static org.apache.commons.math3.stat.inference.TestUtils.*;

public class Statistics {
    public static void main(String[] args) throws IOException {
        testindependences();
        //bigstat();
    }


    public static void testindependences() throws IOException {
        /*System.out.println("Has cycles:");
        testindependence(Classifier::hasCycles);

        System.out.println("Has nature:");
        testindependence(Classifier::hasNature);

        System.out.println("Has subset overlap only:");
        testindependence(Classifier::hasOverlapSubsetOnly);

        System.out.println("Has overlap with no subsets only:");
        testindependence(Classifier::hasOverlapNoSubsetOnly);

        System.out.println("Has both kinds of overlap:");
        testindependence(Classifier::hasOverlapBoth);

        System.out.println("Has WFO:");
        testindependence(Classifier::hasWFO);

        System.out.println("Has total confusion:");
        testindependence(Classifier::hasBothPlayerUncertain);

        System.out.println("Has no DADK:");
        testindependence(Classifier::hasNoDifferentActionDifferentKnowledge);

        System.out.println("Has no DADK new:");
        testindependence(Classifier::hasNoDifferentActionDifferentKnowledgeNEW);*/

        System.out.println("Has no DADK new new:");
        testindependence(Classifier::hasNoDifferentActionDifferentKnowledgeNEWnew);

        /*System.out.println("Has no DADK new new new:");
        testindependence(Classifier::hasNoDifferentActionDifferentKnowledgeNEWnewNEW);*/


        System.out.println("Has DWC:");
        testindependence(Classifier::hasDeterminismWithCooperation);
    }

    public static void bigstat() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("50kanalysed.txt"));

        String line;
        int cycles = 0;
        int overlapSubset = 0;
        int overlapNoSubset = 0;
        int overlapBoth = 0;
        int noOverlap = 0;
        int nature = 0;
        int wfo = 0;
        int totalconfusion = 0;
        int dadk = 0;
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
            if(Classifier.hasWFO(game)) {
                wfo++;
            }
            if(Classifier.hasBothPlayerUncertain(game)) {
                totalconfusion++;
            }
            if(Classifier.hasNoDifferentActionDifferentKnowledge(game)) {
                dadk++;
            }
            total++;

        }
        System.out.println("Cycles: " + cycles);
        System.out.println("Overlap Subset: " + overlapSubset);
        System.out.println("Overlap no Subset: " + overlapNoSubset);
        System.out.println("No overlap: " + noOverlap);
        System.out.println("Overlap both: " + overlapBoth);
        System.out.println("Nature: " + nature);
        System.out.println("WFO: " + wfo);
        System.out.println("Total confusion: " + totalconfusion);
        System.out.println("DADK: " + dadk);
        System.out.println("Total: " + total);
    }

    public static void testindependence(Function<MAGIIAN, Boolean> function) throws IOException {
        BufferedReader games = new BufferedReader(new FileReader("50k32ab.txt"));
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
        System.out.println("true and stable:   " + values[0][0]);
        System.out.println("true and diverge:  " + values[0][1]);
        System.out.println("false and stable:  " + values[1][0]);
        System.out.println("false and diverge: " + values[1][1]);
        System.out.println("Alpha 0.1: " + chiSquareTest(values, 0.1));
        System.out.println("Alpha 0.05: " + chiSquareTest(values, 0.05));
        System.out.println("Alpha 0.01: " + chiSquareTest(values, 0.01));
        System.out.println();
        games.close();
    }
}
