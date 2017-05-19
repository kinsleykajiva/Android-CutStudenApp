package DBAccess.RealmDB;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import BuildsConfigs.BuildsData;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by kinsley kajiva on 11/8/2016.Zvaganzirwa nakinsley kajiva musiwa 11/8/2016
 */

public class CRUD {
    public CRUD() {
    }


    final static SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

    public void writeToDb(String... args) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ClassesLecture lecture = realm.createObject(ClassesLecture.class); // Create a new object

        lecture.setClassDay(args[0]);
        lecture.setClassStartTime(args[1]);
        lecture.setClassEndTime(args[2]);
        lecture.setClassModuleName(args[3]);
        lecture.setClassVenue(args[4]);
        lecture.setClassOrder(Integer.parseInt(args[5]));
        lecture.setIsReminderSetyet(args[6]);
        lecture.setTypeOfClass(args[7]);
        RealmResults<ClassesLecture> results = realm.where(ClassesLecture.class).findAll();
        if (results.isEmpty() && results.size() < 1) {
            final int INIT_ID = 0;
            lecture.setClassID(INIT_ID);
        } else {
            final int ADD_ID = 1;
            int max = results.max("classID").intValue() + ADD_ID;

            lecture.setClassID(max);
        }
        realm.commitTransaction();
        realm.close();
    }

    public static void updateClassDB(String... amnts) {
        /*handling time formats*/
        if (amnts[1].length() > 8) {
            amnts[1] = amnts[1].substring(0, amnts[1].length() - 3);
        }
        if (amnts[2].length() > 8) {
            amnts[2] = amnts[2].substring(0, amnts[2].length() - 3);
        }
        Realm realm = Realm.getDefaultInstance();
        ClassesLecture lecture = realm.where(ClassesLecture.class).equalTo("classID", Integer.parseInt(amnts[8]))
                .findFirst();
        realm.beginTransaction();
        lecture.setClassDay(amnts[0]);
        lecture.setClassStartTime(amnts[1]);
        lecture.setClassEndTime(amnts[2]);
        lecture.setClassModuleName(amnts[3]);
        lecture.setClassVenue(amnts[4]);
        lecture.setClassOrder(Integer.parseInt(amnts[5]));

    /*lecture.setIsReminderSetyet( amnts[6]);*/ // we don't want to change the alarm setting as it has nothing to do with updating the class records
        lecture.setTypeOfClass(amnts[7]);


        realm.commitTransaction();
        realm.close();
    }

    public void UpdateClassReminders(int ID_Object, String update_type) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ClassesLecture lecture_ = realm.where(ClassesLecture.class)
                .equalTo("classID", ID_Object)
                .findFirst();
        if (update_type.equalsIgnoreCase("add")) {
            lecture_.setIsReminderSetyet(new BuildsData().IS_REMINDER_SET[1]);
        } else {
            lecture_.setIsReminderSetyet(new BuildsData().IS_REMINDER_SET[0]);
        }
        realm.commitTransaction();
        realm.close();
    }

    public void DeleteClass(int ID_Object) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ClassesLecture> results = realm.where(ClassesLecture.class)
                .equalTo("classID", ID_Object)
                .findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }
    public static void DeleteAllClasses() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ClassesLecture> results = realm.where(ClassesLecture.class)
                .findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static boolean isClassAvailable(String title) {
        boolean isAvalable = false;
        Realm realm = Realm.getDefaultInstance();
        if (!(realm.where(ClassesLecture.class).equalTo("classModuleName", title).findAll()).isEmpty()) {
            isAvalable = true;
        }
        realm.close();
        return isAvalable;

    }

    public static void CreateClassLectureBackup(String data){
        Realm realm = Realm.getDefaultInstance();

        RealmResults<BackUp_ClassLecture> results = realm.where(BackUp_ClassLecture.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();

        realm.beginTransaction();
        BackUp_ClassLecture lecture = realm.createObject(BackUp_ClassLecture.class);
        lecture.setDataString(data);
        realm.commitTransaction();

        realm.close();
    }
    public static void CreateExamBackup(String data){
        Realm realm = Realm.getDefaultInstance();

        RealmResults<BackUp_ExaminationDB> results = realm.where(BackUp_ExaminationDB.class).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();


        realm.beginTransaction();
        BackUp_ExaminationDB lecture = realm.createObject(BackUp_ExaminationDB.class);
        lecture.setDataString(data);
        realm.commitTransaction();

        realm.close();
    }
    public static boolean isClassLectureBackupEmpty(){
        Realm realm = Realm.getDefaultInstance();
        boolean i= !realm.where(BackUp_ClassLecture.class).findAll().isEmpty();
        realm.close();
        return i;
    }
    public static  String getExamBackup(){

    Realm realm = Realm.getDefaultInstance();
    String returnString=  realm.where(BackUp_ExaminationDB.class).findFirst().getDataString();
    realm.close();

    return returnString;
}
    public static  String getClassLEctureBackup(){

        Realm realm = Realm.getDefaultInstance();
        String returnString=  realm.where(BackUp_ClassLecture.class).findFirst().getDataString();
        realm.close();

        return returnString;
    }
    public static boolean isExamBackupEmpty(){
        Realm realm = Realm.getDefaultInstance();
        boolean i= !realm.where(BackUp_ExaminationDB.class).findAll().isEmpty();
        realm.close();
        return i;
    }

    public static void CreateNote(String cont){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String datae=(month + 1) + "/" +day  + "/" + year;

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        NotesDB le = realm.createObject(NotesDB.class); // Create a new object
        le.setDate(datae);
        le.setDescription(cont);
        RealmResults<NotesDB> results = realm.where(NotesDB.class).findAll();
        if (results.isEmpty() && results.size() < 1) {
            final int INIT_ID = 0;
            le.setID(INIT_ID);
        } else {
            final int ADD_ID = 1;
            int max = results.max("ID").intValue() + ADD_ID;

            le.setID(max);
        }
        realm.commitTransaction();
        realm.close();
    }
    public static String[]  getNote(int row){
        String [] returns={"",""};
        Realm realm = Realm.getDefaultInstance();
        NotesDB rs= realm.where(NotesDB.class).equalTo("ID",row).findFirst();
        returns[0]=rs.getDate();
        returns[1]=rs.getDescription();
        realm.close();
        return returns;
    }
    public static void UpdateNote(int rw,String description){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        NotesDB ls = realm.where(NotesDB.class)
                .equalTo("ID", rw)
                .findFirst();
        ls.setDescription(description);
        realm.commitTransaction();
        realm.close();

    }
    public static void DeleteNote(int rw){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<NotesDB> ls = realm.where(NotesDB.class)
                .equalTo("ID", rw)
                .findAll();
       ls.deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();

    }

    /**
    * @param temp1
    * */
    public static void initWriteExamination(List<ExaminationDB> temp1) { // this list uses the constructor with no week as a parameter
        List<ExaminationDB> temp2 = new ArrayList<>(); //this uses a constructor with week as a parameter
        Collections.sort(temp1, new WeekComparator());
        int week = 0;
        int woy = -1;
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ExaminationDB> results = realm.where(ExaminationDB.class).findAll();
        if (results.isEmpty()) {
            for (ExaminationDB date : temp1) {

                try {
                    Date check = sdf.parse(date.getdDate());
                    if (woy != getWeekOfYear(check)) {
                        woy = getWeekOfYear(check);
                        week++;
                    }
                    temp2.add(new ExaminationDB(date.getExamDay(), date.getStartTime(), date.getEndTime(), date.getVenue(), date.getName(), date.getdDate(), date.getID(), date.isDone(), date.isRemindSet(), week + ""));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(temp2, new WeekComparator());
            if (!temp2.isEmpty()) {
                for (ExaminationDB gt : temp2) {
                    writeExam(gt.getStartTime(), gt.getEndTime(), gt.getVenue(), gt.getName(), gt.getdDate(), gt.getWeek(), gt.getExamDay());
                }
            }
        } else {
            List<ExaminationDB> pojoArray = realm.copyFromRealm(results);
            ExaminationDB tm = temp1.get(0);
            pojoArray.add(new ExaminationDB(tm.getExamDay(), tm.getStartTime(), tm.getEndTime(), tm.getVenue(), tm.getName(), tm.getdDate(),/*this is just dummy value*/100,/*this is just dummy value*/false,/*this is just dummy value*/false,/*this is just dummy value*/"100"));
            Collections.sort(pojoArray, new WeekComparator());
            for (ExaminationDB date : pojoArray) {
                try {
                    Date check = sdf.parse(date.getdDate());
                    if (woy != getWeekOfYear(check)) {
                        woy = getWeekOfYear(check);
                        week++;
                    }

                    temp2.add(new ExaminationDB(date.getExamDay(), date.getStartTime(), date.getEndTime(), date.getVenue(), date.getName(), date.getdDate(), date.getID(), date.isDone(), date.isRemindSet(), week + ""));

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            Collections.sort(temp2, new WeekComparator());
            pojoArray.clear();
            for (ExaminationDB gt : temp2) {
                if (gt.getName().contentEquals(tm.getName()) && gt.getdDate().contentEquals(tm.getdDate())) {

                    pojoArray.add(new ExaminationDB(gt.getExamDay(), gt.getStartTime(), gt.getEndTime(), gt.getVenue(), gt.getName(), gt.getdDate(), 0/*is dummy*/, gt.isDone()/*is dummy*/, gt.isRemindSet()/*is dummy*/, gt.getWeek()));
                    break;
                }
            }
            ExaminationDB date = pojoArray.get(0);
            writeExam(date.getStartTime(), date.getEndTime(), date.getVenue(), date.getName(), date.getdDate(), date.getWeek(), date.getExamDay());

        }
        realm.close();


    }

    public static class WeekComparator implements Comparator<ExaminationDB> {


        @Override
        public int compare(ExaminationDB o1, ExaminationDB o2) {
            int result = 1;
            try {
                Date lhsDate = sdf.parse(o1.getdDate());
                Date rhsDate = sdf.parse(o2.getdDate());
                result = getWeekOfYear(lhsDate) - getWeekOfYear(rhsDate);
                if (result == 0) {
                    result = lhsDate.compareTo(rhsDate);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    protected static int getWeekOfYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }

    public static boolean isExamAlreadySaved(String title) {
        Realm realm = Realm.getDefaultInstance();
        boolean exists = !realm.where(ExaminationDB.class).equalTo("name", title).findAll().isEmpty();
        realm.close();
        return exists;
    }
    public static boolean isClassLectureDBEmpty(){
        Realm realm = Realm.getDefaultInstance();
        boolean i= !realm.where(ClassesLecture.class).findAll().isEmpty();
        realm.close();
        return i;
    }
    public static boolean isExamDBEmpty(){
        Realm realm = Realm.getDefaultInstance();
        boolean i= !realm.where(ExaminationDB.class).findAll().isEmpty();
        realm.close();
        return i;
    }

    public static void writeExam(String... r) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        ExaminationDB rw = realm.createObject(ExaminationDB.class); // Create a new object
        RealmResults<ExaminationDB> results = realm.where(ExaminationDB.class).findAll();
        rw.setStartTime(r[0]);
        rw.setEndTime(r[1]);
        rw.setVenue(r[2]);
        rw.setName(r[3]);
        rw.setdDate(r[4]);
        rw.setWeek(r[5]);
        rw.setExamDay(r[6]);
        rw.setDone(false);
        rw.setRemindSet(false);
        if (results.isEmpty() && results.size() < 1) {
            final int INIT_ID = 0;
            rw.setID(INIT_ID);
        } else {
            final int ADD_ID = 1;
            int max = results.max("ID").intValue() + ADD_ID;
            rw.setID(max);
        }
        realm.commitTransaction();

        realm.close();

    }

    public static void DeleteAllExams() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        realm.where(ExaminationDB.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static void UpdateExam(int id,String ... r){

        Realm realm = Realm.getDefaultInstance();
        ExaminationDB rw = realm.where(ExaminationDB.class).equalTo("ID", id)
                .findFirst();
        realm.beginTransaction();
        rw.setStartTime(r[0]);
        rw.setEndTime(r[1]);
        rw.setVenue(r[2]);
        rw.setName(r[3]);
        rw.setdDate(r[4]);
        rw.setWeek(r[5]);
        rw.setExamDay(r[6]);

        realm.commitTransaction();
        realm.close();

    }

    public static boolean isNotificationExist(String title) {
        boolean returnVal = false;
        Realm realm = Realm.getDefaultInstance();

        RealmResults<NotificationsDB> results = realm.where(NotificationsDB.class).equalTo("title", title).findAll();
        if (!results.isEmpty()) {
            returnVal = true;
        }
        realm.close();
        return returnVal;
    }

    public void writeNotificationDB(String... args) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        NotificationsDB rw = realm.createObject(NotificationsDB.class);
        RealmResults<NotificationsDB> results = realm.where(NotificationsDB.class).findAll();
        rw.setTitle(args[0]);
        rw.setBody(args[1]);
        rw.setDate_posted(args[2]);


        if (results.isEmpty() && results.size() < 1) {
            final int INIT_ID = 0;
            rw.setNotificationID(INIT_ID);
        } else {
            final int ADD_ID = 1;
            int max = results.max("notificationID").intValue() + ADD_ID;
            rw.setNotificationID(max);
        }


        realm.commitTransaction();
        realm.close();
    }


}
