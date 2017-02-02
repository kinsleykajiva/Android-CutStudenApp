package Netwox;

import android.os.AsyncTask;

import InterfaceCallBacks.Interface_OnloadExamTimeTable;

import static DBAccess.RealmDB.CRUD.DeleteAllExams;

/**
 * Created by Kinsley Kajiva on 1/4/2017.
 */

public class NetGetExamTimeTable extends AsyncTask<String,Void,String> {

    private Interface_OnloadExamTimeTable examTimeTable;

    public NetGetExamTimeTable(Interface_OnloadExamTimeTable examTimeTable) {
        this.examTimeTable = examTimeTable;
    }

    @Override
    protected String doInBackground(String... strings) {
        return NetwoxRequest.GetExamTimeTable(strings[0],strings[1],strings[2]);
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        examTimeTable.onExamLoaded(s);
    }
}
