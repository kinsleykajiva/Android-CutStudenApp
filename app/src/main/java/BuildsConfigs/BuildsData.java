package BuildsConfigs;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import Messages.NifftyDialogs;
import kinsleykajiva.co.zw.cutstudentapp.MainActivity;
import kinsleykajiva.co.zw.cutstudentapp.R;


/**
 * Created by kinsley kajiva on 11/8/2016.Zvaganzirwa nakinsley kajiva musiwa 11/8/2016
 *
 * Utility class
 */

public class BuildsData {
    public static final String COUNT_APP_USED = "kinsleykajiva.co.zw.cutstudentapp.BuildsConfigs.Usage";
    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD = 1234;
    public static final String APP_FOLDER = "CutAppBackup";
    public static final String APP_BACKUP_CLASSES_TXT = APP_FOLDER+"/cut_class_cache.txt";

    public static int OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG = 5678;
    public static boolean IsStillUnreadNotification = false, isWeekEnd = false;
    public final static String NOTIFICATION_TITLE_ID = "kinsleykajiva.co.zw.cutstudentapp.BuildsConfigs.ID";
    public static String WEEK_DAY = "", WEEK_DAY_SELECTED = "";
    public static boolean IS_INSTALL_FROM_DRAWER = false,JustSetRenamedFragNamings=false;
    public static boolean isExamTime = false;
    public static boolean ExamLoadedFromFragment = false,JustClearedAllClassesDb=false,JustClearedAllExamsDb,JustRemovedTAB_SUNDAY=false,JustRemovedTAB_SARTARDAY=false;
    public static String EXAM_TIME_KEY = "kinsleykajiva.co.zw.cutstudentapp.BuildsConfigs.examtrigger";
    public static final String TYPE_OF_CLASS[] = {"Tutorial", "Lecture"};

