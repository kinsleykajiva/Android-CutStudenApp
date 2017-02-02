package kinsleykajiva.co.zw.cutstudentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import BuildsConfigs.BuildsData;
import DBAccess.Preffs.mSettings;
import DBAccess.RealmDB.ClassesLecture;
import InterfaceCallBacks.InterfaceLoadTimeTable;
import Messages.NifftyDialogs;
import Netwox.NetGetTimeTable;
import io.realm.Realm;
import io.realm.RealmResults;
import jonathanfinerty.once.Once;
import mServices.AlarmConfigs;
import widgets.CustomSpinnerAdapter;
import widgets.CustomeViewPager;

import static BuildsConfigs.BuildsData.OVERLAY_PERMISSION_REQ_CODE_CHATHEAD;
import static BuildsConfigs.BuildsData.OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG;

public class WelcomeSetup extends AppCompatActivity implements InterfaceLoadTimeTable {
    private CustomeViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private ProgressDialog dialog;
    int current;
    private Context context = WelcomeSetup.this;
    private Button btnSkip, btnNext;
    private boolean spnnerCheck1 = false, spnnerCheck2 = false, spnnerCheck3 = false;
    private mSettings settings;
    private String School_ = "", Program_ = "", Level_ = "";
    private ArrayList<String> programmes;
    private CustomSpinnerAdapter customSpinnerAdapter2;
    private Spinner spinnerProgramme,spinnerLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_welcome_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().hide();
        settings = new mSettings(context);
        //
        viewPager = (CustomeViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);
        viewPager.setPagingEnabled(false);
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        // adding bottom dots
        addBottomDots(0);
        changeStatusBarColor();
        btnNext.setOnClickListener(v -> {
            current = getItem(+1);
            if (current < layouts.length) {
                // move to next screen

                if (spnnerCheck1 && spnnerCheck2 && spnnerCheck3) {

                    if (current == 3) {

                        dialog = new ProgressDialog(context);
                        /*if we are going to load the data we need to make sure that the database
                        is empty so that we dont load what we already have simce my db does not use any check if it exist at all*/
                        AlarmConfigs AL=  new AlarmConfigs(context);
                        Realm realm = Realm.getDefaultInstance();
                        RealmResults<ClassesLecture> results = realm.where(ClassesLecture.class).equalTo("isReminderSetyet","yes").findAll();
                        for (ClassesLecture u : results) {
                            AL.CancelAlarm(context,u.getClassID());
                        }
                        realm.beginTransaction();
                        realm.where(ClassesLecture.class).findAll().deleteAllFromRealm();
                        realm.commitTransaction();

                        realm.close();
                        settings.setUSER_SCHOOL(School_);
                        settings.setUSER_LEVEL(Level_);
                        settings.setUSER_PROGRAME(Program_);
                        ShowPorgressDialog(true);

                        new NetGetTimeTable(WelcomeSetup.this).execute(School_, Program_, Level_);
                    } else {
                        viewPager.setCurrentItem(current);

                    }

                    // viewPager.setCurrentItem(current);
                } else {
                    new NifftyDialogs(context).messageOkError("Eish ..Can't Proceed !!",
                            "Please Select From All The Drop Menu Items");
                }
            } else {
                launchHomeScreen();

            }
        });
        btnSkip.setOnClickListener(v -> {
            int current1 = getItem(-1);
            if (current1 < layouts.length) {
                // move to next screen
                viewPager.setCurrentItem(current1);
            } else {
                launchHomeScreen();
            }
        });
        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        RequestOverlayPermission();
        if( BuildsData.IS_INSTALL_FROM_DRAWER){
            viewPager.setCurrentItem(2);
        }
    }

    private void RequestOverlayPermission(){
        if(Build.VERSION.SDK_INT >= 23) {
            if(!canDrawOverlays(context)){
                requestPermission(OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG);
            }
        }
    }
    private  boolean canDrawOverlays(Context context){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }else{
            return Settings.canDrawOverlays(context);
        }


    }
    private void requestPermission(int requestCode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, requestCode);
    }
    private void needPermissionDialog(final int requestCode){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("You need to allow permission to set Alarms");
        builder.setPositiveButton("OK",
                (dialog12, which) -> {
                    dialog12.dismiss();
                    requestPermission(requestCode);
                });
        builder.setNegativeButton("Cancel", (dialog1, which) -> {

            dialog1.dismiss();
           /* Toast.makeText(WelcomeSetup.this, "Allow permissions next time.Restart the app", Toast.LENGTH_LONG).show();
            finish();*/
        });
        builder.setCancelable(false);
        builder.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD) {
            if (!canDrawOverlays(context)) {
                needPermissionDialog(requestCode);
            }

        }else if(requestCode == OVERLAY_PERMISSION_REQ_CODE_CHATHEAD_MSG){
            if (canDrawOverlays(context)) {
                needPermissionDialog(requestCode);
            }

        }

    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    public static boolean HasUsedManuall = false;

    private void launchHomeScreen() {
        settings.setFirstTimeLaunch(false);
        if (!Once.beenDone(Once.THIS_APP_INSTALL, "installation")) {
            Once.markDone("installation");
        }
        if( BuildsData.IS_INSTALL_FROM_DRAWER){
            Intent i=new Intent(WelcomeSetup.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        }else{
            startActivity(new Intent(WelcomeSetup.this, MainActivity.class));
            finish();
        }

    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == layouts.length - 1) {

                ShowPorgressDialog(false);
                btnNext.setText(getString(R.string.start));

            } else {
                btnNext.setText(position==2?"Install":getString(R.string.next));
            }
            if (position == 1 || position == 2 || position == 3) {
                btnSkip.setVisibility(View.VISIBLE);
            } else {
                btnSkip.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}

        @Override
        public void onPageScrollStateChanged(int arg0) {}
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void ShowPorgressDialog(boolean shouldshow) {
        if (shouldshow) {
            if (!dialog.isShowing()) {
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setMessage("Loading TimeTable. Please wait...");
                dialog.show();
            }
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }

    }

    @Override
    public void onTimeTableLoaded(String s) {
        ShowPorgressDialog(false);

        if (!s.isEmpty() && !s.equalsIgnoreCase("empty")) {

            viewPager.setCurrentItem(current);
        } else if (s.isEmpty()) {
            new NifftyDialogs(context).messageOkError("Eish ..Failed !!",
                    "Failed to load the time table .Try again");
        } else if (s.equalsIgnoreCase("empty")) {
            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);

            dialogBuilder
                    .withTitle("Manual Installation")
                    .withTitleColor("#FFFFFF")
                    .withDividerColor("#727272")
                    .withIcon(R.drawable.ic_info_white_24dp)
                    .withMessage("Timetable download failed.Instead install your Timetable manually ?")
                    .withMessageColor("#FFFFFFFF")
                    .withDialogColor("#FFE74C3C")
                    .isCancelableOnTouchOutside(false)
                    .withDuration(700)
                    .withEffect(new NifftyDialogs(context).stylepop_up())
                    .withButton1Text("Yes")
                    .withButton2Text("No")
                    .isCancelableOnTouchOutside(true)

                    .setButton1Click(v -> {
                        HasUsedManuall = true;
                        launchHomeScreen();
                        dialogBuilder.dismiss();
                    })
                    .setButton2Click(v -> {
                        dialogBuilder.dismiss();
                        ShowPorgressDialog(true);
                        new NetGetTimeTable(WelcomeSetup.this).execute(School_, Program_, Level_);

                    })
                    .show();
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (position == 0 || position == 1 || position == 2) {
                spnnerCheck1 = true;
                spnnerCheck2 = true;
                spnnerCheck3 = true;
            } else {
                spnnerCheck1 = false;
                spnnerCheck2 = false;
                spnnerCheck3 = false;
            }

            View view = layoutInflater.inflate(layouts[position], container, false);

            if (position == 3) {
                initCustomSpinner();
            }
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }


    private void initCustomSpinner() {
        BuildsData configs = new BuildsData();
        Spinner spinnerSchool = (Spinner) findViewById(R.id.spinnerSchool);
         spinnerProgramme = (Spinner) findViewById(R.id.spinnerProgramme);
         spinnerLevel = (Spinner) findViewById(R.id.spinnerLevel);

        spinnerProgramme.setEnabled(false);
        spinnerProgramme.setBackgroundResource(R.drawable.background_error);
        spinnerLevel.setEnabled(false);
        spinnerLevel.setBackgroundResource(R.drawable.background_error);

        //
        // Spinner Drop down elements

        final  ArrayList<String> languages = new ArrayList<>(Arrays.asList(configs.SCHOOLS));
         programmes =  new ArrayList<>(Arrays.asList(configs.PROGRAMS));
        final  ArrayList<String> levels =new ArrayList<>(Arrays.asList(configs.LEVELS));

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(context, languages);
        spinnerSchool.setAdapter(customSpinnerAdapter);
        //
         customSpinnerAdapter2 = new CustomSpinnerAdapter(context, programmes);
        spinnerProgramme.setAdapter(customSpinnerAdapter2);
        //
        spinnerSchool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();
                if (position == 0) {

                    spnnerCheck1 = false;

                    spinnerProgramme.setEnabled(false);
                    spinnerProgramme.setBackgroundResource(R.drawable.background_error);
                    spinnerLevel.setEnabled(false);
                    spinnerLevel.setBackgroundResource(R.drawable.background_error);

                } else {
                    spinnerProgramme.setEnabled(true);
                    spinnerProgramme.setBackgroundResource(R.drawable.custom_spinner_background);

                    programmes.clear();

                    if(position == 1){ //Natural_Sciences__Mathematics

                        programmes = new ArrayList<>(Arrays.asList(BuildsData.Natural_Sciences__Mathematics));

                    }else if(position ==2 ){ // Hospitality__Tourism
                        programmes = new ArrayList<>(Arrays.asList(BuildsData.Hospitality__Tourism));
                    }else if(position == 3){ // Engineering_Science_Technology
                        programmes = new ArrayList<>(Arrays.asList(BuildsData.Engineering_Science_Technology));
                    }else if(position ==4 ){ // Entrepreneurship_Business_Sciences
                        programmes = new ArrayList<>(Arrays.asList(BuildsData.Entrepreneurship_Business_Sciences));
                    }else if(position ==5 ){ // Art_Design
                        programmes = new ArrayList<>(Arrays.asList(BuildsData.Art_Design));
                    }else if(position == 6){//  Wild_life
                        programmes =new ArrayList<>(Arrays.asList(BuildsData.Wild_life));
                    }else  if(position==7) {//   Agricultural_Sciences_Technology
                        programmes = new ArrayList<>(Arrays.asList(BuildsData.Agricultural_Sciences_Technology));
                    }
                    customSpinnerAdapter2 = new CustomSpinnerAdapter(context, programmes);
                    spinnerProgramme.setAdapter(customSpinnerAdapter2);

                    School_ = item;
                    spnnerCheck1 = true;
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ////end


        spinnerProgramme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

                if (position == 0) {

                    spnnerCheck2 = false;

                    spinnerLevel.setEnabled(false);
                    spinnerLevel.setBackgroundResource(R.drawable.background_error);
                } else {
                    spinnerLevel.setEnabled(true);
                    spinnerLevel.setBackgroundResource(R.drawable.custom_spinner_background);
                    Program_ = item;
                    spnnerCheck2 = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ////end
        CustomSpinnerAdapter customSpinnerAdapter3 = new CustomSpinnerAdapter(context, levels);
        spinnerLevel.setAdapter(customSpinnerAdapter3);
        spinnerLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String item = parent.getItemAtPosition(position).toString();

                if (position == 0) {

                    spnnerCheck3 = false;
                } else {
                    Level_ = item;
                    spnnerCheck3 = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ////end
    }

}
