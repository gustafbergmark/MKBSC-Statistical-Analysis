import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MAGIIAN {
    int states;
    int l0;
    int players;
    int stabilises;
    String[] sigma;
    ArrayList<Transition> delta;
    Observation[] observations;

    public MAGIIAN(int states, int l0, int players) {
        this.states = states;
        this.l0 = l0;
        this.players = players;
        stabilises = -1; //dummy value, meant to be updated by mkbsc
        sigma = new String[players];
        delta = new ArrayList<>();
        observations = new Observation[players];
        for (int i = 0; i < players; i++) {
            observations[i] = new Observation();
        }
    }

    public MAGIIAN(JSONObject json) {
        states = json.getJSONArray("states").length();
        l0 = json.getInt("L0");
        players = json.getInt("players");
        stabilises = json.getInt("stabilises");
        sigma = new String[players];
        delta = new ArrayList<>();
        observations = new Observation[players];
        for (int i = 0; i < players; i++) {
            observations[i] = new Observation();
        }
        //sigma
        JSONArray actions = json.getJSONArray("sigma");
        for (int i = 0; i < actions.length(); i++) {
            setAction(i, actions.getString(i));
        }
        //delta
        JSONArray transitions = json.getJSONArray("delta");
        for (int i = 0; i < transitions.length(); i++) {
            JSONArray parsed = new JSONArray(transitions.getString(i));
            addTransition(parsed.getInt(0), parsed.getInt(2), parsed.getString(1));
        }
        //observations
        JSONArray observationarray = json.getJSONArray("obs");
        for (int i = 0; i < observationarray.length(); i++) {
            JSONArray parsedplayerobs = new JSONArray(observationarray.getString(i));
            int currentobs = 0;
            for (int j = 0; j < parsedplayerobs.length(); j++) {
                JSONArray parsedobsstates = parsedplayerobs.getJSONArray(j);
                for (int k = 0; k < parsedobsstates.length(); k++) {
                    setObsForPlayer(i, parsedobsstates.getInt(k), currentobs);
                }
                currentobs++;
            }
        }
    }

    ArrayList<Integer> visitedstates;

    public ArrayList<Integer> reachablestates() {
        if(visitedstates != null) return visitedstates;
        visitedstates = new ArrayList<>();
        return reachabelstateshelper(l0);
    }

    public ArrayList<Integer> reachabelstateshelper(int state) {
        if(visitedstates.contains(state)) {
            return new ArrayList<>();
        }
        visitedstates.add(state);
        ArrayList<Integer> result = new ArrayList<>();
        result.add(state);
        for (Transition transition:gettransitionfromstate(state)) {
            result.addAll(reachabelstateshelper(transition.to));
        }
        return result;
    }

    public ArrayList<ArrayList<Integer>> getPlayersActualPossibleKnowledge(int player) {
        ArrayList<Integer> startstate = new ArrayList<>();
        startstate.add(l0);
        visitedknowledgestates = new ArrayList<>();
        return PAPKhelper(this, player, startstate);
    }

    public boolean visited(ArrayList<Integer> currentstate) {
        for (ArrayList<Integer> visited: visitedknowledgestates) {
            if(visited.containsAll(currentstate) && currentstate.containsAll(visited)) {
                return true;
            }
        }
        return false;
    }

    ArrayList<ArrayList<Integer>> visitedknowledgestates;

    public ArrayList<ArrayList<Integer>> PAPKhelper(MAGIIAN game, int player, ArrayList<Integer> currentstate) {
        if(visited(currentstate)) {
            return new ArrayList<>();
        }
        visitedknowledgestates.add(currentstate);
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        result.add(currentstate);
        ArrayList<Transition> transitions = new ArrayList<>();
        for (int state : currentstate) {
            transitions.addAll(game.gettransitionfromstate(state));
        }
        for (char action:game.sigma[player].toCharArray()) {
            ArrayList<Integer> reachable = new ArrayList<>();
            for (MAGIIAN.Transition transition : transitions) {
                if(transition.playermove(player)==action) {
                    reachable.add(transition.to);
                }
            }
            for (int currentobs = 0; currentobs < game.observations[player].size(); currentobs++) {
                ArrayList<Integer> newstate = new ArrayList<>();
                for (int state:reachable) {
                    if(game.observations[player].obs[state] == currentobs) {
                        if(!newstate.contains(state)) {
                            newstate.add(state);
                        }
                    }
                }
                result.addAll(PAPKhelper(game, player, newstate));
            }
        }
        return result;
    }

    public ArrayList<ArrayList<Integer>> advancedtraversehelper(MAGIIAN game, int player, ArrayList<Integer> currentstate, int secondplayer, char secondplayeraction) {
        if(visited(currentstate)) {
            return new ArrayList<>();
        }
        visitedknowledgestates.add(currentstate);
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        result.add(currentstate);
        ArrayList<Transition> transitions = new ArrayList<>();
        for (int state : currentstate) {
            transitions.addAll(game.gettransitionfromstate(state));
        }
        transitions = new ArrayList<>(transitions.stream().filter(transition -> transition.playermove(secondplayer) == secondplayeraction).collect(Collectors.toList()));
        for (char action:game.sigma[player].toCharArray()) {
            ArrayList<Integer> reachable = new ArrayList<>();
            for (MAGIIAN.Transition transition : transitions) {
                if(transition.playermove(player)==action) {
                    reachable.add(transition.to);
                }
            }
            for (int currentobs = 0; currentobs < game.observations[player].size(); currentobs++) {
                ArrayList<Integer> newstate = new ArrayList<>();
                for (int state:reachable) {
                    if(game.observations[player].obs[state] == currentobs) {
                        if(!newstate.contains(state)) {
                            newstate.add(state);
                        }
                    }
                }
                result.addAll(advancedtraversehelper(game, player, newstate, secondplayer, secondplayeraction));
            }
        }
        return result;
    }

    public ArrayList<ArrayList<ArrayList<Integer>>> advancedtraverse(int player, int secondplayer) {
        ArrayList<Integer> startstate = new ArrayList<>();
        startstate.add(l0);
        visitedknowledgestates = new ArrayList<>();
        ArrayList<ArrayList<ArrayList<Integer>>> result = new ArrayList<>();
        for (char action:this.sigma[secondplayer].toCharArray()) {
            result.add(advancedtraversehelper(this, player, startstate, secondplayer, action));
        }
        return result;
    }

    public void neighbours(PrintWriter writer) {
        //print base game
        writer.println(this.toString());

        //vary edges
        for (Transition edge:delta) {
            int original = edge.to;
            for (int i = 0; i < states; i++) {
                edge.to = i;
                if(i != original) {
                    writer.println(this.toString());
                }
                edge.to = original;
            }
        }

        //vary observations by swapping which observation two states belong to.
        for (Observation observation:observations) {
            for (int i = 0; i < states; i++) {
                for (int j = i+1; j < states; j++) {
                    if(observation.obs[i] != observation.obs[j] && (observation.getSize(i) > 1 || observation.getSize(j) > 1)) {
                        int temp = observation.obs[i];
                        observation.obs[i] = observation.obs[j];
                        observation.obs[j] = temp;

                        writer.println(this.toString());

                        observation.obs[j] = observation.obs[i];
                        observation.obs[i] = temp;
                    }
                }
            }
        }
    }

    void addAction(int player, String actions) {
        sigma[player] += actions;
    }

    void setAction(int player, String actions) {
        sigma[player] = actions;
    }

    void addTransition(int from, int to, String actions) {
        delta.add(new Transition(from, to, actions));
    }

    void setObsForPlayer(int player, int state, int observation) {
        observations[player].setObservation(state, observation);
    }

    public ArrayList<Transition> gettransitionfromstate(int state) {
        ArrayList<Transition> result = new ArrayList<>();
        for (Transition edge : delta) {
            if (edge.from == state) {
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
        json.put("stabilises", stabilises);
        json.put("states", statearray);
        json.put("players", players);
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

        public char playermove(int player) {
            return path.charAt(player);
        }


        @Override
        public String toString() {

            return "[" + from + "," + path + "," + to + "]";

        }
    }

    class Observation {
        int[] obs;

        public Observation() {
            obs = new int[states];
        }

        public void setObservation(int state, int observation) {
            obs[state] = observation;
        }

        public int getSize(int state){
            int count = 0;
            for (int i = 0; i < obs.length; i++) {
                if(obs[i] == obs[state]){
                    count++;
                }
            }
            return count;
        }

        // Returns all states which are in the same observation as the given state
        public ArrayList<Integer> getObsInSameState(int state) {
            ArrayList<Integer> result = new ArrayList<>();
            for (int i = 0; i < obs.length; i++) {
                if(obs[state] == obs[i]){
                    result.add(i);
                }
            }
            return result;
        }
        
        public int size() {
            boolean[] counted = new boolean[states];
            int count = 0;
            for (int observation:obs) {
                if(!counted[observation]) {
                    counted[observation] = true;
                    count++;
                }
            }
            return count;
        }

        // gets all states in an observation. State refers to the observation state
        public ArrayList<Integer> getObservation(int state) {
            ArrayList<Integer> result = new ArrayList<>();
            for (int i = 0; i < obs.length; i++) {
                if(state == obs[i]){
                    result.add(i);
                }
            }
            return result;
        }

        boolean isNormalised(){
            boolean[] seen = new boolean[states];
            int count = 0;
            int max = 0;

            for (int i = 0; i < states; i++) {
                if (obs[i] >= states) {
                    return false;
                }

                if (!seen[obs[i]]) {
                    if (obs[i] > max) {
                        max = obs[i];
                    }
                    count++;
                    seen[obs[i]] = true;
                }
            }
            return (max == count - 1);

        }
        //todo: make normailsed method

        @Override
        public String toString() {
            String s = "[";

            int count = 0;
            for (int i = 0; count < states; i++) {
                s += "[";
                for (int j = 0; j < states; j++) {
                    if (i == obs[j]) {
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
