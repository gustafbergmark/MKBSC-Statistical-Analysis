public class Classifier {

    public static boolean hasCycles(MAGIIAN game) {
        boolean[] visited = new boolean[game.states];
        int currentstate = game.l0;
        return recursiveCycles(game, visited, currentstate);
    }

    private static boolean recursiveCycles(MAGIIAN game, boolean[] visited, int currentstate) {
        if(visited[currentstate]) {
            return true;
        }
        visited[currentstate] = true;
        for (MAGIIAN.Transition edge:game.gettransitionfromstate(currentstate)) {
            if(edge.from != edge.to) {
                boolean result = recursiveCycles(game, visited, edge.to);
                if(result) {
                    return true;
                }
            }
        }
        visited[currentstate] = false;
        return false;
    }


    static boolean hasOverlap(MAGIIAN game) {
        /* Working for 2 players only ?
        Observation p1 = game.observations[0];
        Observation p2 = game.observations[1];

        for (int i = 0; i < game.states; i++) {
            if(p1.getSize(i) > 1 && p2.getSize(j) > 1){
            return true;
            }
        }
        return false;
        */

        //General approach for n players
        for (int i = 0; i < game.players ; i++) {
            for (int j = i+1; j < game.players; j++) {

                for (int k = 0; k < game.states; k++) {
                        if(game.observations[i].getSize(k) > 1 && game.observations[j].getSize(k) > 1) { //observation overlap found
                            return true;
                        }
                }
            }
        }
        return false;
    }
}