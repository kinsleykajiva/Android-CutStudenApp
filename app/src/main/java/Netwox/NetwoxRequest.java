package Netwox;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import BuildsConfigs.BuildsData;
import DBAccess.RealmDB.CRUD;
import DBAccess.RealmDB.ExaminationDB;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static DBAccess.RealmDB.CRUD.CreateClassLectureBackup;
import static DBAccess.RealmDB.CRUD.CreateExamBackup;
import static DBAccess.RealmDB.CRUD.DeleteAllExams;

/**
 * Created by kinsley kajiva on 6/28/2016.Zvaganzirwa nakinsley kajiva musiwa 6/28/2016
 */
public class NetwoxRequest {


    final String ARRAY_NAME = "results";
    private BuildsData configs = new BuildsData();



    public NetwoxRequest() {

    }
public static String GetExamTimeTable(String school, String department, String level ){
    String returningValue = "";

    try {
        Response responses = new OkHttpClient().newCall(
                new Request.Builder()
                        .url("http://zimcybers.co.zw/cutApp/silentscripts/examtimetables.php")
                        .post(
                                new FormBody.Builder()
                                        .add("Get_school", school)
                                        .add("Get_department", department)
                                        .add("Get_level", level)
                                        .build())
                        .build()
        ).execute();
        if (responses.isSuccessful()) {
            returningValue = responses.body().string();
            CreateExamBackup(returningValue); //creating a back up
             processExamJson(returningValue);
        }
    } catch (IOException  e) {
        e.printStackTrace();
    }
    return returningValue;

}

