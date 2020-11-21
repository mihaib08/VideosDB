package users;

import java.util.ArrayList;

/**
 * Popular Recommendation Util
 *     --> for a list of videos
 *         count their total no. views
 */
public final class VideoViews {
    /* counter */
    private int num;

    /* list of videos */
    private ArrayList<String> videos;

    /** Constructor(s) */

    public VideoViews() {
        num = 0;
        videos = new ArrayList<>();
    }

    /** Getters + Setters */

    public int getNum() {
        return num;
    }

    void setNum(final int num) {
        this.num = num;
    }

    public ArrayList<String> getVideos() {
        return videos;
    }

    void setVideos(final ArrayList<String> videos) {
        this.videos = videos;
    }
}
