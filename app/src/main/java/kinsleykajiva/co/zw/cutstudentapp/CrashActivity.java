package kinsleykajiva.co.zw.cutstudentapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cat.ereza.customactivityoncrash.CustomActivityOnCrash;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
 ((TextView) findViewById(R.id.error_details)).setText(
         "For programmers only: \n\n"+
                 CustomActivityOnCrash.getStackTraceFromIntent(getIntent()));

    }

    public void RestartApp(View f){
        final Class<? extends Activity> restartActivityClass = CustomActivityOnCrash.getRestartActivityClassFromIntent(getIntent());
        final CustomActivityOnCrash.EventListener eventListener = CustomActivityOnCrash.getEventListenerFromIntent(getIntent());
        if (restartActivityClass != null) {
            Intent intent = new Intent(CrashActivity.this, restartActivityClass);
            CustomActivityOnCrash.restartApplicationWithIntent(CrashActivity.this, intent, eventListener);
        }else{
            CustomActivityOnCrash.closeApplication(CrashActivity.this, eventListener);
        }
    }




}
