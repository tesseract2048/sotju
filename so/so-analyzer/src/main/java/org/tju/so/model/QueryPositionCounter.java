package org.tju.so.model;

/**
 * @author Tianyi HE <hty0807@gmail.com>
 */
public class QueryPositionCounter {

    private long lastClick;

    private long click;

    public QueryPositionCounter() {}

    public QueryPositionCounter(long lastClick, long click) {
        setLastClick(lastClick);
        setClick(click);
    }

    /**
     * @return the lastClick
     */
    public long getLastClick() {
        return lastClick;
    }

    /**
     * @param lastClick
     *            the lastClick to set
     */
    public void setLastClick(long lastClick) {
        this.lastClick = lastClick;
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

    @Override
    public String toString() {
        return lastClick + "," + click;
    }

    public void incrLastClickBy(long count) {
        lastClick += count;
    }

    public void incrClickBy(long count) {
        click += count;
    }

    public static QueryPositionCounter valueOf(String s) {
        String[] cols = s.split(",");
        return new QueryPositionCounter(Long.valueOf(cols[0]),
                Long.valueOf(cols[1]));
    }
}