    public String TIMES_DAYS[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
    public static String TIMES_DAYS_FULL[] = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday","Saturday","Sunday"};
    public static String TIMES_DAYS_SHORT[] = {"Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun"};
    public static String WEEKEND_DAYS[] = {"Saturday", "Sunday"};

    public String IS_REMINDER_SET[] = {"no", "yes"};

    public String[] TUTORIAL_CLASS_STATE = {"tutorial.", "tutorial started.", "tutorial almost done."};
    public String[] LECTURE_CLASS_STATE = {"lecture.", "lecture started.", "lecture almost done."};

    public String[] LEVELS = {"Select", "1.1", "1.2", "2.1", "2.1", "2.2", "4.1", "4.2"};

    public static String TOKEN_SAVE = "http://www.zimcycbers.co.zw/cutapp/register.php";
    public static String READ_ARTICLE = "http://www.zimcycbers.co.zw/cutapp/readarticle.php?id=";

    public static Drawable getResourceDrawable(Context context, int icon) {
        return ContextCompat.getDrawable(context, icon);
    }

    public static int getColor(Context context, int id) {
        if (Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public String TIMES_PERIOD_START[] = {"07:00:00", "09:15:00", "11:30:00", "13:45:00", "16:00:00", "18:15:00"};//17:00:00
    public String TIMES_PERIOD_END[] = {"09:00:00", "11:15:00", "13:30:00", "15:45:00", "18:00:00", "20:15:00"};
    public int CLASS_ORDER[] = {1, 2, 3, 4, 5, 6, 7};

    public static String TIMES_PERIOD_START_copy[] = {"07:00:00", "09:15:00", "11:30:00", "13:45:00", "16:00:00", "18:15:00"};//17:00:00
    public static String TIMES_PERIOD_END_copy[] = {"09:00:00", "11:15:00", "13:30:00", "15:45:00", "18:00:00", "20:15:00"};

    private SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");







    public static String incrementTime(String mytime) {
        String NewtimeIncreaseByOneHour = "";
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new SimpleDateFormat("HH:mm:ss").parse(mytime));
            now.add(Calendar.HOUR, 1);
            NewtimeIncreaseByOneHour = new SimpleDateFormat("HH:mm:ss").format(now.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return NewtimeIncreaseByOneHour;

    }
    public static int RandomInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
    public static String incrementTime(String mytime, int byHowMany_Hours) {
        String NewtimeIncreaseByOneHour = "";
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new SimpleDateFormat("HH:mm:ss").parse(mytime));
            now.add(Calendar.HOUR, byHowMany_Hours == 0 ? 1 : byHowMany_Hours);
            NewtimeIncreaseByOneHour = new SimpleDateFormat("HH:mm:ss").format(now.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return NewtimeIncreaseByOneHour;

    }

    public static int isAtposition(String time) {
        int i;
        boolean hasFound = false;
        for (i = 0; i < TIMES_PERIOD_START_copy.length; i++) {
            if (isTimeWith_in_Interval(time, TIMES_PERIOD_END_copy[i], TIMES_PERIOD_START_copy[i])) {
                hasFound = true;
                break;
            }

        }

        return hasFound ? i : -1;
    }
/**
 * <p>
 *     will only restart the app by closing everything and restart afresh hence all static variables will be reset from start
 * </p>
 * **/
    public static void doRestart(Context c) {
        try {
            //check if the context is given
            if (c != null) {
                //fetch the packagemanager so we can get the default launch activity
                // (you can replace this intent with any other activity if you want
                PackageManager pm = c.getPackageManager();
                //check if we got the PackageManager
                if (pm != null) {
                    //create the intent with the default start activity for your application
                    Intent mStartActivity = pm.getLaunchIntentForPackage(
                            c.getPackageName()
                    );
                    if (mStartActivity != null) {
                        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        //create a pending intent so the application is restarted after System.exit(0) was called.
                        // We use an AlarmManager to call this intent in 100ms
                        int mPendingIntentId = 223344;
                        PendingIntent mPendingIntent = PendingIntent
                                .getActivity(c, mPendingIntentId, mStartActivity,
                                        PendingIntent.FLAG_CANCEL_CURRENT);
                        AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                        //kill the application
                        System.exit(0);
                    } else {
                        Toast.makeText(c, "Please Open the Cut app now ", Toast.LENGTH_LONG).show();
                        // Log.e(TAG, "Was not able to restart application, mStartActivity null");
                    }
                } else {
                    Toast.makeText(c, "Please Open the Cut app now ", Toast.LENGTH_LONG).show();
                    //  Log.e(TAG, "Was not able to restart application, PM null");
                }
            } else {
                // Log.e(TAG, "Was not able to restart application, Context null");
                Toast.makeText(c, "Please Open the Cut App now ", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            Toast.makeText(c, "Please Open the Cut App now ", Toast.LENGTH_LONG).show();
            //Log.e(TAG, "Was not able to restart application");
        }
    }

    public static boolean isTimeWith_in_Interval(String valueToCheck, String endTime, String startTime) {
        boolean isBetween = false;
        try {
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(endTime);

            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(startTime);

            Date d = new SimpleDateFormat("HH:mm:ss").parse(valueToCheck);

            if (time1.after(d) && time2.before(d)) {
                isBetween = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return isBetween;
    }

    public boolean isTimeBetweenTwoTime(String initialTime, String finalTime) {
        String currentTime = CurrentTime();
        String reg = "^([0-1][0-9]|2[0-3]):([0-5][0-9]):([0-5][0-9])$";
        if (initialTime.matches(reg) && finalTime.matches(reg) && currentTime.matches(reg)) {
            boolean valid = false;
            //Start Time
            Date inTime = null;
            try {
                inTime = format.parse(initialTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(inTime);

            //Current Time
            Date checkTime = null;
            try {
                checkTime = format.parse(currentTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(checkTime);

            //End Time
            Date finTime = null;
            try {
                finTime = format.parse(finalTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(finTime);

            if (finalTime.compareTo(initialTime) < 0) {
                calendar2.add(Calendar.DATE, 1);
                calendar3.add(Calendar.DATE, 1);
            }

            Date actualTime = calendar3.getTime();
            if ((actualTime.after(calendar1.getTime()) || actualTime.compareTo(calendar1.getTime()) == 0)
                    && actualTime.before(calendar2.getTime())) {
                valid = true;
            }
            return valid;
        } else {
            throw new IllegalArgumentException("Not a valid time, expecting HH:mm:ss format");
        }

    }

    public static String RemoveLastChars(String string, int RemoveTheLast) {
        return string.substring(0, string.length() - RemoveTheLast);
    }

    private String CurrentTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = format;
        return dateformat.format(c.getTime());
    }

    public String getTimedifference(String time1, String time2) {


        // SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        Date date1 = null;
        try {
            date1 = format.parse(time1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date date2 = null;
        try {
            date2 = format.parse(time2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long difference = date2.getTime() - date1.getTime();
        long midpoint = (long) (difference * .5);
        midpoint = midpoint + date2.getTime();
        String HalfHourTime = milliSectoHours(midpoint);


        return HalfHourTime;


    }
/**
 * <p>
 *     converting milli seconds to hours
 *
 * </p>
 * <p>
 *
 * </p>
 *  @param millis
 *  @return  String
 * **/
    private String milliSectoHours(long millis) {

        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));


    }
    /**
     * <p>
     *     Download  the Update apk into phone memory
     * </p>
     * <p>
     *     will show a notification progress bar until it finishes
     *     and will show installation action to install the update
     * </p>
     * @param context
     * @param  url
     * * */

    public static void DownloadUpdatesApk(Context  context,String url){
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = "CutUpDateApp.apk";
        final Uri uri = Uri.parse("file://" + destination);
        DownloadManager.Request request=new  DownloadManager.Request(Uri.parse(url));
        request.setTitle("Cut-App-Update Download");
        request.setDescription("Downloading Update ...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir(destination,fileName);
        DownloadManager downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        final long downloadId = downloadManager.enqueue(request);

        new NifftyDialogs(context).messageOk("Check Download Progress In Notification Bar!");

        BroadcastReceiver onComplete=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.setDataAndType(uri, downloadManager.getMimeTypeForDownloadedFile(downloadId));
                context.startActivity(install);
                context.unregisterReceiver(this);
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }




    private static String get_device_id(Context ctx) {

        final TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);

        String tmDevice = "";
        String tmSerial = null;
        String androidId = null;


        try {
            tmDevice = "" + tm.getDeviceId();
        } catch (Exception ex) {
        }

        try {
            tmSerial = "" + tm.getSimSerialNumber();
        } catch (Exception ex) {
        }
        try {
            androidId = "" + Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ex) {
        }
        try {
            UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            String deviceId = deviceUuid.toString();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(deviceId.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            deviceId = sb.toString();


            return deviceId;
        } catch (Exception ex) {
        }
        return "nodeviceid";
    }

    private static String getAndroidID(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    }
/**
 * <p>
 *     get device ID .useful in identifying device
 * </p>
 * @param  context
 * @return String
 *
 * */
    public static String getAndroidDeviceID(Context context) {
        String testee = get_device_id(context);
        return testee.equalsIgnoreCase("nodeviceid") ? getAndroidID(context) : testee;
    }



    public int passHoursTime(String startTime) {
        return Integer.parseInt(startTime.substring(0, 2));

    }

    public int passMinsTime(String startTime) {
        return Integer.parseInt(startTime.substring(3, 5));

    }




    public String[] SCHOOLS = {"Select",
            "Natural Sciences & Mathematics", "Hospitality & Tourism"/*, "Graduate Business"*/
            , "Engineering Science & Technology", "Entrepreneurship & Business Sciences", "Art & Design", "Wild Life"
            , "Agricultural Sciences & Technology"
    };
    public String[] PROGRAMS = {"Select",
            "Agricultural Engineering", "Animal Production & Technology"
            , "Environmental Science", "Environmental Health", "Irrigation Engineering", "Biotechnology", "Food Science and Technology"
            , "Crop Science and Technology", "Creative Art And Design", "Clothing and Textile Technology"
            , "Accountancy", "Business Management And Entreprenuership", "Marketing", "Consumer Science", "Supply Chain Management"
            , "Fuels and Energy Engineering", "Mechatronics Engineering", "Production Engineering", "Environmental Engineering", "ICT"
            , "Applied Entrepreneurship", "Strategic Management", "Hospitality and Tourism", "Travel and Recreation", "Biology"
            , "Physics", "Mathematics", "Chemistry", "Statistics"
    };

    public static String[] Natural_Sciences__Mathematics = {"Select",
            "Biology"
            , "Physics", "Mathematics", "Chemistry", "Statistics"

    };
    public static String[] Hospitality__Tourism = {"Select",
            "Hospitality and Tourism", "Travel and Recreation"

    };
    public static String[] Graduate_Business = {"Select",

    };
    public static String[] Engineering_Science_Technology = {"Select",
            "Mechatronics Engineering", "Production Engineering", "Environmental Engineering", "ICT"

    };
    public static String[] Entrepreneurship_Business_Sciences = {"Select",
            "Accounting Science & Finance", "Entrepreneurship & Business Management", "International Marketing",
            "Retail Management and Consumer Sciences", "Supply Chain Management",};
    public static String[] Wild_life = {"Select",
            "Wildlife Ecology and Conservation", "Freshwater and Fishery Science"
    };
    public static String[] Art_Design = {"Select",
            "Creative Art and Design", "Clothing Fashion Design"
    };
    public static String[] Agricultural_Sciences_Technology = {"Select",
            "Agricultural Engineering", "Animal Production and Technology", "Environmental Science", "Irrigation And Water Engineering",
            "Biotechnology", "Food Science and Technology", "Food Science and Technology",
    };


}
