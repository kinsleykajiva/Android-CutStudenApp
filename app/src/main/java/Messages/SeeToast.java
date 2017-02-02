package Messages;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by kinsley kajiva on 6/28/2016.Zvaganzirwa nakinsley kajiva musiwa 6/28/2016
 */
public class SeeToast {


    public void message_long(Context cxt, String string ){
        Toast toast=Toast.makeText(cxt, string, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }
    public void message_short(Context cxt, String string ){
        Toast toast=Toast.makeText(cxt, string, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }
}
