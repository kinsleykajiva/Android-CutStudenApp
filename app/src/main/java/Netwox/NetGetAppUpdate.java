package Netwox;

import android.os.AsyncTask;

import InterfaceCallBacks.OnUpdateFound_Interface;

/**
 * Created by Kinsley Kajiva on 12/31/2016.
 * <p>
 *     A background thread that will ping a server to get the new or current app version recently uploaded
 *
 * </p>
 * <p>
 *     In the onPostExecute() method interface OnUpdateFound_Interface will be called
 * </p>
 * @see OnUpdateFound_Interface
 */

public class NetGetAppUpdate extends AsyncTask<Void, Void, String[]> {

    private OnUpdateFound_Interface onUpdateFound_interface;

    public NetGetAppUpdate(OnUpdateFound_Interface onUpdateFound_interface) {
        this.onUpdateFound_interface = onUpdateFound_interface;
    }

    @Override
    protected String[] doInBackground(Void... voids) {

        return NetwoxRequest.getAppVersions();
    }

    @Override
    protected void onPostExecute(String s[]) {
        super.onPostExecute(s);
        if (s[0] !=null && s[1]!=null){
        } else {
            onUpdateFound_interface.onUpdateFound(s[0], s[1]);
        }

    }
}