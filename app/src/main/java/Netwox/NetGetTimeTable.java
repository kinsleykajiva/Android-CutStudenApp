package Netwox;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import InterfaceCallBacks.InterfaceLoadTimeTable;

/**
 * Created by kinsley kajiva on 9/26/2016.Zvaganzirwa nakinsley kajiva musiwa 9/26/2016
 */

public class NetGetTimeTable extends AsyncTask<String,Void,String> {

private InterfaceLoadTimeTable interfaceLoadTimeTable;

    public NetGetTimeTable(InterfaceLoadTimeTable interfaceLoadTimeTable) {
        this.interfaceLoadTimeTable = interfaceLoadTimeTable;
    }

    @Override
    protected String doInBackground(String... params) {
        String result="";
        try {
            result=  new NetwoxRequest().GetTimeTable(params[0],params[1],params[2]);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String list) {
        if(list.isEmpty()){
            interfaceLoadTimeTable.onTimeTableLoaded("empty");
        }else if(!list.isEmpty() ){
            new NetwoxRequest().ProcessJson(list);
            interfaceLoadTimeTable.onTimeTableLoaded(list);
        }



    }
}
