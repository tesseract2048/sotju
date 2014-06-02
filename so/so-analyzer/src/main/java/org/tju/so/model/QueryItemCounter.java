package org.tju.so.model;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class QueryItemCounter {

    private int position;

    private long click;

    private long impression;

    public QueryItemCounter() {}

    public QueryItemCounter(int position, long click, long impression) {
        setPosition(position);
        setClick(click);
        setImpression(impression);
    }

    /**
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * @param position
     *            the position to set
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * @return the click
     */
    public long getClick() {
        return click;
    }

    /**
     * @param click
     *            the click to set
     */
    public void setClick(long click) {
        this.click = click;
    }

    /**
     * @return the impression
     */
    public long getImpression() {
        return impression;
    }

    /**
     * @param impression
     *            the impression to set
     */
    public void setImpression(long impression) {
        this.impression = impression;
    }

    @Override
    public String toString() {
        return position + "," + click + "," + impression;
    }

    public void incrClickBy(long count) {
        click += count;
    }

    public void incrImpressionBy(long count) {
        impression += count;
    }

    public static QueryItemCounter valueOf(String s) {
        String[] cols = s.split(",");
        return new QueryItemCounter(Integer.valueOf(cols[0]),
                Long.valueOf(cols[1]), Long.valueOf(cols[2]));
    }
}