    public static void processExamJson(String json) {
        List<ExaminationDB> temp=new ArrayList<>();
        try {
            JSONArray Jarray = new JSONObject(json).getJSONArray("results");

            for(int i=0;i<Jarray.length();i++){
                JSONObject object = Jarray.getJSONObject(i);
               String startTime=  (object.getString("time_"));
                String endTime= (object.getString("time__"));
                String venue= object.getString("venue");
                String dDate=object.getString("date_");
                String class_= object.getString("class_");
                String day_=object.getString("day_");

                temp.add(new ExaminationDB(day_,startTime,endTime,venue,class_,dDate,0,false,false));
            }
            DeleteAllExams();
            CRUD.initWriteExamination(temp);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static String[] getAppVersions() {
        String[] returnValu = new String[2];
        try {
            Response responses = new OkHttpClient().newCall(
                    new Request.Builder()
                            .url("http://zimcybers.co.zw/cutApp/silentscripts/timetable.php")
                            .post(
                                    new FormBody.Builder()
                                            .add("cutApp_POST", "versions")

                                            .build())
                            .build()
            ).execute();
            if (responses.isSuccessful()) {

                JSONObject oj=new JSONObject(responses.body().toString());
                returnValu[0]=oj.getString("versionCode");
                returnValu[1]=oj.getString("versionName");
            }
        } catch (IOException |JSONException e) {
            e.printStackTrace();
        }
        return returnValu;

    }

    public String GetTimeTable(String school, String department, String level) throws JSONException {
        String returningValue = "";
        try {
            Response responses = new OkHttpClient().newCall(
                    new Request.Builder()
                            .url("http://zimcybers.co.zw/cutApp/silentscripts/timetable.php")
                            .post(
                                    new FormBody.Builder()
                                            .add("Get_school", school)
                                            .add("Get_department", department)
                                            .add("Get_level", level)
                                            .build())
                            .build()
            ).execute();

            if (responses.isSuccessful()) {
                returningValue = responses.body().string();
                CreateClassLectureBackup(returningValue);// creating a  backup
                returningValue = new JSONObject(returningValue).getJSONArray(ARRAY_NAME).length() == 0 ? "" : returningValue;
            } else {
                String $;
                switch (responses.code()) {
                    case 400:
                        $ = "Bad Request";//The request could not be understood by the server due to malformed syntax
                        break;
                    case 402:
                        $ = "This code is reserved for future use";
                        break;
                    case 403:
                        $ = "Forbidden";//The server understood the request, but is refusing to fulfill it.
                        break;
                    case 404:
                        $ = "Not Found";//The server has not found anything matching the Request-URI
                        break;
                    case 401:
                        $ = "permission denied";
                        break;
                    case 502:
                        $ = "Bad Gateway";//The server, while acting as a gateway or proxy, received an invalid response
                        // from the upstream server it accessed in attempting to fulfill the request.
                        break;
                    case 500:
                        $ = "Internal Server Error";//The server encountered an unexpected condition which prevented it from fulfilling the request.
                        break;
                    case 503:
                        $ = "Service Unavailable";//The server is currently unable to handle the request due to a temporary
                        // overloading or maintenance of the server.
                        break;
                    case 504:
                        $ = "Service Unavailable";//The server, while acting as a gateway or proxy, did not receive a timely response
                        // from the upstream server specified by the URI (e.g. HTTP, FTP, LDAP) or some other auxiliary server (e.g. DNS)
                        // it needed to access in attempting to complete the request.
                        break;
                    case 408:
                        $ = "Request Timeout";//The client did not produce a request within the time that the server was prepared to wait
                        break;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        return returningValue;


    }

    public void ProcessJson(String jsonString) {

        try {

            JSONArray Jarray = new JSONObject(jsonString).getJSONArray(ARRAY_NAME);
            for (int i = 0; i < Jarray.length(); i++) {
                JSONObject object = Jarray.getJSONObject(i);
                writeToDb(
                        processStartTime(object.getString("time_")),
                        processEndTime(object.getString("time_")),
                        object.getString("venue"),
                        object.getString("class_"),
                        processTimesOrder(object.getString("time_")),
                        object.getString("day_"),
                        object.getString("type")
                );
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String processStartTime(String time) {

        if (time.startsWith("07")) {
            return configs.TIMES_PERIOD_START[0];
        } else if (time.startsWith("07")) {
            return configs.TIMES_PERIOD_START[1];
        } else if (time.startsWith("09")) {
            return configs.TIMES_PERIOD_START[2];
        } else if (time.startsWith("11")) {
            return configs.TIMES_PERIOD_START[3];
        } else if (time.startsWith("13")) {
            return configs.TIMES_PERIOD_START[4];
        } else if (time.startsWith("16")) {
            return configs.TIMES_PERIOD_START[5];
        } else {
            return configs.TIMES_PERIOD_START[6];
        }

    }
    private static String processStartTime2(String time) {

        if (time.startsWith("07")) {
            return BuildsData.TIMES_PERIOD_START_copy[0];
        } else if (time.startsWith("07")) {
            return BuildsData.TIMES_PERIOD_START_copy[1];
        } else if (time.startsWith("09")) {
            return BuildsData.TIMES_PERIOD_START_copy[2];
        } else if (time.startsWith("11")) {
            return BuildsData.TIMES_PERIOD_START_copy[3];
        } else if (time.startsWith("13")) {
            return BuildsData.TIMES_PERIOD_START_copy[4];
        } else if (time.startsWith("16")) {
            return BuildsData.TIMES_PERIOD_START_copy[5];
        } else {
            return BuildsData.TIMES_PERIOD_START_copy[6];
        }

    }

    private String processEndTime(String time) {

        if (time.startsWith("07")) {
            return configs.TIMES_PERIOD_END[0];
        } else if (time.startsWith("07")) {
            return configs.TIMES_PERIOD_END[1];
        } else if (time.startsWith("09")) {
            return configs.TIMES_PERIOD_END[2];
        } else if (time.startsWith("11")) {
            return configs.TIMES_PERIOD_END[3];
        } else if (time.startsWith("13")) {
            return configs.TIMES_PERIOD_END[4];
        } else if (time.startsWith("16")) {
            return configs.TIMES_PERIOD_END[5];
        } else {
            return configs.TIMES_PERIOD_END[6];
        }

    }

    private int processTimesOrder(String time) {
        if (time.startsWith("07")) {
            return configs.CLASS_ORDER[0];
        } else if (time.startsWith("07")) {
            return configs.CLASS_ORDER[1];
        } else if (time.startsWith("09")) {
            return configs.CLASS_ORDER[2];
        } else if (time.startsWith("11")) {
            return configs.CLASS_ORDER[3];
        } else if (time.startsWith("13")) {
            return configs.CLASS_ORDER[4];
        } else if (time.startsWith("16")) {
            return configs.CLASS_ORDER[5];
        } else {
            return configs.CLASS_ORDER[6];
        }

    }

    private void writeToDb(String classStartTime, String classEndTime, String classVenue, String classModuleName, int ClassOrdering, String classDay, String ClassType) {
        new CRUD().writeToDb(
                classDay,
                classStartTime,
                classEndTime,
                classModuleName,
                classVenue,
                "" + ClassOrdering,
                configs.IS_REMINDER_SET[0],
                ClassType

        );
    }
}
