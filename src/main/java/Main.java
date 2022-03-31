import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        /*PrintWriter writer = new PrintWriter("games.txt");
        Generator generator = new Generator(3,2,"ab");
        for (int i = 0; i < 50000; i++) {
            MAGIIAN game = generator.generate();
            writer.println(game.toString());
        }

        //getNeighbours123();
        writer.close();*/
        JSONObject json = new JSONObject("{\"sigma\":[\"ab\",\"ab\"],\"obs\":[\"[[2,],[0,1,],]\",\"[[0,],[1,2,],]\"],\"L0\":0,\"players\":2,\"stabilises\":0,\"delta\":[\"[0,aa,1]\",\"[0,ab,0]\",\"[0,ba,2]\",\"[0,bb,0]\",\"[0,bb,1]\",\"[0,bb,2]\",\"[1,aa,2]\",\"[1,aa,1]\",\"[1,ab,0]\",\"[1,ba,1]\",\"[1,bb,0]\",\"[2,aa,2]\",\"[2,ab,1]\",\"[2,ab,2]\",\"[2,ba,1]\",\"[2,bb,2]\"],\"states\":[0,1,2]}");
        MAGIIAN game = new MAGIIAN(json);
        ArrayList<ArrayList<Integer>> reachable = game.getPlayersActualPossibleKnowledge(0);
        for (ArrayList<Integer> knowledge:reachable) {
            for (int state:knowledge) {
                System.out.print(state + " ");
            }
            System.out.println();
        }
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
