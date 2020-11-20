package actions.queries;

import fileio.ActionInputData;
import users.Users;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Query Util - objectType == "users"
 */
public final class QueryUsers extends Query {

    public QueryUsers(final ActionInputData action) {
        super(action);
    }

    /**
     * Get the specific message of the query
     */
    public String solveUsers() {
        String message;
        Map<String, Integer> res = Users.sortNoRatings();

        /* Check the required order */
        List<String> usersList = new ArrayList<>(res.keySet());
        message = genMessage(usersList);

        return message;
    }
}
