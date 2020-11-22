package actions.queries;

import common.Constants;
import fileio.ActionInputData;
import videos.Movies;

import java.util.HashMap;
import java.util.List;

/**
 * Query Util - objectType == "movies"
 */
public final class QueryMovies extends Query {
    public QueryMovies(final ActionInputData action) {
        super(action);
    }

    /**
     * Get the specific message depending on the criteria
     *    -- filters - yr, genre
     */
    public String solveMovies(final String yr, final String genre) {
        String message;

        /* Check criteria */
        switch (criteria) {
            case Constants.MOST_VIEWED -> {
                HashMap<String, Integer> videos;
                if (yr == null && genre == null) {
                    videos = Movies.listMovies();
                } else if (yr == null) {
                    videos = Movies.listMovies(genre);
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        videos = Movies.listMovies(year);
                    } else {
                        videos = Movies.listMovies(year, genre);
                    }
                }
                message = getMostVideos(videos);
                return message;
            }
            case Constants.LONGEST -> {
                List<String> res;
                if (yr == null && genre == null) {
                    res = Movies.sortByDuration(Movies.getMovies());
                } else if (yr == null) {
                    res = Movies.sortByDuration(Movies.getGenreMovies(genre));
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        res = Movies.sortByDuration(Movies.getYearMovies(year));
                    } else {
                        res = Movies.sortByDuration(Movies.getYearGenreMovies(year, genre));
                    }
                }
                message = getLongest(res);
                return message;
            }
            case Constants.FAVORITE -> {
                HashMap<String, Integer> list;

                if (yr == null && genre == null) {
                    list = Movies.getFavorites();
                } else if (yr == null) {
                    list = Movies.getGenreFavorites(genre);
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        list = Movies.getYearFavorites(year);
                    } else {
                        list = Movies.getYearGenreFavorites(year, genre);
                    }
                }
                message = getMostVideos(list);
                return message;
            }
            case Constants.RATINGS -> {
                HashMap<String, Double> list;

                if (yr == null && genre == null) {
                    list = Movies.getRatings();
                } else if (yr == null) {
                    list = Movies.getGenreRatings(genre);
                } else {
                    Integer year = Integer.parseInt(yr);
                    if (genre == null) {
                        list = Movies.getYearRatings(year);
                    } else {
                        list = Movies.getYearGenreRatings(year, genre);
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
