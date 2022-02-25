import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MAGIIANTest {
    MAGIIAN game;
    @BeforeEach
    void setUp() {
        game = new MAGIIAN(3,0,2);
    }

    @Test
    void testToString() {
        //Adding actions
        game.setAction(0, "wp");
        game.setAction(1, "wp");

//Adding transitions
        game.addTransition(0,0,"pp");
        game.addTransition(0,0,"ww");

        game.addTransition(0,1,"wp");
        game.addTransition(0,2,"pw");

        game.addTransition(1,1,"pp");
        game.addTransition(1,1,"ww");

        game.addTransition(1,2,"wp");
        game.addTransition(1,0,"pw");

        game.addTransition(2,2,"pp");
        game.addTransition(2,2,"ww");

        game.addTransition(2,0,"wp");
        game.addTransition(2,1,"pw");

        game.setObsForPlayer(0,0,0);
        game.setObsForPlayer(0,1,0);
        game.setObsForPlayer(0,2,1);

        game.setObsForPlayer(1,0,0);
        game.setObsForPlayer(1,2,0);
        game.setObsForPlayer(1,1,1);

        System.out.println(game.toString());
    }
}