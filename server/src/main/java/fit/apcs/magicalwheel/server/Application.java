package fit.apcs.magicalwheel.server;

import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.server.gameplay.GameLoader;

public final class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class.getName());

    public static void main(String[] args) {
        final var gameLoader = GameLoader.getInstance();
        LOGGER.log(Level.INFO, "Questions {0}", gameLoader.getQuestions());
        for (var i = 0; i < 10; ++i) {
            LOGGER.log(Level.INFO, "Random question {0}", gameLoader.getRandomQuestion());
        }
    }

}
