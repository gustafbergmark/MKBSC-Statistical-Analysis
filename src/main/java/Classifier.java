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
}
