package DBAccess.RealmDB;

import io.realm.RealmObject;

/**
 * Created by Kinsley Kajiva on 1/9/2017.
 */

public class NotesDB extends RealmObject {

    private String description;
    private String date;
    private  int ID;



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }


}
