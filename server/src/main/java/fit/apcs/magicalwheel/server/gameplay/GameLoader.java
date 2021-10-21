package fit.apcs.magicalwheel.server.gameplay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fit.apcs.magicalwheel.server.enitty.Question;

public final class GameLoader {

    private static final Logger LOGGER = Logger.getLogger(GameLoader.class.getName());
    private static final GameLoader INSTANCE = new GameLoader();

    private final List<Question> questions = new ArrayList<>();

    private GameLoader() {
        loadQuestions("database.txt");
    }

    private void loadQuestions(String path) {
        try {
            final var inputStream = getClass().getClassLoader().getResourceAsStream(path);
            if (inputStream == null) {
                LOGGER.log(Level.SEVERE, "Cannot find resource file {0}", path);
                System.exit(1);
            }
            final var reader = new BufferedReader(new InputStreamReader(inputStream));
            final var numQuestions = loadNumQuestions(reader);
            for (var question = 0; question < numQuestions; ++question) {
                loadNextQuestion(reader);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, String.format("An error occurred while reading file %s", path), ex);
            System.exit(1);
        }
    }

    @SuppressWarnings("MethodMayBeStatic")
    private int loadNumQuestions(BufferedReader reader) throws IOException {
        final var line = reader.readLine();
        if (line == null) {
            return 0;
        }
        return Integer.parseInt(line);
    }

    private void loadNextQuestion(BufferedReader reader) throws IOException {
        final var keyword = reader.readLine();
        if (keyword == null) {
            return;
        }
        final var description = reader.readLine();
        questions.add(new Question(keyword.trim().toUpperCase(), description));
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public static GameLoader getInstance() {
        return INSTANCE;
    }

}
