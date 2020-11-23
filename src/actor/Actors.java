package actor;

import common.Constants;
import fileio.ActorInputData;
import utils.Utils;
import videos.Movies;
import videos.Serials;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class
 *     --> process the actors in the database
 *         + create Actors' Utils
 */
public final class Actors {
    /**
     * The list of actors in the db
     */
    private static List<ActorInputData> actors;

    /**
     * HM actorName (key) - total no. awards (value)
     */
    private static HashMap<String, Integer> noAwards;

    /** - Private - Constructor */
    private Actors() { }

    /** Generate the fields */
    public static void genUtils(final List<ActorInputData> actorsData) {
        actors = actorsData;

        noAwards = new HashMap<>();
        for (ActorInputData a : actorsData) {
            for (ActorsAwards award : a.getAwards().keySet()) {
                noAwards.put(a.getName(),
                        noAwards.getOrDefault(a.getName(), 0)
                                + a.getAwards().get(award));
            }
        }
    }

    /**
     * Get the actors who have received the given awards
     */
    public static HashMap<String, Integer> getAwardsActors(final List<String> awards) {
        HashMap<String, Integer> res = new HashMap<>();

        /* no awards filter given */
        if (awards == null) {
            return noAwards;
        }

        /* find the actors who have won the awards given */
        for (ActorInputData a : actors) {
            boolean ok = true;

            for (String award : awards) {
                if (!a.getAwards().containsKey(Utils.stringToAwards(award))) {
                    ok = false;
                    break;
                }
            }

            /* check if the actor a has won all the awards */
            if (ok) {
                res.put(a.getName(), noAwards.get(a.getName()));
            }
        }
        return res;
    }

    /**
     * Sort a map (actorName, (int) value) by value
     */
    public static Map<String, Integer> sortValueActors(final HashMap<String, Integer> list) {
        Map<String, Integer> res = list.entrySet()
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
     * Generate actors' ratings
     *      --> HM actorName (key) -> meanRatingActor (value)
     */
    private static HashMap<String, Double> genMeanRatingActors() {
        /**
         * HM videoName (key) -> meanRating (value)
         *    --> videos which actors played in
         */
        HashMap<String, Double> meanRatings = new HashMap<>();

        HashMap<String, Double> res = new HashMap<>();

        for (ActorInputData a : actors) {
            int ct = 0;
            double s = 0;
            for (String v : a.getFilmography()) {
                if (!meanRatings.containsKey(v)) {
                    /*
                     * check if v is a serial
                     * if true, find its meanRating
                     */
                    double rat = Serials.checkSerial(v);
                    if (rat == Constants.ERROR_VALUE) {
                        /*
                         * v is a SERIAL
                         * and wasn't rated
                         */
                        continue;
                    }
                    if (rat == -1) {
                        /* v is a MOVIE */
                        rat = Movies.findMeanRatings(v);
                        if (rat == -1) {
                            /* v wasn't rated */
                            continue;
                        }
                    }
                    meanRatings.put(v, rat);
                }
                s += meanRatings.get(v);
                ct++;
            }
            if (ct == 0) {
                continue;
            }
            double mean = s / ct;
            res.put(a.getName(), mean);
        }
        return res;
    }

    /**
     * Sort actors by their rating
     *     --> return a list of actors
     */
    public static List<String> sortRatings() {
        HashMap<String, Double> ratingActors = genMeanRatingActors();

        Map<String, Double> res = ratingActors.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        List<String> list = new ArrayList<>(res.keySet());
        return list;
    }

    /**
     * Generate a list of words from a string
     */
    private static List<String> genWordsList(final String s) {
        String[] words = s.split("\\W+");

        List<String> res = Arrays.asList(words);
        res.replaceAll(String::toLowerCase);
        return res;
    }

    /**
     * Look for the given list of words in actors' descriptions
     */
    public static List<String> findWordsActors(final List<String> words) {
        List<String> res = new ArrayList<>();
        for (ActorInputData a : actors) {
            /*
             * generate a list of words
             * of the actor a's description
             */
            List<String> descWords = genWordsList(a.getCareerDescription());

            /* search for each word in descWords */
            boolean ok = true;
            for (String w : words) {
                if (!descWords.contains(w)) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                res.add(a.getName());
            }
        }
        Collections.sort(res);
        return res;
    }
}
