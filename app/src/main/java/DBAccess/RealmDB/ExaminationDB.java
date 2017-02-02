package DBAccess.RealmDB;

import io.realm.RealmObject;

/**
 * Created by Kinsley Kajiva on 12/31/2016.
 */



public class ExaminationDB extends RealmObject {

    private String startTime;
    private String endTime;
    private String Venue;
    private String name;
    private String dDate;
    private int ID;
    private boolean isDone,isRemindSet;
    private String week;

    private String examDay;


    public ExaminationDB(String examDay,String startTime, String endTime, String venue, String name, String dDate, int ID, boolean isDone, boolean isRemindSet, String week) {
        this.examDay=examDay;
        this.startTime = startTime;
        this.endTime = endTime;
        Venue = venue;
        this.name = name;
        this.dDate = dDate;
        this.ID = ID;
        this.isDone = isDone;
        this.isRemindSet = isRemindSet;
        this.week = week;
    }

    public ExaminationDB(String examDay,String startTime, String endTime, String venue, String name, String dDate, int ID, boolean isDone, boolean isRemindSet) {
        this.examDay=examDay;
        this.startTime = startTime;
        this.endTime = endTime;
        Venue = venue;
        this.name = name;
        this.dDate = dDate;
        this.ID = ID;
        this.isDone = isDone;
        this.isRemindSet = isRemindSet;
    }

    public ExaminationDB() {
        this.startTime = "09:00";
        this.endTime = "12:20";
        this.Venue = "Unnix";
        this.name = "Cuit 100";
        this.dDate = "12-31-2016";
        this.ID = 0;
        this.isDone = false;
        this.isRemindSet = false;
        this.week="1";
    }
    public String getExamDay() {
        return examDay;
    }

    public void setExamDay(String examDay) {
        this.examDay = examDay;
    }
    public String getdDate() {
        return dDate;
    }

    public void setdDate(String dDate) {
        this.dDate = dDate;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isRemindSet() {
        return isRemindSet;
    }

    public void setRemindSet(boolean remindSet) {
        isRemindSet = remindSet;
    }






}
