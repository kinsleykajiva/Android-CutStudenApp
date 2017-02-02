package DBAccess.RealmDB;

import io.realm.RealmObject;

/**
 * Created by Kinsley Kajiva on 1/8/2017.
 */

public class BackUp_ClassLecture extends RealmObject {
    public BackUp_ClassLecture() {
    }

    private String DataString;

    public String getDataString() {return DataString;}

    public void setDataString(String dataString) {DataString = dataString; }


}