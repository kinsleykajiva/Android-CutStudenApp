package mServices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import DBAccess.RealmDB.ClassesLecture;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;


/**
 * Created by kinsley kajiva on 7/10/2016.Zvaganzirwa nakinsley kajiva musiwa 7/10/2016
 */
public class miniWakefulBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        setFirstAlrms(context);

    }
    public  void setFirstAlrms(Context context){

        AlarmConfigs alarm = new AlarmConfigs(context);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<ClassesLecture> results = realm.where(ClassesLecture.class)
                .equalTo("classDay","Moday")
                .findAllSorted("classOrder", Sort.ASCENDING);
                ClassesLecture lc=results.where().findFirst();
        if(!results.isEmpty()){

            alarm.CreateAlarm(lc.getClassID());
        }


        realm.close();
    }
}
