package actions.queries;

import common.Constants;
import fileio.ActionInputData;
import videos.Serials;

import java.util.HashMap;
import java.util.List;

/**
 * Query Util - objectType == "shows"
 */
public final class QueryShows extends Query {
    public QueryShows(final ActionInputData action) {
        super(action);
    }

    /**
     * Get the specific message depending on the criteria
     *    -- filters - yr, genre
     */
    public String solveShows(final String yr, final String genre) {
        String message;

        /* Check criteria */
        switch (criteria) {
            case Constants.MOST_VIEWED -> {
                HashMap<String, Integer> videos;
                if (yr == null && genre == null) {
                    videos = Serials.listSerials();
                } else if (yr == null) {
                    videos = Serials.listSerials(genre);
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        videos = Serials.listSerials(year);
                    } else {
                        videos = Serials.listSerials(year, genre);
                    }
                }
                message = getMostVideos(videos);
                return message;
            }
            case Constants.LONGEST -> {
                List<String> res;
                if (yr == null && genre == null) {
                    res = Serials.sortByDuration(Serials.getShows());
                } else if (yr == null) {
                    res = Serials.sortByDuration(Serials.getGenreSerials(genre));
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        res = Serials.sortByDuration(Serials.getYearSerials(year));
                    } else {
                        res = Serials.sortByDuration(Serials.getYearGenreSerials(year, genre));
                    }
                }
                message = getLongest(res);
                return message;
            }
            case Constants.FAVORITE -> {
                HashMap<String, Integer> list;

                if (yr == null && genre == null) {
                    list = Serials.getFavorites();
                } else if (yr == null) {
                    list = Serials.getGenreFavorites(genre);
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        list = Serials.getYearFavorites(year);
                    } else {
                        list = Serials.getYearGenreFavorites(year, genre);
                    }
                }
                message = getMostVideos(list);
                return message;
            }
            case Constants.RATINGS -> {
                HashMap<String, Double> list;

                if (yr == null && genre == null) {
                    list = Serials.getRatings();
                } else if (yr == null) {
                    list = Serials.getGenreRatings(genre);
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        list = Serials.getYearRatings(year);
                    } else {
                        list = Serials.getYearGenreRatings(year, genre);
                    }
                }
                message = getMostRated(list);
                return message;
            }
            default -> {
            }
        }
        return null;
    }
}
