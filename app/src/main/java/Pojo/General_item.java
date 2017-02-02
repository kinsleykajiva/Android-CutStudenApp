package Pojo;

import DBAccess.RealmDB.ExaminationDB;

/**
 * Created by Kinsley Kajiva on 1/4/2017.
 */

public class General_item extends ListItem {
    private ExaminationDB pojoArray;

    public ExaminationDB getPojoArray() {
        return pojoArray;
    }

    public void setPojoArray(ExaminationDB pojoArray) {
        this.pojoArray = pojoArray;
    }

    @Override
    public int getType() {
        return TYPE_GENERAL;
    }

}
