package DBAccess.RealmDB;

import io.realm.RealmObject;

/**
 * Created by Kinsley Kajiva on 1/8/2017.
 */

public class BackUp_ExaminationDB extends RealmObject {


    private String DataString;

    public String getDataString() {return DataString;}

    public void setDataString(String dataString) {DataString = dataString; }


}