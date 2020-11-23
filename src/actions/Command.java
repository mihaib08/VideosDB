package actions;

import common.Constants;
import fileio.ActionInputData;
import users.Users;

public class Command {
    private final String type;
    private final String username;
    private final String title;
    private final Double grade;
    private final int seasonNumber;

    /** Constructor(s) */

    public Command(final ActionInputData action) {
        this.type = action.getType();
        this.username = action.getUsername();
        this.title = action.getTitle();
        this.grade = action.getGrade();
        this.seasonNumber = action.getSeasonNumber();
    }

    /** Methods */

    public String solveCommand() {
        int res;
        String message;

        switch (type) {
            case Constants.FAVORITE -> {
                res = Users.addFavorite(username, title);

                if (res == 2) {
                    message = "error -> " + title
                            + " is already in favourite list";
                } else if (res == 1) {
                    message = "success -> " + title
                            + " was added as favourite";
                } else {
                    message = "error -> " + title
                            + " is not seen";
                }
                return message;
            }
            case Constants.VIEW -> {
                res = Users.watchVideo(username, title);

                message = "success -> " + title
                        + " was viewed with total views of "
                        + res;
                return message;
            }
            case Constants.RATING -> {
                res = Users.rateVideo(username, title, grade, seasonNumber);

                if (res == 0) {
                    message = "error -> " + title
                            + " is not seen";
                } else if (res == 1) {
                    message = "error -> " + title
                            + " has been already rated";
                } else {
                    message = "success -> " + title
                            + " was rated with "
                            + grade + " by "
                            + username;
                }
                return message;
            }
            default -> {
            }
        }
        return null;
    }
}
