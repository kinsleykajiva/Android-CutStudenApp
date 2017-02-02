package kinsleykajiva.co.zw.cutstudentapp;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import BuildsConfigs.BuildsData;
import InterfaceCallBacks.OnUpdateFound_Interface;
import Netwox.NetGetAppUpdate;
import Netwox.NetwoxRequest;
import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import jonathanfinerty.once.Amount;
import jonathanfinerty.once.Once;

import static BuildsConfigs.BuildsData.EXAM_TIME_KEY;
import static BuildsConfigs.BuildsData.isExamTime;

/**
 * Created by kinsley kajiva on 11/8/2016.Zvaganzirwa nakinsley kajiva musiwa 11/8/2016
 */

public class MyApplication extends Application  {
    private static MyApplication singleton;

    public static MyApplication getInstance(){
        return singleton;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        singleton = this;
        CustomActivityOnCrash.install(this);
        Once.initialise(this);
        Realm.init(this);
        Realm.setDefaultConfiguration(
                new RealmConfiguration.Builder().build()
        );
        Fabric.with(this, new Crashlytics());

        Crashlytics.setUserIdentifier(BuildsData.getAndroidDeviceID(this));
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }

    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("user@fabric.io");
        Crashlytics.setUserName("Test User");
    }
public void setExamMark(){

    Once.markDone(EXAM_TIME_KEY);
}



}
