package DBAccess.Preffs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kinsley kajiva on 11/9/2016.Zvaganzirwa nakinsley kajiva musiwa 11/9/2016
 */

public class mSettings {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context _context;

    // shared pref mode
    private int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "kinsleykajiva.co.zw.cutstudentapp.msettings";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String TAB_SATURDAY = "tab_saturday";
    private static final String TAB_SUNDAY = "tab_sunday";
    private static final String TAB_ABBRIV = "tab_name_abbriv";
    private final String USER_SCHOOL="school";
    private final String USER_LEVEL="level";
    private final String USER_PROGRAME="programme";
    private final String APP_SHORT_CUT="app_shortcut";
    public mSettings(Context _context) {
        this._context = _context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public boolean hasSHORT_CUT(){return pref.getBoolean(APP_SHORT_CUT,false);}
    public void setAPP_SHORT_CUT(boolean i) { editor.putBoolean(APP_SHORT_CUT,i);editor.commit();}

    public boolean isTAB_ABBRIVIATED(){return pref.getBoolean(TAB_ABBRIV,true);}
    public void setTAB_ABBRIVIATED(boolean i) { editor.putBoolean(TAB_ABBRIV,i);editor.commit();}

    public String getTAB_SUNDAY(){return pref.getString(TAB_SUNDAY,"");}
    public void setTAB_SUNDAY(String i) { editor.putString(TAB_SUNDAY,i);editor.commit();}


    public String getTAB_SATURDAY(){ return pref.getString(TAB_SATURDAY,""); }
    public void setTAB_SATURDAY(String i) {editor.putString(TAB_SATURDAY,i);editor.commit(); }


    public void setUSER_SCHOOL(String i) {editor.putString(USER_SCHOOL,i);editor.commit();}
    public String getUSER_SCHOOL(){return pref.getString(USER_SCHOOL,"");}

    public void setUSER_LEVEL(String i) {editor.putString(USER_LEVEL,i);editor.commit();}
    public String getUSER_LEVEL(){return pref.getString(USER_LEVEL,"");}

    public void setUSER_PROGRAME(String i) {editor.putString(USER_PROGRAME,i);editor.commit();}
    public String getUSER_PROGRAME(){ return pref.getString(USER_PROGRAME,"");
    }
    public void setFirstTimeLaunch(boolean isFirstTime) { editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime); editor.commit(); }

    public void ClearAllPreffs(){
        this.setUSER_PROGRAME("");
        this.setUSER_LEVEL("");
        this.setUSER_SCHOOL("");
        this.setTAB_SATURDAY("");
        this.setTAB_SUNDAY("");
    }


}
