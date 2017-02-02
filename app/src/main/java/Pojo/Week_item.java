package Pojo;

/**
 * Created by Kinsley Kajiva on 1/4/2017.
 */

public class Week_item extends ListItem {
    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    private String week;
    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
