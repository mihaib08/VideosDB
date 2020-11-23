package users;

import fileio.UserInputData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class Users {
    /** Create HashMaps username (key) - attribute (value)
     *                 --> attribute -> history -> (name, no_views)
     *                               - favorite videos
     */
    private static HashMap<String, Map<String, Integer>> watchedShows;
    private static HashMap<String, ArrayList<String>> favoriteVideos;

    /**
     * Video ratings per season (if it is a movie --> season(s) : 0)
     *      username (key) - attribute (value)
     *                        --> title (key) - season, rating (value)
     *                                           -> season (key) - rating (value)
     */
    private static HashMap<String,
                   HashMap<String, HashMap<Integer, Double>>> videoRatings;

    /**
     * No. ratings given by each users
     *      user (key) - no. ratings (value)
     */
    private static HashMap<String, Integer> noRatings;

    /**
     * HashMap video (key) - no. views (value)
     */
    private static HashMap<String, Integer> viewsVideo;

    /**
     * HashMap video (key) -> how many users added video as favorite
     */
    private static HashMap<String, Integer> noFavorites;

    /**
     * Query Util
     * HashMap video (key) - seasonNumber, rating(s) (value)
     *     -- video == movie --> seasonNumber == 0
     */
    private static HashMap<String, HashMap<Integer, Ratings>> totalRatings;

    /**
     * Popular Recommendation Util
     * HashMap (String)genre (key) - (VideoViews)obj (key)
     *     --> for each genre, count the total no. views
     *         of videos which have <genre>
     */
    private static HashMap<String, VideoViews> genreVideos;

    private static List<UserInputData> users;

    /** - Private - Constructor */

    private Users() { }

    /** Generate the fields */
    public static void genUtils(final List<UserInputData> usersData) {
        watchedShows = new HashMap<>();
        favoriteVideos = new HashMap<>();
        videoRatings = new HashMap<>();
        noRatings = new HashMap<>();
        viewsVideo = new HashMap<>();
        noFavorites = new HashMap<>();
        totalRatings = new HashMap<>();
        genreVideos = new HashMap<>();
        users = usersData;

        for (UserInputData user : usersData) {
            watchedShows.put(user.getUsername(), user.getHistory());
            favoriteVideos.put(user.getUsername(), user.getFavoriteMovies());

            /*
             * Add the number of views from history
             * to the correspondent videos
             */
            for (String video : user.getHistory().keySet()) {
                viewsVideo.put(video, viewsVideo.getOrDefault(video, 0)
                              + user.getHistory().get(video));
            }

            /*
             * Add the favorite shows to the noFavorites map
             */
            for (String video : user.getFavoriteMovies()) {
                noFavorites.put(video, noFavorites.getOrDefault(video, 0) + 1);
            }
        }
    }

    /** Methods */

    /**
     * 2 - video is already in favorites
     * 1 - video was added
     * 0 - video was NOT added <-> not seen
     */
    public static int addFavorite(final String user, final String video) {
        /* Check if <video> is already in the favoriteVideos of user */
        ArrayList<String> favorites = favoriteVideos.get(user);

        for (String show : favorites) {
            if (video.equals(show)) {
                return 2;
            }
        }

        /* video is not in favorites --> check if it is watched */
        Map<String, Integer> shows = watchedShows.get(user);
        Integer nr = shows.get(video);

        /* video hasn't been watched */
        if (nr == null) {
            return 0;
        }

        /* video was watched --> add it to favorites */
        favorites.add(video);

        /*
         * increment the no. users for favorite video
         */
        noFavorites.put(video, noFavorites.getOrDefault(video, 0) + 1);
        return 1;
    }

    /**
     * "Watch" <video> for user --> update watchedShows
     * Return viewsNumber for video
     */
    public static int watchVideo(final String user, final String video) {
        Map<String, Integer> videos = watchedShows.get(user);
        videos.put(video, videos.getOrDefault(video, 0) + 1);

        /* Add +1 view to viewsVideo */
        viewsVideo.put(video, viewsVideo.getOrDefault(video, 0) + 1);

        return videos.get(video);
    }

    /**
     * Rate <video> --> check if seasonNumber is rated
     *      -- movie -> seasonNumber == 0
     *
     *      >> 0 - is not seen
     *      >> 1 - has already been rated
     *      >> 2 - is newly rated
     */
    public static int rateVideo(final String user, final String video,
                                final Double grade, final int seasonNumber) {
        /* Check if video was seen by user --> is in watchedShows */
        Map<String, Integer> shows = watchedShows.get(user);

        if (shows.get(video) == null) {
            return 0;
        }
        /* video is seen --> check if it's rated */
        HashMap<String, HashMap<Integer, Double>> videos = videoRatings.get(user);

        /* user hasn't rated any videos */
        if (videos == null) {
            noRatings.put(user, 1);

            HashMap<Integer, Double> ratedVideo = new HashMap<>();
            ratedVideo.put(seasonNumber, grade);

            HashMap<String, HashMap<Integer, Double>> ratedShow = new HashMap<>();
            ratedShow.put(video, ratedVideo);

            videoRatings.put(user, ratedShow);

            addRating(video, seasonNumber, grade);

            return 2;
        }

        /* video is not rated */
        if (videos.get(video) == null) {
            noRatings.put(user, noRatings.get(user) + 1);

            HashMap<Integer, Double> ratedVideo = new HashMap<>();
            ratedVideo.put(seasonNumber, grade);
            videos.put(video, ratedVideo);

            addRating(video, seasonNumber, grade);

            return 2;
        }

        HashMap<Integer, Double> ratedSeason = videos.get(video);

        /* season seasonNumber is not rated */
        if (ratedSeason.get(seasonNumber) == null) {
            addRating(video, seasonNumber, grade);

            return 2;
        }

        return 1;
    }

    /**
     * Add the rating to totalRatings HM
     */
    private static void addRating(final String video, final int seasonNumber,
                                  final Double grade) {
        HashMap<Integer, Ratings> seasonRatings = totalRatings.get(video);

        /* no season of video is rated */
        if (seasonRatings == null) {
            Ratings rating = new Ratings(grade);
            HashMap<Integer, Ratings> rat = new HashMap<>();
            rat.put(seasonNumber, rating);

            totalRatings.put(video, rat);
        } else {
            /*
             * check if the seasonNumber
             * was previously rated
             */
            Ratings rating;
            if (seasonRatings.containsKey(seasonNumber)) {
                rating = seasonRatings.get(seasonNumber);

                /* add the new rating */
                Double nr = rating.getSum();
                nr += grade;
                rating.setSum(nr);

                int ct = rating.getCount();
                ct++;
                rating.setCount(ct);

            } else {
                rating = new Ratings(grade);
            }
            seasonRatings.put(seasonNumber, rating);
            totalRatings.put(video, seasonRatings);
        }
    }

    /**
     * Sort noRatings by its values in ascending order
     *     --> use stream()
     *
     * Inspired by : https://www.baeldung.com/java-hashmap-sort
     */
    public static Map<String, Integer> sortNoRatings() {
        Map<String, Integer> res = noRatings.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return res;
    }

    /**
     * Get subscriptionType of user
     */
    public static String getSubscriptionType(final String user) {
        for (UserInputData u : users) {
            if (user.equals(u.getUsername())) {
                return u.getSubscriptionType();
            }
        }
        return null;
    }

    /**
     * For genre and videoName given --> update genreVideos
     */
    public static void addVideoGenre(final String genre, final String video) {
        /* find the total no. views for video */
        int nr;

        nr = viewsVideo.getOrDefault(video, 0);
        VideoViews v;
        if (!genreVideos.containsKey(genre)) {
            /* create a new entry in HM for genre */
            v = new VideoViews();

            ArrayList<String> list = new ArrayList<>();
            list.add(video);

            v.setNum(nr);
            v.setVideos(list);
        } else {
            /* add video */
            v = genreVideos.get(genre);

            int aux = v.getNum();
            aux += nr;
            v.setNum(aux);

            ArrayList<String> list = v.getVideos();
            list.add(video);
            v.setVideos(list);
        }
        genreVideos.put(genre, v);
    }

    /** Getters + Setters */

    public static HashMap<String, Map<String, Integer>> getWatchedShows() {
        return watchedShows;
    }

    public HashMap<String, ArrayList<String>> getFavoriteVideos() {
        return favoriteVideos;
    }

    public static HashMap<String, Integer> getViewsVideo() {
        return viewsVideo;
    }

    public static HashMap<String, Integer> getNoFavorites() {
        return noFavorites;
    }

    public static HashMap<String, HashMap<Integer, Ratings>> getTotalRatings() {
        return totalRatings;
    }

    public static HashMap<String, VideoViews> getGenreVideos() {
        return genreVideos;
    }
}
