import java.util.ArrayList;

public class Classifier {

    public static boolean hasCycles(MAGIIAN game) {
        boolean[] visited = new boolean[game.states];
        int currentstate = game.l0;
        return recursiveCycles(game, visited, currentstate);
    }

    private static boolean recursiveCycles(MAGIIAN game, boolean[] visited, int currentstate) {
        if (visited[currentstate]) {
            return true;
        }
        visited[currentstate] = true;
        for (MAGIIAN.Transition edge : game.gettransitionfromstate(currentstate)) {
            if (edge.from != edge.to) {
                boolean result = recursiveCycles(game, visited, edge.to);
                if (result) {
                    return true;
                }
            }
        }
        visited[currentstate] = false;
        return false;
    }


    static boolean hasOverlapSubsetOnly(MAGIIAN game) {
        return hasOverlapCombined(game) == 1;
    }

    static boolean hasOverlapNoSubsetOnly(MAGIIAN game) {
        return hasOverlapCombined(game) == 2;
    }

    static boolean hasOverlapBoth(MAGIIAN game) {
        return hasOverlapCombined(game) == 3;
    }

    static boolean hasNoOverlap(MAGIIAN game) {
        return hasOverlapCombined(game) == 0;
    }

    static boolean hasOverlapSubset(MAGIIAN game) {
        //General approach for n players
        for (int i = 0; i < game.players; i++) {
            for (int j = i + 1; j < game.players; j++) {
                for (int k = 0; k < game.states; k++) {
                    if (game.observations[i].getSize(k) > 1 && game.observations[j].getSize(k) > 1) { //observation overlap found
                        if(game.observations[i].getObsInSameState(k).containsAll(game.observations[j].getObsInSameState(k))
                                || game.observations[j].getObsInSameState(k).containsAll(game.observations[i].getObsInSameState(k))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    static public int hasOverlapCombined(MAGIIAN game) {
        boolean hasOverlapNoSubset = false;
        //General approach for n players
        for (int i = 0; i < game.players; i++) {
            for (int j = i + 1; j < game.players; j++) {
                for (int k = 0; k < game.states; k++) {
                    if (game.observations[i].getSize(k) > 1 && game.observations[j].getSize(k) > 1) { //observation overlap found
                        if(!game.observations[i].getObsInSameState(k).containsAll(game.observations[j].getObsInSameState(k))
                                && !game.observations[j].getObsInSameState(k).containsAll(game.observations[i].getObsInSameState(k))) {
                            hasOverlapNoSubset = true;
                        }
                    }
                }
            }
        }

        boolean hasOverlapSubset = false;
        //General approach for n players
        for (int i = 0; i < game.players; i++) {
            for (int j = i + 1; j < game.players; j++) {
                for (int k = 0; k < game.states; k++) {
                    if (game.observations[i].getSize(k) > 1 && game.observations[j].getSize(k) > 1) { //observation overlap found
                        if(game.observations[i].getObsInSameState(k).containsAll(game.observations[j].getObsInSameState(k))
                                || game.observations[j].getObsInSameState(k).containsAll(game.observations[i].getObsInSameState(k))) {
                            hasOverlapSubset = true;
                        }
                    }
                }
            }
        }
        if(hasOverlapNoSubset && hasOverlapSubset) {
            return 3;
        } else if(hasOverlapNoSubset) {
            return 2;
        } else if(hasOverlapSubset) {
            return 1;
        } else {
            return 0;
        }
    }

    static boolean hasOverlapNoSubset(MAGIIAN game) {
        //General approach for n players
        for (int i = 0; i < game.players; i++) {
            for (int j = i + 1; j < game.players; j++) {
                for (int k = 0; k < game.states; k++) {
                    if (game.observations[i].getSize(k) > 1 && game.observations[j].getSize(k) > 1) { //observation overlap found
                        if(!game.observations[i].getObsInSameState(k).containsAll(game.observations[j].getObsInSameState(k))
                                && !game.observations[j].getObsInSameState(k).containsAll(game.observations[i].getObsInSameState(k))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    static boolean hasNature(MAGIIAN game) {
        for (int i = 0; i < game.states; i++) {
            ArrayList<MAGIIAN.Transition> edges = game.gettransitionfromstate(i);
            for (int j = 0; j < edges.size(); j++) {
                for (int k = j+1; k < edges.size(); k++) {
                    if(edges.get(j).path.equals(edges.get(k).path)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // WFO = WellFormedObservations
    // All nodes in an observation state are reachable from any other observations state that has atleast
    // one transition into said observation state
    static boolean hasWFO(MAGIIAN game) {
        for (MAGIIAN.Observation playerObs:game.observations) {

            for (int j = 0; j < playerObs.size(); j++) {
                ArrayList<Integer> states = playerObs.getObservation(j);
                boolean[] reachablestates = new boolean[game.states];
                for (int state:states) {
                    for (MAGIIAN.Transition edge:game.gettransitionfromstate(state)) {
                        reachablestates[edge.to] = true;
                    }
                }
                for (int k = 0; k < game.states; k++) {
                    if(reachablestates[k]) {
                        ArrayList<Integer> sameObs = playerObs.getObsInSameState(k);
                        for (int state:sameObs) {
                            if(!reachablestates[state]) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    static boolean hasBothPlayerUncertain(MAGIIAN game) {
        for (int state = 0; state < game.states; state++) {
            boolean[] totalconfusion = new boolean[game.players];
            //ArrayList<MAGIIAN.Transition> transitions = game.gettransitionfromstate(state);
            for (int player = 0; player < game.players; player++) {
                ArrayList<MAGIIAN.Transition> transitions = new ArrayList<>();
                ArrayList<Integer> statesinobs = game.observations[player].getObsInSameState(state);
                for (int obsstate : statesinobs) {
                    transitions.addAll(game.gettransitionfromstate(obsstate));
                }
                for (char action:game.sigma[player].toCharArray()) {
                    ArrayList<MAGIIAN.Transition> actiontransitions = new ArrayList<>();
                    for (MAGIIAN.Transition transition : transitions) {
                        if(transition.playermove(player)==action) {
                            actiontransitions.add(transition);
                        }
                    }
                    boolean[] visitedobs = new boolean[game.observations[player].size()];
                    boolean[] visitedstates = new boolean[game.states];
                    for (MAGIIAN.Transition transition:actiontransitions) {
                        if(visitedobs[game.observations[player].obs[transition.to]] && !visitedstates[transition.to]) {
                            totalconfusion[player] = true;
                        }
                        visitedobs[game.observations[player].obs[transition.to]] = true;
                        visitedstates[transition.to] = true;
                    }
                }
            }
            int confusioncount = 0;
            for (int i = 0; i < game.players; i++) {
                if(totalconfusion[i]) {
                    confusioncount++;
                }
            }
            if(confusioncount>1) {
                return true;
            }
        }
        return false;
    }
}