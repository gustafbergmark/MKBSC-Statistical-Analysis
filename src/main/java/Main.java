import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        PrintWriter writer = new PrintWriter("games.txt");
        /*Generator generator = new Generator(5,2,"ab");
        for (int i = 0; i < 50000; i++) {
            MAGIIAN game = generator.generate();
            writer.println(game.toString());
        }
         */
        getNeighbours123();
        writer.close();
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
