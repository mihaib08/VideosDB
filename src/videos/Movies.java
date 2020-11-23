package videos;

import fileio.MovieInputData;
import users.Ratings;
import users.Users;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class
 *     --> operates with movies
 */
public final class Movies {
    /**
     * HashMap year (key) - movies (value)
     */
    private static HashMap<Integer, ArrayList<MovieInputData>> yearMovies;

    private static List<MovieInputData> movies;

    /** - Private - Constructor */
    private Movies() { }

    /** Generate the fields */
    public static void genUtils(final List<MovieInputData> moviesData) {
        yearMovies = new HashMap<>();

        int yr;
        for (MovieInputData m : moviesData) {
            yr = m.getYear();
            if (!yearMovies.containsKey(yr)) {
                ArrayList<MovieInputData> list = new ArrayList<>();
                list.add(m);

                yearMovies.put(yr, list);
            } else {
                ArrayList<MovieInputData> list = yearMovies.get(yr);
                list.add(m);
            }
        }
        Movies.movies = moviesData;
    }

    /** Methods */

    /**
     *  ---------- MOST_VIEWED Util ----------
     * Get the movies produced in <year>, which have <genre>
     *     --> HashMap movieTitle (key) -> no. views (value)
     */
    public static HashMap<String, Integer> listMovies(final Integer year,
                                                      final String genre) {
        HashMap<String, Integer> res;

        /* Check if there are movies produced in year */
        if (!yearMovies.containsKey(year)) {
            return null;
        }

        res = genListMovies(genre, yearMovies.get(year));
        return res;
    }

    /**
     * only genre is given
     */
    public static HashMap<String, Integer> listMovies(final String genre) {
        HashMap<String, Integer> res;
        res = genListMovies(genre, movies);
        return res;
    }

    /**
     * only year is given
     */
    public static HashMap<String, Integer> listMovies(final Integer year) {
        HashMap<String, Integer> res = new HashMap<>();

        if (!yearMovies.containsKey(year)) {
            return null;
        }

        for (MovieInputData movie : yearMovies.get(year)) {
            String title = movie.getTitle();

            HashMap<String, Integer> views = Users.getViewsVideo();
            if (views.containsKey(title)) {
                res.put(movie.getTitle(), views.get(title));
            }
        }
        return res;
    }

    /**
     * no filters given
     */
    public static HashMap<String, Integer> listMovies() {
        HashMap<String, Integer> res = new HashMap<>();

        for (MovieInputData movie : movies) {
            if (Users.getViewsVideo().containsKey(movie.getTitle())) {
                res.put(movie.getTitle(),
                        Users.getViewsVideo().get(movie.getTitle()));
            }
        }
        return res;
    }

    /**
     * Generate a map of movies which have <genre>
     *         -- listMovies util
     */
    private static HashMap<String, Integer> genListMovies(final String genre,
                                                          final List<MovieInputData> list) {
        HashMap<String, Integer> res = new HashMap<>();

        for (MovieInputData movie : list) {
            if (movie.getGenres().contains(genre)) {
                /* Find the no. views */
                String title;
                title = movie.getTitle();

                HashMap<String, Integer> views = Users.getViewsVideo();
                if (views.containsKey(title)) {
                    res.put(movie.getTitle(), views.get(title));
                }
            }
        }
        return res;
    }

    /**
     *  ---------- LONGEST Util ----------
     * Get the list of movies - produced in <year>
     *                        - having <genre>
     */
    public static List<MovieInputData> getYearGenreMovies(final Integer year,
                                                          final String genre) {
        List<MovieInputData> videos = new ArrayList<>();

        if (!yearMovies.containsKey(year)) {
            return null;
        }

        for (MovieInputData v : yearMovies.get(year)) {
            if (v.getGenres().contains(genre)) {
                videos.add(v);
            }
        }
        return videos;
    }

    /** only genre is given */
    public static List<MovieInputData> getGenreMovies(final String genre) {
        List<MovieInputData> videos = new ArrayList<>();

        for (MovieInputData v : movies) {
            if (v.getGenres().contains(genre)) {
                videos.add(v);
            }
        }
        return videos;
    }

    /** only year is given */
    public static List<MovieInputData> getYearMovies(final Integer year) {
        return new ArrayList<>(yearMovies.get(year));
    }

    /**
     * Sort a list of movies by duration
     *     * 2nd criteria : movie titles
     *
     * Inspired by : https://mkyong.com/java8/java-8-how-to-sort-list-with-stream-sorted/
     */
    public static List<String> sortByDuration(final List<MovieInputData> list) {
        List<MovieInputData> videos = new ArrayList<>(list);

        List<MovieInputData> sortedMovies = videos.stream()
                .sorted(Comparator.comparing(MovieInputData::getTitle))
                .sorted(Comparator.comparingInt(MovieInputData::getDuration))
                .collect(Collectors.toList());

        /* Create a list having only the title field */
        List<String> res = new ArrayList<>();
        for (MovieInputData m : sortedMovies) {
            res.add(m.getTitle());
        }
        return res;
    }

