package fcm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import BuildsConfigs.BuildsData;
import DBAccess.RealmDB.CRUD;
import DBAccess.RealmDB.NotificationsDB;
import io.realm.Realm;
import kinsleykajiva.co.zw.cutstudentapp.MReadPage;
import kinsleykajiva.co.zw.cutstudentapp.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static BuildsConfigs.BuildsData.NOTIFICATION_TITLE_ID;
import static BuildsConfigs.BuildsData.READ_ARTICLE;

/**
 * Created by Kinsley Kajiva on 12/30/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = remoteMessage.getData().get("title");
        int id = Integer.parseInt(remoteMessage.getData().get("id"));
        if (!CRUD.isNotificationExist(title)) {
            getArticle(id);
            sendNotification(this,title,id);
        }

    }

    private void sendNotification(Context context,String title,int id) {
        long[] vibratePattern = new long[]{
                500, 1000
        };

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Intent intent = new Intent(context, MReadPage.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        intent.putExtra(NOTIFICATION_TITLE_ID, id);
        stackBuilder.addParentStack(MReadPage.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent contentIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder   mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(Build.VERSION.SDK_INT>21? R.mipmap.ic_launcher:R.mipmap.ic_launcher)
                .setVibrate(vibratePattern).setSound(uri)
                .setContentTitle(title )
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(title))
                .setPriority(Notification.PRIORITY_HIGH);

        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    private void getArticle(int id) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(READ_ARTICLE + id).build();
        try {
            Response response = client.newCall(request).execute();
            ProcessJsonObject(new JSONObject(response.body().string()));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void ProcessJsonObject(JSONObject json) {

        if (json.length() > 0) {
            try {

                Realm realm = Realm.getDefaultInstance();
                NotificationsDB conx = realm.createObject(NotificationsDB.class);

                        realm.beginTransaction();

                        conx.setTitle(json.getString("title"));
                        conx.setDate_posted(json.getString("date_"));
                        conx.setBody(json.getString("description"));
                        conx.setImageurl(json.getString("image"));
                        realm.commitTransaction();


                    realm.close();


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

}
