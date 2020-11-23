package videos;

import common.Constants;
import entertainment.Season;
import fileio.SerialInputData;
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
 *     --> operates with serials
 */
public final class Serials {
    /**
     * HashMap year (key) - serials (value)
     */
    private static HashMap<Integer, ArrayList<SerialInputData>> yearSerials;

    private static List<SerialInputData> serials;

    private static List<Show> shows;

    /** - Private - Constructor */
    private Serials() { }

    /** Generate the fields */
    public static void genUtils(final List<SerialInputData> serialsData) {
        yearSerials = new HashMap<>();

        int yr;
        for (SerialInputData s : serialsData) {
            yr = s.getYear();
            if (!yearSerials.containsKey(yr)) {
                ArrayList<SerialInputData> list = new ArrayList<>();
                list.add(s);

                yearSerials.put(yr, list);
            } else {
                ArrayList<SerialInputData> list = yearSerials.get(yr);
                list.add(s);
            }
        }
        Serials.serials = serialsData;

        shows = new ArrayList<>();
        Show sh;
        /* Get the duration of an entire serial */
        for (SerialInputData s : serials) {
            int d = 0;
            for (Season season : s.getSeasons()) {
                d += season.getDuration();
            }
            sh = new Show(s, d);
            shows.add(sh);
        }
    }

    /** Methods */

    /**
     * Get the serials produced in year, which have genre
     *     --> HashMap serialTitle (key) -> no. views (value)
     */
    public static HashMap<String, Integer> listSerials(final Integer year,
                                                      final String genre) {
        HashMap<String, Integer> res;

        /* Check if there are serials produced in year */
        if (!yearSerials.containsKey(year)) {
            return null;
        }

        res = genListSerials(genre, yearSerials.get(year));
        return res;
    }

    /**
     * only genre is given
     */
    public static HashMap<String, Integer> listSerials(final String genre) {
        HashMap<String, Integer> res;
        res = genListSerials(genre, serials);
        return res;
    }

    /**
     * only year is given
     */
    public static HashMap<String, Integer> listSerials(final Integer year) {
        HashMap<String, Integer> res = new HashMap<>();

        if (!yearSerials.containsKey(year)) {
            return null;
        }

        for (SerialInputData serial : yearSerials.get(year)) {
            String title = serial.getTitle();

            HashMap<String, Integer> views = Users.getViewsVideo();
            if (views.containsKey(title)) {
                res.put(serial.getTitle(), views.get(title));
            }
        }
        return res;
    }

    /**
     * no filters given
     */
    public static HashMap<String, Integer> listSerials() {
        HashMap<String, Integer> res = new HashMap<>();

        for (SerialInputData serial : serials) {
            if (Users.getViewsVideo().containsKey(serial.getTitle())) {
                res.put(serial.getTitle(),
                        Users.getViewsVideo().get(serial.getTitle()));
            }
        }
        return res;
    }

    /**
     * Generate a map of serials which have <genre>
     *         -- listSerials util
     */
    private static HashMap<String, Integer> genListSerials(final String genre,
                                                           final List<SerialInputData> list) {
        HashMap<String, Integer> res = new HashMap<>();

        for (SerialInputData serial : list) {
            if (serial.getGenres().contains(genre)) {
                /* Find the no. views */
                String title = serial.getTitle();

                HashMap<String, Integer> views = Users.getViewsVideo();
                if (views.containsKey(title)) {
                    res.put(serial.getTitle(), views.get(title));
                }
            }
        }
        return res;
    }

    /**
     * Get a list of serials filtered by <year> & <genre>
     */
    public static List<Show> getYearGenreSerials(final Integer year,
                                                 final String genre) {
        List<Show> res = new ArrayList<>();

        for (Show v : shows) {
            if (v.getSerial().getYear() == year
                    && v.getSerial().getGenres().contains(genre)) {
                res.add(v);
            }
        }
        return res;
    }

    /**
     * Only genre is given
     */
    public static List<Show> getGenreSerials(final String genre) {
        List<Show> res = new ArrayList<>();

        for (Show v : shows) {
            if (v.getSerial().getGenres().contains(genre)) {
                res.add(v);
            }
        }
        return res;
    }

    /**
     * Only year is given
     */
    public static List<Show> getYearSerials(final Integer year) {
        List<Show> res = new ArrayList<>();

        for (Show v : shows) {
            if (v.getSerial().getYear() == year) {
                res.add(v);
            }
        }
        return res;
    }

