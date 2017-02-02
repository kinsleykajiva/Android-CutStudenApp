package mServices;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

import BuildsConfigs.BuildsData;


/**
 * Created by kinsley kajiva on 7/4/2016.Zvaganzirwa nakinsley kajiva musiwa 7/4/2016
 */
public class AlarmWakefulBroadcastReciver extends WakefulBroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        final String ClassName = intent.getStringExtra("className");
        final String ClassTime = intent.getStringExtra("classTime");
        final String ClassDay = intent.getStringExtra("classDay");
        Intent service = new Intent(context, OverlayAlarm.class);

        service.putExtra("className", ClassName);
        service.putExtra("ClassTime", ClassTime);
        service.putExtra("classDay", ClassDay);

        startWakefulService(context, service);

    }
}
