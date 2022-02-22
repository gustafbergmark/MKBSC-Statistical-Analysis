import java.util.ArrayList;
import java.util.Arrays;

public class MAGIIAN {
    int states;
    int l0;
    int players;
    String[] sigma;
    ArrayList<Transition> delta;
    Observation[] observations;

    public MAGIIAN(int states, int l0, int players) {
        this.states = states;
        this.l0 = l0;
        this.players = players;
        sigma = new String[players];
        delta = new ArrayList<>();
        observations = new Observation[players];
        for (int i = 0; i < players; i++) {
            observations[i] = new Observation();
        }
    }

    void addAction(int player, String actions){
        sigma[player] += actions;
    }

    void setAction(int player, String actions){
        sigma[player] = actions;
    }

    void addTransition(int from, int to, String actions){
        delta.add(new Transition(from, to, actions));
    }

    void setObsForPlayer(int player, int state, int observation){
        observations[player].setObservation(state, observation);
    }

    @Override
    public String toString() {

    }

    class Transition {
        int from;
        int to;
        String path;

        public Transition(int from, int to, String path) {
            this.from = from;
            this.to = to;
            this.path = path;
        }

        @Override
        public String toString() {

        }
    }

    class Observation {
        int[] obs;

        public Observation(){
            obs = new int[states];
        }

        public void setObservation(int state, int observation){
            obs[state] = observation;
        }

        boolean isNormalised(){
            boolean[] seen = new boolean[states];
            int count = 0;
            int max = 0;

            for (int i = 0; i < states; i++) {
                if(obs[i] >= states){return false;}

                if(!seen[obs[i]]) {
                    if (obs[i] > max) {
                        max = obs[i];
                    }
                    count++;
                    seen[obs[i]] = true;
                }
            }
           return (max == count-1);
        }
        //todo: make normailsed method

        @Override
        public String toString() {
            String s = "[";

            int count = 0;
            for (int i = 0; count < states; i++) {
                s += "[";
                for (int j = 0; j < states; j++) {
                   if(i == obs[j]){
                       s += j + ",";
                       count++;
                   }
                }
                s += "],";
            }

            s += "]";
            return s;
        }
    }
}
