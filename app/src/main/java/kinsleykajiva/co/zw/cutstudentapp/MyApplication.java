package kinsleykajiva.co.zw.cutstudentapp;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import jonathanfinerty.once.Once;

import static BuildsConfigs.BuildsData.EXAM_TIME_KEY;

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
                new RealmConfiguration
                        .Builder()
                        .deleteRealmIfMigrationNeeded()
                        .build()
        );
       /* Fabric.with(this, new Crashlytics());
        Crashlytics.setUserIdentifier(BuildsData.getAndroidDeviceID(this));*/
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }


public void setExamMark(){

    Once.markDone(EXAM_TIME_KEY);
}



}
