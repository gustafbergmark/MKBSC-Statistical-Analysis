import java.util.LinkedList;
import java.util.Random;

public class Generator {
    Random random;
    int states, players;
    String actions;
    double k = 0.5;

    public Generator(int states, int players, String actions) {
        this.states = states;
        this.players = players;
        this.actions = actions;
        random = new Random();
    }

    public MAGIIAN generate() {
        MAGIIAN game = new MAGIIAN(states, 0, players);
        for (int i = 0; i < game.players; i++) {
            game.setAction(i,actions);
        }
        LinkedList<String> combinedactions = combinedactions(actions);
        for (int from = 0; from < game.states; from++) {
            for (String combinedaction:combinedactions) {
                int edges = 0;
                boolean[] connected = new boolean[states];
                while(random.nextDouble() < (k/(k+edges)) && edges < states) {
                    int to;
                    do {
                        to = random.nextInt(states);
                    } while (connected[to]);

                    game.addTransition(from, to, combinedaction);
                    connected[to] = true;
                    edges++;
                }
            }
        }
        //https://stats.stackexchange.com/questions/497858/sampling-uniformly-from-the-set-of-partitions-of-a-set
        for (int player = 0; player < players; player++) {
            int k = getbins(states);
            boolean[] assignedstates = new boolean[states];
            for(int bin = 0; bin < k; bin++) {
                int rand = random.nextInt(states);
                while(assignedstates[rand]) {
                    rand = random.nextInt(states);
                }
                game.setObsForPlayer(player, rand, bin);
                assignedstates[rand] = true;
            }
            for (int state = 0; state < states; state++) {
                if(!assignedstates[state]) {
                    int rand = random.nextInt(k);
                    game.setObsForPlayer(player, state, rand);
                }
            }
        }
        return game;
    }

    public static LinkedList<String> combinedactions(String actions) {
        LinkedList<String> result = new LinkedList<>();
        for (int i = 0; i < actions.length(); i++) {
            for (int j = 0; j < actions.length(); j++) {
                result.add(actions.charAt(i) + "" + actions.charAt(j));
            }
        }
        return result;
    }

    //Uniformly chooses amount of bins for observation states according o J. Stam
    public int getbins(int n) {
        double[] binprobability = new double[n];
        double total = 0;
        for (int i = 1; i <= n; i++) {
            double value = Math.pow(i,n) / factorial(i);
            binprobability[i-1] = value;
            total += value;
        }
        for (int i = 0; i < n; i++) {
            binprobability[i] /= total;
        }
        double rand = random.nextDouble();
        int bin = 0;
        while(binprobability[bin] < rand) {
            rand -= binprobability[bin];
            bin++;
        }
        return bin+1;
    }

    public static double factorial(int n) {
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
