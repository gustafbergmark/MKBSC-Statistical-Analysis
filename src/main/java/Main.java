import netscape.javascript.JSObject;
import org.json.JSONObject;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) throws IOException {
        PrintWriter writer = new PrintWriter("games.txt");
        Generator generator = new Generator(5,2,"ab");
        for (int i = 0; i < 50000; i++) {
            MAGIIAN game = generator.generate();
            writer.println(game.toString());
        }
        writer.close();
    }
}
