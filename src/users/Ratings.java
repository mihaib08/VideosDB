package users;

/**
 * Keep track of the ratings received by a video
 *     --> count - no. ratings
 *     --> sum - sum of the ratings
 */
public final class Ratings {
    private int count;
    private double sum;

    /** Constructor(s) */

    public Ratings(final double grade) {
        count = 1;
        sum = grade;
    }

    /** Getters + Setters */

    public int getCount() {
        return count;
    }

    void setCount(final int count) {
        this.count = count;
    }

    public double getSum() {
        return sum;
    }

    void setSum(final double sum) {
        this.sum = sum;
    }
}