    /**
     *  ---------- FAVORITE Util ----------
     * Get the favorite movies filtered by <year> and <genre>
     */
    public static HashMap<String, Integer> getYearGenreFavorites(final Integer year,
                                                                 final String genre) {
        HashMap<String, Integer> res = new HashMap<>();

        if (!yearMovies.containsKey(year)) {
            return null;
        }

        for (MovieInputData v : yearMovies.get(year)) {
            if (v.getGenres().contains(genre)) {
                if (Users.getNoFavorites().containsKey(v.getTitle())) {
                    res.put(v.getTitle(),
                            Users.getNoFavorites().get(v.getTitle()));
                }
            }
        }
        return res;
    }

    /**
     * only genre is given
     */
    public static HashMap<String, Integer> getGenreFavorites(final String genre) {
        HashMap<String, Integer> res = new HashMap<>();

        for (MovieInputData v : movies) {
            if (v.getGenres().contains(genre)) {
                if (Users.getNoFavorites().containsKey(v.getTitle())) {
                    res.put(v.getTitle(),
                            Users.getNoFavorites().get(v.getTitle()));
                }
            }
        }
        return res;
    }

    /**
     * only year is given
     */
    public static HashMap<String, Integer> getYearFavorites(final Integer year) {
        HashMap<String, Integer> res = new HashMap<>();

        if (!yearMovies.containsKey(year)) {
            return null;
        }

        for (MovieInputData v : yearMovies.get(year)) {
            if (Users.getNoFavorites().containsKey(v.getTitle())) {
                res.put(v.getTitle(),
                        Users.getNoFavorites().get(v.getTitle()));
            }
        }
        return res;
    }

    /**
     * No filter is given
     */
    public static HashMap<String, Integer> getFavorites() {
        HashMap<String, Integer> res = new HashMap<>();

        for (MovieInputData v : movies) {
            if (Users.getNoFavorites().containsKey(v.getTitle())) {
                res.put(v.getTitle(),
                        Users.getNoFavorites().get(v.getTitle()));
            }
        }
        return res;
    }

    /**
     * Find the movies which are rated
     *    --> filters : year, genre
     */
    public static HashMap<String, Double> getYearGenreRatings(final Integer year,
                                                              final String genre) {
        HashMap<String, Double> res = new HashMap<>();

        if (!yearMovies.containsKey(year)) {
            return null;
        }

        for (MovieInputData v : yearMovies.get(year)) {
            if (v.getGenres().contains(genre)) {
                if (Users.getTotalRatings().containsKey(v.getTitle())) {
                    Double s = findMeanRatings(v.getTitle());
                    res.put(v.getTitle(), s);
                }
            }
        }
        return res;
    }

    /**
     * only genre is given
     */
    public static HashMap<String, Double> getGenreRatings(final String genre) {
        HashMap<String, Double> res = new HashMap<>();

        for (MovieInputData v : movies) {
            if (v.getGenres().contains(genre)) {
                if (Users.getTotalRatings().containsKey(v.getTitle())) {
                    Double s = findMeanRatings(v.getTitle());
                    res.put(v.getTitle(), s);
                }
            }
        }
        return res;
    }

    /**
     * only year is given
     */
    public static HashMap<String, Double> getYearRatings(final Integer year) {
        HashMap<String, Double> res = new HashMap<>();

        if (!yearMovies.containsKey(year)) {
            return null;
        }

        for (MovieInputData v : yearMovies.get(year)) {
            if (Users.getTotalRatings().containsKey(v.getTitle())) {
                Double s = findMeanRatings(v.getTitle());
                res.put(v.getTitle(), s);
            }
        }
        return res;
    }

    /**
     * no filters given
     */
    public static HashMap<String, Double> getRatings() {
        HashMap<String, Double> res = new HashMap<>();

        for (MovieInputData v : movies) {
            if (Users.getTotalRatings().containsKey(v.getTitle())) {
                Double s = findMeanRatings(v.getTitle());
                res.put(v.getTitle(), s);
            }
        }
        return res;
    }

    /**
     * Find the mean rating of a movie
     * based on its no. ratings and ratingsSum
     */
    public static double findMeanRatings(final String v) {
        /* v wasn't rated */
        if (!Users.getTotalRatings().containsKey(v)) {
            return -1;
        }

        /*
         * movie --> only seasonNumber == 0
         * get the mean from totalRatings
         */
        Ratings ratings = Users.getTotalRatings().get(v).get(0);

        double s = ratings.getSum();
        int nr = ratings.getCount();

        s /= nr;
        return s;
    }

    /**
     * ---- SEARCH Util ----
     * Find all the movies having <genre>
     *      & are not watched by <user>
     */
    public static HashMap<String, Double> searchGenreMovies(final String genre,
                                                            final String user) {
        HashMap<String, Double> res = new HashMap<>();

        /* check if user has watched any shows */
        if (!Users.getWatchedShows().containsKey(user)) {
            res = getGenreRatings(genre);
            return res;
        }

        Map<String, Integer> userShows = Users.getWatchedShows().get(user);
        for (MovieInputData v : movies) {
            if (!userShows.containsKey(v.getTitle()) && v.getGenres().contains(genre)) {
                Double s = findMeanRatings(v.getTitle());
                res.put(v.getTitle(), s);
            }
        }
        return res;
    }

    /** Getters + Setters */

    public static List<MovieInputData> getMovies() {
        return movies;
    }
}
