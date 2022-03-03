import org.json.JSONArray;
import org.json.JSONObject;

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

    void addTransition(int from, int to, String actions){ delta.add(new Transition(from, to, actions)); }

    void setObsForPlayer(int player, int state, int observation){
        observations[player].setObservation(state, observation);
    }

    public ArrayList<Transition> gettransitionfromstate(int state) {
        ArrayList<Transition> result = new ArrayList<>();
        for (Transition edge:delta) {
            if(edge.from == state) {
                result.add(edge);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        int[] statearray = new int[states];
        for (int i = 0; i < states; i++) {
            statearray[i] = i;
        }
        json.put("states", statearray);
        json.put("L0", l0);
        json.put("sigma", sigma);
        json.put("delta", Arrays.stream(delta.toArray()).map(Object::toString).
                toArray(String[]::new));
        json.put("obs", Arrays.stream(observations).map(Object::toString).
                toArray(String[]::new));

        return json.toString();
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

        return "[" + from +"," + path + "," + to + "]";

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
