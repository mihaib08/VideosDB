package actions.queries;

import common.Constants;
import fileio.ActionInputData;
import videos.Videos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Query {
    protected ActionInputData action;
    protected String objectType;
    protected String sortType;
    protected String criteria;
    protected int number;
    protected List<List<String>> filters;

    /* for coding style - [MagicNumber] */
    private static final int FILTER_AWARDS_INDEX = 3;

    /** Constructor(s) */

    public Query() { }

    public Query(final ActionInputData action) {
        this.action = action;
        this.objectType = action.getObjectType();
        this.sortType = action.getSortType();
        this.criteria = action.getCriteria();
        this.number = action.getNumber();
        this.filters = action.getFilters();
    }

    /** Methods */
    public String solveQuery() {
        String message;

        String yr = filters.get(0).get(0);
        String genre = filters.get(1).get(0);

        switch (objectType) {
            case Constants.USERS -> {
                QueryUsers qUsers = new QueryUsers(action);
                message = qUsers.solveUsers();
                return message;
            }
            case Constants.MOVIES -> {
                QueryMovies qMovies = new QueryMovies(action);
                message = qMovies.solveMovies(yr, genre);
                return message;
            }
            case Constants.SHOWS -> {
                QueryShows qShows = new QueryShows(action);
                message = qShows.solveShows(yr, genre);
                return message;
            }
            case Constants.ACTORS -> {
                List<String> words = filters.get(2);
                List<String> awards = filters.get(FILTER_AWARDS_INDEX);

                QueryActors qActors = new QueryActors(action);
                message = qActors.solveActors(words, awards);

                return message;
            }
            default -> {
            }
        }
        return null;
    }

    /**
     * Get the first <number> entries from a list
     */
    List<String> getFirst(final List<String> list) {
        List<String> res = new ArrayList<>();

        for (int i = 0; i < number; ++i) {
            res.add(list.get(i));
        }
        return res;
    }

    /**
     * Get the message for - MOST_VIEWED
     *                     - FAVORITE
     */
    String getMostVideos(final HashMap<String, Integer> videos) {
        String message;

        if (videos == null) {
            message = "Query result: []";
            return message;
        }

        Map<String, Integer> res = Videos.sortValueVideos(videos);
        List<String> listVideos = new ArrayList<>(res.keySet());

        message = genMessage(listVideos);
        return message;
    }

    /**
     * Criteria : LONGEST
     */
    String getLongest(final List<String> videos) {
        String message;

        if (videos == null) {
            message = "Query result: []";
            return message;
        }

        message = genMessage(videos);
        return message;
    }

    /**
     * Criteria : RATINGS
     */
    String getMostRated(final HashMap<String, Double> videos) {
        String message;

        if (videos == null) {
            message = "Query result: []";
            return message;
        }

        Map<String, Double> res = Videos.sortRatingVideos(videos);
        List<String> listVideos = new ArrayList<>(res.keySet());

        message = genMessage(listVideos);
        return message;
    }

    /**
     * Generate the Query message for a given list
     */
    String genMessage(final List<String> list) {
        String message;

        /* check the required order */
        if (sortType.equals("desc")) {
            Collections.reverse(list);
        }

        if (list.size() <= number) {
            message = "Query result: " + list;
        } else {
            List<String> firstVideos = getFirst(list);
            message = "Query result: " + firstVideos;
        }
        return message;
    }
}