    /**
     * Sort a list of shows by duration
     *     * 2nd criteria : show titles
     */
    public static List<String> sortByDuration(final List<Show> list) {
        List<Show> videos = new ArrayList<>(list);

        List<Show> sortedShows = videos.stream()
                .sorted(Comparator.comparing(Show::getTitle))
                .sorted(Comparator.comparingInt(Show::getDuration))
                .collect(Collectors.toList());

        /* Extract the title field only */
        List<String> res = new ArrayList<>();
        for (Show s : sortedShows) {
            res.add(s.getTitle());
        }
        return res;
    }

    /**
     * Get favorite serials filtered by <year> & <genre>
     */
    public static HashMap<String, Integer> getYearGenreFavorites(final Integer year,
                                                                 final String genre) {
        HashMap<String, Integer> res = new HashMap<>();

        if (!yearSerials.containsKey(year)) {
            return null;
        }

        for (SerialInputData v : yearSerials.get(year)) {
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

        for (SerialInputData v : serials) {
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

        if (!yearSerials.containsKey(year)) {
            return null;
        }

        for (SerialInputData v : yearSerials.get(year)) {
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

        for (SerialInputData v : serials) {
            if (Users.getNoFavorites().containsKey(v.getTitle())) {
                res.put(v.getTitle(),
                        Users.getNoFavorites().get(v.getTitle()));
            }
        }
        return res;
    }

    /**
     * Get the serials which are rated
     *    --> filters : year, genre
     */
    public static HashMap<String, Double> getYearGenreRatings(final Integer year,
                                                              final String genre) {
        HashMap<String, Double> res = new HashMap<>();

        if (!yearSerials.containsKey(year)) {
            return null;
        }

        for (SerialInputData v : yearSerials.get(year)) {
            if (v.getGenres().contains(genre)) {
                if (Users.getTotalRatings().containsKey(v.getTitle())) {
                    Double s = findMeanRatings(v);
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

        for (SerialInputData v : serials) {
            if (v.getGenres().contains(genre)) {
                if (Users.getTotalRatings().containsKey(v.getTitle())) {
                    Double s = findMeanRatings(v);
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

        if (!yearSerials.containsKey(year)) {
            return null;
        }

        for (SerialInputData v : yearSerials.get(year)) {
            if (Users.getTotalRatings().containsKey(v.getTitle())) {
                Double s = findMeanRatings(v);
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

        for (SerialInputData v : serials) {
            if (Users.getTotalRatings().containsKey(v.getTitle())) {
                Double s = findMeanRatings(v);
                res.put(v.getTitle(), s);
            }
        }
        return res;
    }

    /**
     * Find the mean rating of a serial
     */
    private static double findMeanRatings(final SerialInputData v) {
        /* check if the serial was rated */
        if (!Users.getTotalRatings().containsKey(v.getTitle())) {
            return Constants.ERROR_VALUE;
        }

        HashMap<Integer, Ratings> seasonRatings = Users.getTotalRatings().get(v.getTitle());
        double res = 0;

        for (int season : seasonRatings.keySet()) {
            Ratings ratings = seasonRatings.get(season);

            /* find the mean rating for a season */
            double s = ratings.getSum();
            int nr = ratings.getCount();
            s /= nr;

            res += s;
        }
        /*
         * find how many seasons v has
         */
        int nr = v.getNumberSeason();

        res /= nr;
        return res;
    }

    /**
     * Check if a video <s> is a serial
     *      --> if true, return its meanRating
     *                 -- if it wasn't rated -> -2
     *      --> if false -> -1
     */
    public static double checkSerial(final String s) {
        for (SerialInputData v : serials) {
            if (v.getTitle().equals(s)) {
                double res = findMeanRatings(v);
                return res;
            }
        }
        /* s is not a serial */
        return -1;
    }

    /**
     * ---- SEARCH Util ----
     * Find all the serials which have <genre>
     *      & are not watched by user
     */
    public static HashMap<String, Double> searchGenreSerials(final String genre,
                                                             final String user) {
        /* check if user has watched any shows */
        if (!Users.getWatchedShows().containsKey(user)) {
            return getGenreRatings(genre);
        }

        HashMap<String, Double> res = new HashMap<>();
        Map<String, Integer> userShows = Users.getWatchedShows().get(user);
        for (SerialInputData v : serials) {
            if (v.getGenres().contains(genre)
                && !userShows.containsKey(v.getTitle())) {
                double s = findMeanRatings(v);

                if (s == Constants.ERROR_VALUE) {
                    s = -1;
                }

                res.put(v.getTitle(), s);
            }
        }
        return res;
    }

    /** Getters + Setters */

    public static List<Show> getShows() {
        return shows;
    }
}
