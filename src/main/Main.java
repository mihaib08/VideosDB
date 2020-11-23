package main;

import actions.Command;
import actions.Recommendation;
import actions.queries.Query;
import actor.Actors;
import checker.Checkstyle;
import checker.Checker;
import common.Constants;
import fileio.ActionInputData;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import users.Users;
import videos.Movies;
import videos.Serials;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The entry point to this homework. It runs the checker that tests your implentation.
 */
public final class Main {
    /**
     * for coding style
     */
    private Main() {
    }

    /**
     * Call the main checker and the coding style checker
     * @param args from command line
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(Constants.TESTS_PATH);
        Path path = Paths.get(Constants.RESULT_PATH);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        File outputDirectory = new File(Constants.RESULT_PATH);

        Checker checker = new Checker();
        checker.deleteFiles(outputDirectory.listFiles());

        for (File file : Objects.requireNonNull(directory.listFiles())) {

            String filepath = Constants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getAbsolutePath(), filepath);
            }
        }

        checker.iterateFiles(Constants.RESULT_PATH, Constants.REF_PATH, Constants.TESTS_PATH);
        Checkstyle test = new Checkstyle();
        test.testCheckstyle();
    }

    /**
     * @param filePath1 for input file
     * @param filePath2 for output file
     * @throws IOException in case of exceptions to reading / writing
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        InputLoader inputLoader = new InputLoader(filePath1);
        Input input = inputLoader.readData();

        Writer fileWriter = new Writer(filePath2);
        JSONArray arrayResult = new JSONArray();

        String field = "----";

        /* Process the data given in input */
        Users.genUtils(input.getUsers());
        Movies.genUtils(input.getMovies());
        Serials.genUtils(input.getSerials());
        Actors.genUtils(input.getActors());

        String message;

        /* Parse the actions array */
        JSONObject obj;
        for (ActionInputData action : input.getCommands()) {
            obj = null;
            switch (action.getActionType()) {
                case Constants.COMMAND -> {
                    Command command = new Command(action);
                    message = command.solveCommand();
                    obj = fileWriter.writeFile(action.getActionId(), field, message);
                }
                case Constants.QUERY -> {
                    Query query = new Query(action);
                    message = query.solveQuery();
                    obj = fileWriter.writeFile(action.getActionId(), field, message);
                }
                case Constants.RECOMMENDATION -> {
                    Recommendation rec = new Recommendation(action,
                            input.getMovies(), input.getSerials());
                    message = rec.solveRecommendation();
                    obj = fileWriter.writeFile(action.getActionId(), field, message);
                }
                default -> {
                }
            }

            arrayResult.add(obj);
        }

        fileWriter.closeJSON(arrayResult);
    }
}
