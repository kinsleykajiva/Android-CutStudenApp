package DBAccess.RealmDB;

import io.realm.RealmObject;

/**
 * Created by kinsley kajiva on 11/8/2016.Zvaganzirwa nakinsley kajiva musiwa 11/8/2016
 */

public class NotificationsDB extends RealmObject {
    private int notificationID;
    private String  title;
    private String body;
    private String date_posted;
    private String imageurl;



    public String getImageurl() {
        return imageurl;
    }





    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }


    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getDate_posted() {
        return date_posted;
    }

    public void setDate_posted(String date_posted) {
        this.date_posted = date_posted;
    }
}