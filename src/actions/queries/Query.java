package actions.queries;

import common.Constants;
import fileio.ActionInputData;
import videos.Serials;
import videos.Videos;

import java.util.*;

public class Query {
    protected ActionInputData action;
    protected String objectType;
    protected String sortType;
    protected String criteria;
    protected int number;
    protected List<List<String>> filters;

    /** Constructor(s) */

    public Query() { }

    public Query(final ActionInputData action) {
        this.action = action;
        this.objectType = action.getObjectType();
        this.sortType = action.getSortType();
        this.criteria = action.getCriteria();
        this.number = action.getNumber();
        this.filters = action.getFilters();
    }

    /** Methods */
    public String solveQuery() {
        String message;

        String yr = filters.get(0).get(0);
        String genre = filters.get(1).get(0);

        switch (objectType) {
            case Constants.USERS -> {
                QueryUsers qUsers = new QueryUsers(action);
                message = qUsers.solveUsers();
                return message;
            }
            case Constants.MOVIES -> {
                QueryMovies qMovies = new QueryMovies(action);
                message = qMovies.solveMovies(yr, genre);
                return message;
            }
            case Constants.SHOWS -> {
                /* Check criteria */
                if (criteria.equals(Constants.MOST_VIEWED)) {
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
                } else if (criteria.equals(Constants.LONGEST)) {
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
                } else if (criteria.equals(Constants.FAVORITE)) {
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
                } else if (criteria.equals(Constants.RATINGS)) {
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
            }
            default -> {
            }
        }
        return null;
    }

    /**
     * Get the first number entries from a list
     */
    List<String> getFirst(final List<String> list) {
        List<String> res = new ArrayList<>();

        for (int i = 0; i < number; ++i) {
            res.add(list.get(i));
        }
        return res;
    }

    /**
     * Get the message for - MOST_VIEWED
     *                     - FAVORITE
     */
    String getMostVideos(final HashMap<String, Integer> videos) {
        String message;

        if (videos == null) {
            message = "Query result: []";
            return message;
        }

        Map<String, Integer> res = Videos.sortValueVideos(videos);
        List<String> listVideos = new ArrayList<>(res.keySet());

        message = genMessage(listVideos);
        return message;
    }

    /**
     * Get the first <sortType> longest videos
     *    --> return message
     */
    String getLongest(final List<String> videos) {
        String message;

        if (videos == null) {
            message = "Query result: []";
            return message;
        }

        message = genMessage(videos);
        return message;
    }

    /**
     * Get the message for the RATING
     */
    String getMostRated(final HashMap<String, Double> videos) {
        String message;

        if (videos == null) {
            message = "Query result: []";
            return message;
        }

        Map<String, Double> res = Videos.sortRatingVideos(videos);
        List<String> listVideos = new ArrayList<>(res.keySet());

        message = genMessage(listVideos);
        return message;
    }

    /**
     * Generate the Query message for a given result
     */
    String genMessage(final List<String> list) {
        String message;

        if (sortType.equals("desc")) {
            Collections.reverse(list);
        }

        if (list.size() <= number) {
            message = "Query result: " + list;
        } else {
            List<String> firstVideos = getFirst(list);
            message = "Query result: " + firstVideos;
        }
        return message;
    }
}
