import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("50k32ab.txt"));
        PrintWriter writer = new PrintWriter("32abnooverlap.txt");
        String line;
        while((line = reader.readLine()) != null) {
            JSONObject json = new JSONObject(line);
            MAGIIAN game = new MAGIIAN(json);
            if(Classifier.hasNoOverlap(game)) {
                writer.println(game);
            }
        }

        //getNeighbours123();
        writer.close();
        reader.close();
    }


    private static void getNeighbours() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("neighbours.txt");
        Generator generator = new Generator(3,2,"ab");
        MAGIIAN game = generator.generate();
        game.neighbours(writer);
        writer.close();
    }

    private static void getNeighbours123() throws FileNotFoundException {
        PrintWriter writer = new PrintWriter("neighbours.txt");
        MAGIIAN game = new MAGIIAN(3,2,2);

        game.setAction(0,"a");
        game.setAction(1,"a");

        game.addTransition(0,1,"aa");
        game.addTransition(0,0,"aa");
        game.addTransition(1,0,"aa");
        game.addTransition(1,1,"aa");
        game.addTransition(1,2,"aa");
        game.addTransition(2,0,"aa");
        game.addTransition(2,1,"aa");
        game.addTransition(2,2,"aa");

        game.setObsForPlayer(0,0,0);
        game.setObsForPlayer(0,1,0);
        game.setObsForPlayer(0,2,1);

        game.setObsForPlayer(1,0,0);
        game.setObsForPlayer(1,1,1);
        game.setObsForPlayer(1,2,0);
        game.neighbours(writer);
        writer.close();
    }

}
