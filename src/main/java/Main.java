public class Main {
    public static void main(String[] args) {
        Generator generator = new Generator(4,2,"ab");
        MAGIIAN game = generator.generate();
        System.out.println(game);
    }
}
