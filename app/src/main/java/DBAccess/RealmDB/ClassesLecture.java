package DBAccess.RealmDB;

import java.util.Date;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by kinsley kajiva on 11/8/2016.Zvaganzirwa nakinsley kajiva musiwa 11/8/2016
 */



public class ClassesLecture extends RealmObject  {

    private String classStartTime;
    private String classEndTime;
    private String classVenue;
    private String classModuleName;
    private String classDay;
    private String TypeOfClass;
    private int classID;
    private int classOrder;







    public String getTypeOfClass() {
        return TypeOfClass;
    }

    public void setTypeOfClass(String typeOfClass) {
        TypeOfClass = typeOfClass;
    }

    //if it is  0==tutorial  or 1 ==lecture
    public int getClassOrder() {
        return classOrder;
    }

    public void setClassOrder(int classOrder) {
        this.classOrder = classOrder;
    }

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public String getIsReminderSetyet() {
        return isReminderSetyet;
    }

    public void setIsReminderSetyet(String isReminderSetyet) {
        this.isReminderSetyet = isReminderSetyet;
    }

    private String isReminderSetyet;

    public String getClassStartTime() {
        return classStartTime;
    }

    public void setClassStartTime(String classStartTime) {
        this.classStartTime = classStartTime;
    }

    public String getClassEndTime() {
        return classEndTime;
    }

    public void setClassEndTime(String classEndTime) {
        this.classEndTime = classEndTime;
    }

    public String getClassVenue() {
        return classVenue;
    }

    public void setClassVenue(String classVenue) {
        this.classVenue = classVenue;
    }

    public String getClassModuleName() {
        return classModuleName;
    }

    public void setClassModuleName(String classModuleName) {
        this.classModuleName = classModuleName;
    }

    public String getClassDay() {
        return classDay;
    }

    public void setClassDay(String classDay) {
        this.classDay = classDay;
    }



}