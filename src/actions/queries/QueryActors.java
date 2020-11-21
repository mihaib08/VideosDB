package actions.queries;

import actor.Actors;
import common.Constants;
import fileio.ActionInputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Query Util - objectType == "actors"
 */
public final class QueryActors extends Query {
    public QueryActors(final ActionInputData action) {
        super(action);
    }

    /**
     * Find the query message for the given filters
     */
    public String solveActors(final List<String> words,
                              final List<String> awards) {
        String message;

        /* Check criteria */
        if (criteria.equals(Constants.AWARDS)) {
            HashMap<String, Integer> listActors = Actors.getAwardsActors(awards);
            Map<String, Integer> list = Actors.sortValueActors(listActors);
            List<String> res = new ArrayList<>(list.keySet());

            if (sortType.equals("desc")) {
                Collections.reverse(res);
            }
            message = "Query result: " + res;
            return message;
        } else if (criteria.equals(Constants.AVERAGE)) {
            /*
             * Generate a list of actors sorted by
             * their rating in ascending order
             */
            List<String> res = Actors.sortRatings();

            message = genMessage(res);
            return message;
        } else if (criteria.equals(Constants.FILTER_DESCRIPTIONS)) {
            words.replaceAll(String::toLowerCase);
            List<String> res = Actors.findWordsActors(words);
            if (sortType.equals("desc")) {
                Collections.reverse(res);
            }

            message = "Query result: " + res;
            return message;
        }
        return null;
    }
}
