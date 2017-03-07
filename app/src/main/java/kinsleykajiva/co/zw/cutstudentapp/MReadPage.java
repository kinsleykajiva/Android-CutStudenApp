package kinsleykajiva.co.zw.cutstudentapp;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import DBAccess.RealmDB.NotificationsDB;
import io.realm.Realm;
import io.realm.RealmResults;

import static BuildsConfigs.BuildsData.NOTIFICATION_TITLE_ID;

public class MReadPage extends AppCompatActivity {
    private  static int RowId;
    Realm realm;
    NotificationsDB results;
    private TextView txttitle,txtDate,txtbody;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getIntent().hasExtra(NOTIFICATION_TITLE_ID)){
            RowId= Integer.parseInt(getIntent().getExtras().getString(NOTIFICATION_TITLE_ID));
        }else{
            RowId= Integer.parseInt(getIntent().getExtras().getString("rowId"));
        }

        setContentView(R.layout.activity_mread_page);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm=Realm.getDefaultInstance();
        results = realm.where(NotificationsDB.class).equalTo("notificationID",RowId).findFirst();
        initViews();
        settingViewsValues();




    }

    private void settingViewsValues() {
        txttitle.setText(results.getTitle());
        txtDate.setText(results.getDate_posted());

        if (Build.VERSION.SDK_INT >= 24) {
            txtbody.setText(Html.fromHtml(results.getBody(), Html.FROM_HTML_MODE_LEGACY));
        } else {
            txtbody.setText(Html.fromHtml(results.getBody()));
        }



    }

    private void initViews() {
        txttitle=(TextView)  findViewById(R.id.txttitle);
        txtDate=(TextView)  findViewById(R.id.txtDate);
        txtbody=(TextView)  findViewById(R.id.txtbody);
        //
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(realm!=null)
        realm.close();
    }
}
