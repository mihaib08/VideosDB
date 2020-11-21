package actions;

import common.Constants;
import fileio.ActionInputData;
import fileio.MovieInputData;
import fileio.SerialInputData;
import users.Users;
import videos.Movies;
import videos.Serials;
import videos.Videos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Recommendation {
    private ActionInputData action;
    private List<MovieInputData> movies;
    private List<SerialInputData> serials;

    private String type;
    private String username;
    private String genre;

    /** Constructor(s) */

    public Recommendation() { }

    public Recommendation(final ActionInputData action,
                          final List<MovieInputData> movies,
                          final List<SerialInputData> serials) {
        this.action = action;
        this.movies = movies;
        this.serials = serials;

        type = action.getType();
        username = action.getUsername();
        genre = action.getGenre();
    }

    /** Methods */

    public String solveRecommendation() {
        String message;
        switch (type) {
            case Constants.STANDARD -> {
                String title = getStandard();
                if (title == null) {
                    message = "StandardRecommendation cannot be applied!";
                } else {
                    message = "StandardRecommendation result: " + title;
                }
                return message;
            }
            case Constants.BEST_UNSEEN -> {
                String title = getBestUnseen();
                if (title == null) {
                    message = "BestRatedUnseenRecommendation cannot be applied!";
                } else {
                    message = "BestRatedUnseenRecommendation result: " + title;
                }
                return message;
            }
            case Constants.POPULAR -> {
                /* check subscriptionType */
                String subType = Users.getSubscriptionType(username);
                if (!subType.equals("PREMIUM")) {
                    message = "PopularRecommendation cannot be applied!";
                    return message;
                }
            }
            case Constants.FAVORITE -> {
                /* check subscriptionType */
                String subType = Users.getSubscriptionType(username);
                if (!subType.equals("PREMIUM")) {
                    message = "FavoriteRecommendation cannot be applied!";
                    return message;
                }
                String title = getFavorite();
                if (title == null) {
                    message = "FavoriteRecommendation cannot be applied!";
                } else {
                    message = "FavoriteRecommendation result: " + title;
                }
                return message;
            }
            case Constants.SEARCH -> {
                /* check subscriptionType */
                String subType = Users.getSubscriptionType(username);
                if (!subType.equals("PREMIUM")) {
                    message = "SearchRecommendation cannot be applied!";
                    return message;
                }

                HashMap<String, Double> genreMovies = Movies.searchGenreMovies(genre, username);
                HashMap<String, Double> genreSerials = Serials.searchGenreSerials(genre, username);

                HashMap<String, Double> res = new HashMap<>();
                res.putAll(genreMovies);
                res.putAll(genreSerials);

                message = getSearch(res);
                return message;
            }
            default -> {
            }
        }
        return null;
    }

    /**
     * Return the title of the first video
     * unseen by the given user
     */
    private String getStandard() {
        Map<String, Integer> videos = Users.getWatchedShows().get(username);

        if (videos == null) {
            if (movies == null) {
                if (serials != null) {
                    return serials.get(0).getTitle();
                }
            } else {
                return movies.get(0).getTitle();
            }
            /* no videos available */
            return null;
        }

        /*
         * Parse through movies and serials
         * and find the first unseen one
         */
        for (MovieInputData v : movies) {
            if (!videos.containsKey(v.getTitle())) {
                return v.getTitle();
            }
        }

        for (SerialInputData v : serials) {
            if (!videos.containsKey(v.getTitle())) {
                return v.getTitle();
            }
        }
        return null;
    }

    /**
     * Generate a map of ratings for Movies&Serials
     *    -- search the video in the sorted map
     */
    private String getBestUnseen() {
        String res;
        Map<String, Integer> videos = Users.getWatchedShows().get(username);

        HashMap<String, Double> movieRatings = Movies.getRatings();
        HashMap<String, Double> serialRatings = Serials.getRatings();

        /* merge the two HMs */
        HashMap<String, Double> listRatings = new HashMap<>();
        listRatings.putAll(serialRatings);
        listRatings.putAll(movieRatings);
        Map<String, Double> sortRatings = sortRatings(listRatings);

        List<String> listVideos = new ArrayList<>(sortRatings.keySet());
        Collections.reverse(listVideos);

        if (videos == null) {
            if (listRatings.size() == 0) {
                res = getStandard();
                return res;
            }
            return listVideos.get(0);
        }

        for (String v : listVideos) {
            if (!videos.containsKey(v)) {
                return v;
            }
        }

        /*
         * all rated videos were already seen by username
         *     --> check the unrated ones
         */
        return getStandard();
    }

    /**
     * Sort a HM with videos -> keys
     *     --> data type values = double
     */
    private static Map<String, Double> sortRatings(final HashMap<String, Double> videos) {
        Map<String, Double> res = videos.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return res;
    }

    /**
     * Parse through the database and
     * find the video which has been added the most to favorites
     */
    private String getFavorite() {
        HashMap<String, Integer> favVideos = Users.getNoFavorites();
        List<String> watchedVideos;
        watchedVideos = new ArrayList<>(Users.getWatchedShows()
                .get(username).keySet());

        /*
         * find the video which has been added the most to Favorites
         * and is the highest in database
         */
        int maxi = 0;
        String res = null;

        for (MovieInputData v : movies) {
            if (!watchedVideos.contains(v.getTitle())
                    && favVideos.containsKey(v.getTitle())) {
                int val = favVideos.get(v.getTitle());
                if (val > maxi) {
                    maxi = val;
                    res = v.getTitle();
                }
            }
        }

        for (SerialInputData v : serials) {
            if (!watchedVideos.contains(v.getTitle())
                    && favVideos.containsKey(v.getTitle())) {
                int val = favVideos.get(v.getTitle());
                if (val > maxi) {
                    maxi = val;
                    res = v.getTitle();
                }
            }
        }
        return res;
    }

    /**
     * Sort a list of videos having the required genre
     * by their ratings in ascending order
     *
     *  --> return the message for Search Recommendation
     */
    private String getSearch(final HashMap<String, Double> videos) {
        Map<String, Double> res = Videos.sortRatingVideos(videos);

        List<String> list = new ArrayList<>(res.keySet());
        String message;

        if (list.size() == 0) {
            message = "SearchRecommendation cannot be applied!";
        } else {
            message = "SearchRecommendation result: " + list;
        }
        return message;
    }
}
