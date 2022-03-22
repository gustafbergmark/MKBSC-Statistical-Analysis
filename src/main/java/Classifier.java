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

    //preemptive name
    static boolean hasL0Valens(MAGIIAN game) {
        return game.gettransitionfromstate(game.l0).size() == game.states;
    }
}