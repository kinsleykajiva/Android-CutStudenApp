package mServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import DBAccess.RealmDB.ClassesLecture;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Kinsley Kajiva on 12/27/2016.
 */

public class bootReciver extends BroadcastReceiver {
    AlarmConfigs configs;
    @Override
    public void onReceive(Context context, Intent intent) {
        configs=new AlarmConfigs(context);
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")){
            Realm realm=Realm.getDefaultInstance();
            RealmResults<ClassesLecture> results = realm.where(ClassesLecture.class).equalTo("isReminderSetyet","yes").findAll();
            for (ClassesLecture u : results) {

                configs.CreateAlarm(u.getClassID());

            }
            realm.close();
        }
    }
}
