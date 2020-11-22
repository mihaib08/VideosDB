package videos;

import fileio.SerialInputData;

/**
 * Add duration to SerialInputData
 */
public final class Show {
    private final SerialInputData serial;
    private final Integer duration;
    private final String title;

    /** Constructor(s) */

    public Show(final SerialInputData serial, final Integer duration) {
        this.serial = serial;
        this.duration = duration;
        this.title = serial.getTitle();
    }

    /** Getters + Setters */

    public SerialInputData getSerial() {
        return serial;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }
}
