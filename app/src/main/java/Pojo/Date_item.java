package Pojo;

/**
 * Created by Kinsley Kajiva on 1/4/2017.
 */

public class Date_item extends ListItem {
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int getType() {
        return TYPE_DATE;
    }
}
