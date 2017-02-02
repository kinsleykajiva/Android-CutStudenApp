package mServices;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by kinsley kajiva on 7/10/2016.Zvaganzirwa nakinsley kajiva musiwa 7/10/2016
 */
public class BackGroundsIntService extends IntentService {

    public BackGroundsIntService() {
        super("kinsley kajiva");

    }



    @Override
    protected void onHandleIntent(Intent intent) {
        seFirstClassAlarms();
    }
    private void seFirstClassAlarms() {



    }

}
