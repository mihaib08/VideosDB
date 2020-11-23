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
     *     --> sort the users by the no. ratings they have given
     */
    public String solveUsers() {
        String message;
        Map<String, Integer> res = Users.sortNoRatings();

        List<String> usersList = new ArrayList<>(res.keySet());
        message = genMessage(usersList);

        return message;
    }
}
