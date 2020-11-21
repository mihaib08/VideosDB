package videos;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class
 */
public final class Videos {
    /** - Private - Constructor */

    private Videos() { }

    /**
     * Sort a HashMap with videos -> keys
     *     --> data type values = int
     *
     *     * 2nd criteria - alphabetical order of the keys
     */
    public static Map<String, Integer> sortValueVideos(final HashMap<String, Integer> videos) {
        Map<String, Integer> res = videos.entrySet()
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
     * Sort a HM with videos -> keys
     *     --> data type values = double
     *
     *     * 2nd criteria - alphabetical order of the keys
     */
    public static Map<String, Double> sortRatingVideos(final HashMap<String, Double> videos) {
        Map<String, Double> res = videos.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .sorted(Map.Entry.comparingByValue())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return res;
    }
}
