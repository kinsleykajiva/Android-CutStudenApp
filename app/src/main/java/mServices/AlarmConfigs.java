package mServices;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Calendar;

import BuildsConfigs.BuildsData;
import DBAccess.RealmDB.ClassesLecture;
import io.realm.Realm;

import static java.util.Calendar.MONDAY;


/**
 * Created by kinsley kajiva on 7/4/2016.Zvaganzirwa nakinsley kajiva musiwa 7/4/2016
 */
public class AlarmConfigs {
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    private BuildsData configs = new BuildsData();

    private Context context;

    public AlarmConfigs(Context context) {
        this.context = context;
        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }


    public void CreateAlarm( int ReqCode/*this reqcode is the same as the database id*/ ) {
        Realm realm = Realm.getDefaultInstance();
        ClassesLecture results  = realm.where(ClassesLecture.class).equalTo("classID",ReqCode).findFirst();
        String classTime=results.getClassStartTime();
        String  className=results.getClassModuleName();
        String classDay=results.getClassDay();

        realm.close();
        int hr = configs.passHoursTime(classTime);
        int mins = configs.passMinsTime(classTime);


        Intent intent = new Intent(context, AlarmWakefulBroadcastReciver.class);
        intent.putExtra("className",className);
        intent.putExtra("classDay",classDay);
        intent.putExtra("classTime",classTime);
        alarmIntent = PendingIntent.getBroadcast(context, ReqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.DAY_OF_WEEK,DaySelect(classDay));
        calendar.set(Calendar.HOUR_OF_DAY, hr);
        calendar.set(Calendar.MINUTE, mins);
        //
        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7/*repeat after 7 days*/, alarmIntent);
        //
        ComponentName receiver = new ComponentName(context, AlarmWakefulBroadcastReciver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

    }


    public void CancelAlarm(Context context, int ReqCode) {
        // If the alarm has been set, cancel it.

        Intent intent = new Intent(context, AlarmWakefulBroadcastReciver.class);
        alarmIntent = PendingIntent.getBroadcast(context, ReqCode, intent, 0);

        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }

        ComponentName receiver = new ComponentName(context, bootReciver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

    private int DaySelect(String mDay){
        if(mDay.equalsIgnoreCase("Monday") ){
            return  2;
        }else if (mDay.equalsIgnoreCase("Tuesday")){
            return  3;
        }else if (mDay.equalsIgnoreCase("Wednesday")){
            return  4;
        }else if (mDay.equalsIgnoreCase("Thursday")){
            return  5;
        }else if (mDay.equalsIgnoreCase("Friday")){
            return 6;
        }else if (mDay.equalsIgnoreCase("Saturday")){
            return  7;
        }else{
            return  1;
        }
    }

}
