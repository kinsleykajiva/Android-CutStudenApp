package mServices;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import kinsleykajiva.co.zw.cutstudentapp.R;

/**
 * Created by Kinsley Kajiva on 12/27/2016.
 */

public class OverlayAlarm extends Service {
    private WindowManager windowManager;
    private RelativeLayout OutterView;
    private ImageView imageView;
    private TextView className, classTime;
    private Button dissmisss;
    private Vibrator vibrator;
    private Ringtone ringtone;
    WindowManager.LayoutParams params;
    String classname = "", classtime = "",Clasday="";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        classname = (String) intent.getExtras().get("className");
        classtime = (String) intent.getExtras().get("ClassTime");
        Clasday= (String) intent.getExtras().get("classDay");
        initObjects();
        windowManager.addView(OutterView, params);
        return START_NOT_STICKY;
    }
    private void initObjects() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 0;
        //
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(/*400*/new long[]{1000, 1000, 1000, 1000, 1000}, 3);
        }
        ///
        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtone = RingtoneManager.getRingtone(getBaseContext(), alert);
        if (ringtone == null) {
            alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(getBaseContext(), alert);
            if (ringtone == null) {
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                ringtone = RingtoneManager.getRingtone(getBaseContext(), alert);
            }
        }
        if (ringtone != null)
            ringtone.play();
        //

        PowerManager pm = (PowerManager) getBaseContext().getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag");
        wakeLock.acquire();
        //
        OutterView = (RelativeLayout) inflater.inflate(R.layout.alarm_overlay, null);
        dissmisss = (Button) OutterView.findViewById(R.id.dissmisss);
        className = (TextView) OutterView.findViewById(R.id.className);

        classTime = (TextView) OutterView.findViewById(R.id.classTime);

        imageView = (ImageView) OutterView.findViewById(R.id.imageView);
        GlideDrawableImageViewTarget imageViewTarget = new GlideDrawableImageViewTarget(((ImageView) OutterView.findViewById(R.id.imageView2)));
        Glide.with(this).load(R.raw.down).into(imageViewTarget);
        className.setText(Clasday+"\n"+classname);
        classTime.setText(classtime);
        dissmisss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                vibrator.cancel();
                ringtone.stop();
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                }
                windowManager.removeView(OutterView);
                stopService(new Intent(getApplicationContext(), OverlayAlarm.class));
            }
        });
    }



}
