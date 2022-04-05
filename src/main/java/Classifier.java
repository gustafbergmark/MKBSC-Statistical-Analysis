import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public class Classifier {

    public static boolean hasCycles(MAGIIAN game) {
        boolean[] visited = new boolean[game.states];
        int currentstate = game.l0;
        boolean ret = recursiveCycles(game, visited, currentstate);
        if(game.stabilises == 0 && !ret) {
            //System.out.println(game);
        }
        return ret;
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

    static boolean hasNoDifferentActionDifferentKnowledge(MAGIIAN game) {
        for (int state = 0; state < game.states; state++) {
            ArrayList<MAGIIAN.Transition> transitions = game.gettransitionfromstate(state);
            for (int player = 0; player < game.players; player++) {
                /*ArrayList<MAGIIAN.Transition> transitions = new ArrayList<>();
                ArrayList<Integer> statesinobs = game.observations[player].getObsInSameState(state);
                for (int obsstate : statesinobs) {
                    transitions.addAll(game.gettransitionfromstate(obsstate));
                }*/
                boolean[][] visitedobs = new boolean[game.sigma[player].length()][game.observations[player].size()];
                boolean[][] visitedstates = new boolean[game.sigma[player].length()][game.states];
                int index = 0;
                for (char action:game.sigma[player].toCharArray()) {
                    ArrayList<MAGIIAN.Transition> actiontransitions = new ArrayList<>();
                    for (MAGIIAN.Transition transition : transitions) {
                        if(transition.playermove(player)==action) {
                            actiontransitions.add(transition);
                        }
                    }

                    for (MAGIIAN.Transition transition:actiontransitions) {
                        visitedobs[index][game.observations[player].obs[transition.to]] = true;
                        visitedstates[index][transition.to] = true;
                    }
                    index++;
                }

                for (int i = 1; i < game.sigma[player].length(); i++) {
                    for (int j = 0; j < game.states; j++) {
                        if((visitedstates[0][j] != visitedstates[i][j]) &&
                        visitedobs[0][game.observations[player].obs[j]] && visitedobs[i][game.observations[player].obs[j]]) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    static boolean hasNoDifferentActionDifferentKnowledgeNEW(MAGIIAN game) {
        for (int state = 0; state < game.states; state++) {
            //ArrayList<MAGIIAN.Transition> transitions = game.gettransitionfromstate(state);
            for (int player = 0; player < game.players; player++) {
                ArrayList<MAGIIAN.Transition> transitions = new ArrayList<>();
                ArrayList<Integer> statesinobs = game.observations[player].getObsInSameState(state);
                for (int obsstate : statesinobs) {
                    transitions.addAll(game.gettransitionfromstate(obsstate));
                }
                boolean[][] visitedobs = new boolean[game.sigma[player].length()][game.observations[player].size()];
                boolean[][] visitedstates = new boolean[game.sigma[player].length()][game.states];
                int index = 0;
                for (char action:game.sigma[player].toCharArray()) {
                    ArrayList<MAGIIAN.Transition> actiontransitions = new ArrayList<>();
                    for (MAGIIAN.Transition transition : transitions) {
                        if(transition.playermove(player)==action) {
                            actiontransitions.add(transition);
                        }
                    }

                    for (MAGIIAN.Transition transition:actiontransitions) {
                        visitedobs[index][game.observations[player].obs[transition.to]] = true;
                        visitedstates[index][transition.to] = true;
                    }
                    index++;
                }

                for (int i = 0; i < game.sigma[player].length(); i++) {
                    for (int j = i+1; j < game.sigma[player].length(); j++) {
                        for (int k = 0; k < game.observations[player].size(); k++) {
                            if(visitedobs[i][k] && visitedobs[j][k]) {
                                ArrayList<Integer> obstate = game.observations[player].getObservation(k);
                                boolean overlap = false;
                                boolean difference = false;
                                for (int node:obstate) {
                                    if(visitedstates[i][node] && visitedstates[j][node]) {
                                        overlap = true;
                                    } else if(visitedstates[i][node] != visitedstates[j][node]) {
                                        difference = true;
                                    }
                                }
                                if(overlap && difference) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(game.stabilises == 0) {
            //System.out.println(game);
        }
        return true;
    }

    static boolean hasNoDifferentActionDifferentKnowledgeNEWnew(MAGIIAN game) {
        for (int player = 0; player < game.players; player++) {
            ArrayList<ArrayList<Integer>> possibleknowledge = game.getPlayersActualPossibleKnowledge(player);

            // testing hypothesis
            /*for (int i = 0; i < game.states; i++) {
                ArrayList<Integer> temp = new ArrayList<>();
                temp.add(i);
                possibleknowledge.add(temp);
            }*/

            for (ArrayList<Integer> knowledge:possibleknowledge) {
                ArrayList<MAGIIAN.Transition> transitions = new ArrayList<>();
                for (int obsstate : knowledge) {
                    transitions.addAll(game.gettransitionfromstate(obsstate));
                }

                boolean[][] visitedobs = new boolean[game.sigma[player].length()][game.observations[player].size()];
                boolean[][] visitedstates = new boolean[game.sigma[player].length()][game.states];
                int index = 0;
                for (char action:game.sigma[player].toCharArray()) {
                    ArrayList<MAGIIAN.Transition> actiontransitions = new ArrayList<>();
                    for (MAGIIAN.Transition transition : transitions) {
                        if(transition.playermove(player)==action) {
                            actiontransitions.add(transition);
                        }
                    }

                    for (MAGIIAN.Transition transition:actiontransitions) {
                        visitedobs[index][game.observations[player].obs[transition.to]] = true;
                        visitedstates[index][transition.to] = true;
                    }
                    index++;
                }

                for (int i = 0; i < game.sigma[player].length(); i++) {
                    for (int j = i+1; j < game.sigma[player].length(); j++) {
                        for (int k = 0; k < game.observations[player].size(); k++) {
                            if(visitedobs[i][k] && visitedobs[j][k]) {
                                ArrayList<Integer> obstate = game.observations[player].getObservation(k);
                                boolean overlap = false;
                                boolean difference = false;
                                for (int node:obstate) {
                                    if(visitedstates[i][node] && visitedstates[j][node]) {
                                        overlap = true;
                                    } else if(visitedstates[i][node] != visitedstates[j][node]) {
                                        difference = true;
                                    }
                                }
                                if(overlap && difference) {
                                    return false;
                                }
                            }
                        }
                    }
                }
            }
        }
        if(game.stabilises == 0) {
            //System.out.println(game);
        }
        return true;
    }

    static boolean hasNoDifferentActionDifferentKnowledgeNEWnewNEW(MAGIIAN game) {
        for(int firstplayer = 0; firstplayer < game.players; firstplayer++) {
            //System.out.println("First player: " + firstplayer);
            ArrayList<ArrayList<Integer>> firstplayerpossibleknowledge = game.getPlayersActualPossibleKnowledge(firstplayer);
            for (int player = 0; player < game.players; player++) {
                ArrayList<ArrayList<Integer>> possibleknowledge = game.getPlayersActualPossibleKnowledge(player);
                if(firstplayer == player) {
                    continue;
                }
                for (ArrayList<Integer> firstplayerknowledge:firstplayerpossibleknowledge) {
                    //System.out.println("first player knowledge: " + firstplayerknowledge);
                    int index = 0;
                    boolean[][] visitedobs = new boolean[game.sigma[player].length()*firstplayerknowledge.size()*(int)Math.pow(2, game.states-1)][game.observations[player].size()];
                    boolean[][] visitedstates = new boolean[game.sigma[player].length()*firstplayerknowledge.size()*(int)Math.pow(2, game.states-1)][game.states];
                    for (int state:firstplayerknowledge) {
                        for (ArrayList<Integer> knowledge:possibleknowledge) {
                            if(knowledge.contains(state)) {
                                ArrayList<MAGIIAN.Transition> transitions = new ArrayList<>();
                                for (int obsstate : knowledge) {
                                    transitions.addAll(game.gettransitionfromstate(obsstate));
                                }
                                for (char action:game.sigma[player].toCharArray()) {
                                    ArrayList<MAGIIAN.Transition> actiontransitions = new ArrayList<>();
                                    for (MAGIIAN.Transition transition : transitions) {
                                        if(transition.playermove(player)==action) {
                                            actiontransitions.add(transition);
                                        }
                                    }

                                    for (MAGIIAN.Transition transition:actiontransitions) {
                                        visitedobs[index][game.observations[player].obs[transition.to]] = true;
                                        visitedstates[index][transition.to] = true;
                                    }
                                    index++;
                                }
                            }
                        }
                    }
                    for (int i = 0; i < index; i++) {
                        for (int j = i+1; j < index; j++) {
                            for (int k = 0; k < game.observations[player].size(); k++) {
                                if(visitedobs[i][k] && visitedobs[j][k]) {
                                    ArrayList<Integer> obstate = game.observations[player].getObservation(k);
                                    boolean overlap = false;
                                    boolean difference = false;
                                    for (int node:obstate) {
                                        if(visitedstates[i][node] && visitedstates[j][node]) {
                                            overlap = true;
                                        } else if(visitedstates[i][node] != visitedstates[j][node]) {
                                            difference = true;
                                        }
                                    }
                                    if(overlap && difference) {
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(game.stabilises == 0) {
            //System.out.println(game);
        }
        return true;
    }

    public static boolean hasDeterminismWithCooperation(MAGIIAN game) {
        for (int state = 0; state < game.states; state++) {
            ArrayList<MAGIIAN.Transition> transitions = game.gettransitionfromstate(state);
            for (int player = 0; player < game.players; player++) {
                LinkedList<String> comboactions = Generator.combinedactions(game.sigma[0]);
                boolean[] nondeterministicaction = new boolean[comboactions.size()];
                int index = 0;
                for (String comboaction:comboactions) {
                    ArrayList<MAGIIAN.Transition> cooptransitions = new ArrayList<>();
                    for (MAGIIAN.Transition trans:transitions) {
                        if(trans.path.equals(comboaction)) {
                            cooptransitions.add(trans);
                        }
                    }
                    boolean[] visitedobs = new boolean[game.observations[player].size()];
                    boolean[] visitedstates = new boolean[game.states];
                    for (MAGIIAN.Transition trans:cooptransitions) {
                        if(visitedobs[game.observations[player].obs[trans.to]] && !visitedstates[trans.to]) {
                            nondeterministicaction[index] = true;
                            return false;
                        }
                        visitedobs[game.observations[player].obs[trans.to]] = true;
                        visitedstates[trans.to] = true;
                    }
                    index++;
                }
                boolean flag = true;
                for (int i = 0; i <  nondeterministicaction.length; i++) {
                    if(!nondeterministicaction[i]) {
                        flag = false;
                    }
                }
                if(flag) {
                    return false;
                }
            }
        }
        if(game.stabilises == 0) {
            //System.out.println(game);
        }
        return true;
    }

}