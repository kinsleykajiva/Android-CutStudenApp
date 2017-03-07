package kinsleykajiva.co.zw.cutstudentapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import BuildsConfigs.BuildsData;
import DBAccess.Preffs.mSettings;
import DBAccess.RealmDB.NotesDB;
import InterfaceCallBacks.Interface_OnloadExamTimeTable;
import InterfaceCallBacks.OnUpdateFound_Interface;
import Messages.NifftyDialogs;
import Messages.SeeToast;
import Netwox.NetGetAppUpdate;
import Netwox.NetGetExamTimeTable;
import fragments.fragmentDay;
import fragments.fragmentExam;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import jonathanfinerty.once.Amount;
import jonathanfinerty.once.Once;
import widgets.CustomSpinnerAdapter;

import static BuildsConfigs.BuildsData.EXAM_TIME_KEY;
import static BuildsConfigs.BuildsData.ExamLoadedFromFragment;
import static BuildsConfigs.BuildsData.JustRemovedTAB_SARTARDAY;
import static BuildsConfigs.BuildsData.JustRemovedTAB_SUNDAY;
import static BuildsConfigs.BuildsData.JustSetRenamedFragNamings;
import static BuildsConfigs.BuildsData.TIMES_DAYS_FULL;
import static BuildsConfigs.BuildsData.TIMES_DAYS_SHORT;
import static BuildsConfigs.BuildsData.capitalizeFirstLetter;
import static BuildsConfigs.BuildsData.doRestart;
import static BuildsConfigs.BuildsData.isExamTime;
import static kinsleykajiva.co.zw.cutstudentapp.WelcomeSetup.HasUsedManuall;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnUpdateFound_Interface, Interface_OnloadExamTimeTable {

    private Toolbar toolbar;
    private BuildsData configs = new BuildsData();
    private FloatingActionButton fab;
    private ViewPager mPager;
    private Boolean exit = false;
    private TabLayout mTabLayout;
    private mSettings settings;
    private MyApplication app;
    private DrawerLayout drawer;
    private ViewPagerAdapter adapter;
    private Context context = MainActivity.this;
    private final String installation = "installation";
    private TextView userprogram;
    private TextView CountBadges_nav_notes;
    private ProgressDialog dialog;
    private int indicator = 0;
    private Menu mMenu;
    private int HowmanyTime_Tabs_Added = 0;
    private boolean wasSat_Day_there = false;
    final String virginity_Check = "virgin_home";
    private Realm realm;
    private RealmResults<NotesDB> results;
    private RealmChangeListener realmListener= element -> {
// results
        CountBadges_nav_notes.setText(results.size()>0?"("+results.size()+")":"");
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        runHomeKeeping();
        setContentView(R.layout.activity_main);
        initViews();
        setUpTabs();
        runInit_tour();
    }
    private void runInit_tour() {
        if (Once.beenDone(Once.THIS_APP_INSTALL, installation)) {
            if (!Once.beenDone(Once.THIS_APP_INSTALL, virginity_Check) && !HasUsedManuall) {
                Once.markDone(virginity_Check);
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder.withTitle("Welcome ...")
                        .withDialogColor(BuildsData.getColor(context, R.color.colorPrimary))
                        .withMessage("Welcome to C.U.T Student App")
                        .withButton1Text("Ok").isCancelableOnTouchOutside(false).withEffect(new NifftyDialogs(context).stylepop_up())
                        .withIcon(R.drawable.ic_info_white_24dp).withDuration(700)
                        .setButton1Click(v -> {
                            dialogBuilder.dismiss();
                            initshowWalkThrough_Manual();
                        })
                        .show();
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        realm.addChangeListener(realmListener);
        if (HasUsedManuall) {
            initshowWalkThrough_Manual();
        }
        RunUpdateCheck(context);
    }
/**
 *
 * <p>
 *  This will check if there is a newer app comparing the app versions and even the version name found in gradle.
 * </p>
 * <p>
 *     At the moment this method will trigger the app up-date function if and only if the app has been used 5 time or more before the last update.
 *     A background class or thread in class  NetGetAppUpdate is started in the background that is off the main thread
 * </p>
 * <p>
 *     The class implementing the OnUpdateFound_Interface interface will call this methhod from the background method onPostexecute ()
 * </p>
 * @see NetGetAppUpdate
 * @see OnUpdateFound_Interface
 * @params Context
 * **/
    private void RunUpdateCheck(Context context) {
        if (Once.beenDone(BuildsData.COUNT_APP_USED, Amount.exactly(5)) || Once.beenDone(BuildsData.COUNT_APP_USED, Amount.moreThan(5))) {
            Toast.makeText(context, "Checking Cut App Update", Toast.LENGTH_LONG).show();
            new NetGetAppUpdate(this).execute();
        } else {
            Once.clearDone(BuildsData.COUNT_APP_USED);
        }

    }

    /**
     * <p>
     *     will work once doing the showcase
     * </p>*/
    private void initshowWalkThrough_Manual() {

        /*if (toolbar.isOverflowMenuShowing()) {
            toolbar.getMenu().clear();
           // toolbar.me
        }*/
       // toolbar.inflateMenu(R.menu.main);
        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(fab, "Add Class/Tutorial", "Click to create or add new class/tutorial depending on day ")
                                .cancelable(false)
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.colorPrimaryDark)
                                .targetCircleColor(android.R.color.black)
                                .transparentTarget(true)
                                .drawShadow(true)
                                .textColor(android.R.color.white).id(1),
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_addTab, "Add Tab", "Tap to add a new Tab to your days")
                                .cancelable(false)
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.colorPrimaryDark)
                                .targetCircleColor(android.R.color.black)
                                .transparentTarget(true)
                                .drawShadow(true)
                                .textColor(android.R.color.white)
                                .id(2),
                        TapTarget.forToolbarMenuItem(toolbar, R.id.action_settings, "Settings", "Tap to manage CUT Settings.")
                                .cancelable(false)
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.colorPrimaryDark)
                                .targetCircleColor(android.R.color.black)
                                .transparentTarget(true)
                                .drawShadow(true)
                                .textColor(android.R.color.white).id(3),
                        TapTarget.forToolbarNavigationIcon(toolbar, "Side Drawer-Menu", "Tap to access menu items like notification.")
                                .cancelable(false)
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.colorPrimaryDark)
                                .targetCircleColor(android.R.color.black)
                                .transparentTarget(true)
                                .drawShadow(true)
                                .textColor(android.R.color.white).id(4)


                ).listener(new TapTargetSequence.Listener() {

                    @Override
                    public void onSequenceFinish() {
                        HasUsedManuall = false;
                        if (Once.beenDone(Once.THIS_APP_INSTALL, installation)) {
                            final String virginity_Check = "virgin_home";
                            if (!Once.beenDone(Once.THIS_APP_INSTALL, virginity_Check)) {
                                Once.markDone(virginity_Check);
                                new NifftyDialogs(context).justMessage("Welcome to Cut Student App");
                            }
                        }
                        drawer.openDrawer(GravityCompat.START);


                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {

                    }
                });


        sequence.start();


    }

    private void InitUser_WalkThrough_Manual() {
        TapTargetView.showFor(this, TapTarget.forView(userprogram, "App user", "App user Programme .")
                        .cancelable(true)
                        .dimColor(android.R.color.black)
                        .outerCircleColor(R.color.colorPrimaryDark)
                        .targetCircleColor(android.R.color.black)
                        .transparentTarget(true)
                        .drawShadow(true)
                        .textColor(android.R.color.white)
                , new TapTargetView.Listener() {
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);

                        drawer.closeDrawer(GravityCompat.START);
                    }
                }
        );
    }

    private void runHomeKeeping() {
        realm=Realm.getDefaultInstance();
        results = realm.where(NotesDB.class).findAll();
        app = (MyApplication) getApplication();
        dialog = new ProgressDialog(context);
        isExamTime = Once.beenDone(Once.THIS_APP_INSTALL, EXAM_TIME_KEY);
        settings = new mSettings(context);
        BuildsData.IsStillUnreadNotification = true;

        if (!Once.beenDone(Once.THIS_APP_INSTALL, installation)) {
            startActivity(new Intent(this, WelcomeSetup.class));
            finish();
            return;
        }

    }

    private void setUpTabs() {
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mPager = (ViewPager) findViewById(R.id.viewpager);
//
        setupViewPager();
    }

    @Override
    public void onUpdateFound(String versionCode, String versionName) {
        final String VersionCode = BuildConfig.VERSION_CODE + "";

        if (versionCode != null) {
            if (!VersionCode.contentEquals(versionCode)) {
                new BottomDialog.Builder(this)
                        .setTitle("Update Cut Student App!")
                        .setContent(capitalizeFirstLetter("A New Better and Faster Version is Out. Please Update"))
                        .setPositiveText("Update")
                        .setNegativeText("Cancel")
                        .setCancelable(false)
                        .setPositiveBackgroundColor(BuildsData.getColor(context, R.color.colorAccent))
                        .setPositiveTextColorResource(android.R.color.white)

                        .onPositive(dialog1 -> {
                            BuildsData.DownloadUpdatesApk(context,"http://www.zimcycbers.co.zw/payment_types_badge1.png");
                           // Toast.makeText(context, "Processing link ...", Toast.LENGTH_SHORT).show();
                        })
                        .onNegative(dialog12 -> dialog12.dismiss())

                        .show();
            }
        }
    }

    @Override
    public void onExamLoaded(String response) {
        ShowPorgressDialog(false);
        if (response.isEmpty()) {
            // new NifftyDialogs(context).messageOkError("Connection Error !!", "Check your Internet Connection");
            app.setExamMark();

            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withTitle("Information")
                    .withMessage("Manually Load Exam-Timetable.")
                    .withButton1Text("Ok-Load").isCancelableOnTouchOutside(false).withEffect(new NifftyDialogs(context).stylepop_up())
                    .withIcon(R.drawable.ic_info_white_24dp).withDuration(700)
                    .setButton1Click(v -> {
                            doRestart(context);
                        dialogBuilder.dismiss();
                    })
                    .show();
        } else {
            app.setExamMark();

            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
            dialogBuilder.withTitle("Information")
                    .withDialogColor(Build.VERSION.SDK_INT > 21 ? ContextCompat.getColor(context, R.color.colorPrimary) : context.getResources().getColor(R.color.colorPrimary))
                    .withMessage("Your Exam-Timetable is ready.")
                    .withButton1Text(ExamLoadedFromFragment ? "Ok" : "Ok-Load").isCancelableOnTouchOutside(false).withEffect(new NifftyDialogs(context).stylepop_up())
                    .withIcon(R.drawable.ic_info_white_24dp).withDuration(700)
                    .setButton1Click(v -> {
                        if (!ExamLoadedFromFragment) {
                            doRestart(context);
                        }
                        dialogBuilder.dismiss();
                    })
                    .show();
        }

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return isExamTime ? new fragmentExam() : fragmentDay.newInstance(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public int getItemPosition(Object object) {
            Fragment f = (Fragment) object;
            for (int i = 0; i < getCount(); i++) {

                Fragment fragment = getItem(i);
                if (f.equals(fragment)) {
                    return i;
                }
            }

            return POSITION_NONE;

        }


        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void movePagerTab() {
            mFragmentList.remove(5);
            mFragmentTitleList.remove(5);
            addFrag(new fragmentDay(), settings.getTAB_SUNDAY());
            notifyDataSetChanged();

        }


        public void removeFrag(String name) {

            for (String title : mFragmentTitleList) {

                if (title.equalsIgnoreCase(name)) {
                    mFragmentList.remove(indicator);
                    mFragmentTitleList.remove(indicator);
                    notifyDataSetChanged();
                    break;
                }
                indicator++;
            }
        }

        public void renameAllFragstoAbbriviations() {
            /*
            * <h1>renames the existing tabs at the give time.</h1>
            * */

            List<String> names = new ArrayList<>();
            for (String j : mFragmentTitleList) {
                /*store all tab names store them in a temp storage*/
                names.add(j);
            }
                /*remove all the tabs */
            mFragmentList.clear();
            mFragmentTitleList.clear();
            int cantCount = 0;
            for (String day : names) {
                /*restart saving or storing them afresh using @link ViewPagerAdapter.addFrag method*/
                if (day.equalsIgnoreCase(TIMES_DAYS_FULL[cantCount])) {
                    addFrag(new fragmentDay(), TIMES_DAYS_SHORT[cantCount]);
                }
                cantCount++;
            }
            notifyDataSetChanged(); /*we finally rename the whole view*/
        }
        public void renameAllFragstoFullName() {
            /*
            * <h1>renames the existing tabs at the give time.</h1>
            * */

            List<String> names = new ArrayList<>();
            for (String j : mFragmentTitleList) {
                /*store all tab names store them in a temp storage*/
                names.add(j);
            }
                /*remove all the tabs */
            mFragmentList.clear();
            mFragmentTitleList.clear();
            int cantCount = 0;
            for (String day : names) {
                /*restart saving or storing them afresh using @link ViewPagerAdapter.addFrag method*/
                if (day.equalsIgnoreCase(TIMES_DAYS_FULL[cantCount].substring(0, 3))) {
                    addFrag(new fragmentDay(), TIMES_DAYS_FULL[cantCount]);
                }
                cantCount++;
            }
            notifyDataSetChanged(); /*we finally rename the whole view*/
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    private void setupViewPager() {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (isExamTime) {
            mTabLayout.setTabGravity(TabLayout.GRAVITY_CENTER);
            mTabLayout.setTabMode(TabLayout.MODE_FIXED);
            mTabLayout.setSelectedTabIndicatorColor(BuildsData.getColor(context, R.color.red));
            adapter.addFrag(new fragmentExam(), "Examinations");

        } else {
            boolean shortHand=settings.isTAB_ABBRIVIATED();
            adapter.addFrag(new fragmentDay(), shortHand?TIMES_DAYS_SHORT[0]:configs.TIMES_DAYS[0]);
            adapter.addFrag(new fragmentDay(), shortHand?TIMES_DAYS_SHORT[1]: configs.TIMES_DAYS[1]);
            adapter.addFrag(new fragmentDay(),  shortHand?TIMES_DAYS_SHORT[2]:configs.TIMES_DAYS[2]);
            adapter.addFrag(new fragmentDay(),  shortHand?TIMES_DAYS_SHORT[3]:configs.TIMES_DAYS[3]);
            adapter.addFrag(new fragmentDay(),  shortHand?TIMES_DAYS_SHORT[4]:configs.TIMES_DAYS[4]);
            if (!settings.getTAB_SATURDAY().isEmpty()) {
                adapter.addFrag(new fragmentDay(),  shortHand?TIMES_DAYS_SHORT[5]:settings.getTAB_SATURDAY());
            }
            if (!settings.getTAB_SUNDAY().isEmpty()) {
                adapter.addFrag(new fragmentDay(), shortHand?TIMES_DAYS_SHORT[6]: settings.getTAB_SUNDAY());
            }
        }
        mPager.setAdapter(adapter);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                BuildsData.WEEK_DAY_SELECTED = "" + position;
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //
        int TAB_INDEX_SELECT=0;
        if (!isExamTime) {


            Calendar myDate = Calendar.getInstance(); // set this up however you need it.
            int dow = myDate.get(Calendar.DAY_OF_WEEK);
            boolean isWeekday = ((dow >= Calendar.MONDAY) && (dow <= Calendar.FRIDAY));

            if (isWeekday) {
                if (dow == Calendar.MONDAY) {
                    BuildsData.WEEK_DAY = "Monday";
                    TAB_INDEX_SELECT=0;
                    mPager.setCurrentItem(TAB_INDEX_SELECT);
                } else if (dow == Calendar.TUESDAY) {
                    BuildsData.WEEK_DAY = "Tuesday";
                    TAB_INDEX_SELECT=1;
                    mPager.setCurrentItem(TAB_INDEX_SELECT);
                } else if (dow == Calendar.WEDNESDAY) {
                    BuildsData.WEEK_DAY = "Wednesday";
                    TAB_INDEX_SELECT=2;
                    mPager.setCurrentItem(TAB_INDEX_SELECT);
                } else if (dow == Calendar.THURSDAY) {
                    BuildsData.WEEK_DAY = "Thursday";
                    TAB_INDEX_SELECT=3;
                    mPager.setCurrentItem(TAB_INDEX_SELECT);
                } else if (dow == Calendar.FRIDAY) {
                    BuildsData.WEEK_DAY = "Friday";
                    TAB_INDEX_SELECT=4;
                    mPager.setCurrentItem(TAB_INDEX_SELECT);
                } else {
                    BuildsData.isWeekEnd = true;
                }

            }else{
                if (dow == Calendar.SATURDAY) {
                    if(!settings.getTAB_SATURDAY().isEmpty()){
                        BuildsData.WEEK_DAY = "Saturday";
                        TAB_INDEX_SELECT=5;
                        mPager.setCurrentItem(TAB_INDEX_SELECT);

                    }

                }else if(dow == Calendar.SUNDAY){
                    if(!settings.getTAB_SUNDAY().isEmpty()){
                        BuildsData.WEEK_DAY = "Sunday";
                        if(!settings.getTAB_SATURDAY().isEmpty()  ){
                            TAB_INDEX_SELECT=6;
                            mPager.setCurrentItem(TAB_INDEX_SELECT);
                        }else if( settings.getTAB_SATURDAY().isEmpty()  ){
                            TAB_INDEX_SELECT=5;
                            mPager.setCurrentItem(TAB_INDEX_SELECT);
                        }
                    }
                }


            }
        }
        mTabLayout.setupWithViewPager(mPager);
        mTabLayout.getTabAt(TAB_INDEX_SELECT).setIcon( R.drawable.ic_today_white_18dp);
        mPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
    }

    private void initViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);
       // getSupportActionBar().setSubtitle("(Alpha)Still under Development.");
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!Once.beenDone(Once.THIS_APP_INSTALL, "walk_via_1")) {
                    Once.markDone("walk_via_1");
                    InitUser_WalkThrough_Manual();
                }

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawer.addDrawerListener(toggle);

        toggle.syncState();
        View headerLayout = LayoutInflater.from(context).inflate(R.layout.nav_header_main, null);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.addHeaderView(headerLayout);

        userprogram = (TextView) headerLayout.findViewById(R.id.textView);
        userprogram.setText(new mSettings(context).getUSER_PROGRAME());

        CountBadges_nav_notes = (TextView) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.nav_notes));
        CountBadges_nav_notes.setGravity(Gravity.CENTER_VERTICAL);

        CountBadges_nav_notes.setText(results.size()>0?"("+results.size()+")":"");

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isExamTime) {
            if (new mSettings(context).getTAB_SUNDAY().isEmpty() && JustRemovedTAB_SUNDAY) {
                adapter.removeFrag("Sunday");
                mPager.setCurrentItem(indicator - 1);
                JustRemovedTAB_SUNDAY = false;
                indicator = 0;
             /*show the add tab icon on tool bar*/
                if (mMenu != null) {
                    MenuItem item = mMenu.findItem(R.id.action_addTab);
                    boolean visibitytest = true;
                    if (!new mSettings(context).getTAB_SUNDAY().isEmpty() && !new mSettings(context).getTAB_SATURDAY().isEmpty()) {
                        visibitytest = false;
                    }
                    item.setVisible(visibitytest);
                }
            }
            if (new mSettings(context).getTAB_SATURDAY().isEmpty() && JustRemovedTAB_SARTARDAY) {
                adapter.removeFrag("Saturday");
                JustRemovedTAB_SARTARDAY = false;
                mPager.setCurrentItem(indicator - 1);
                indicator = 0;
           /*show the add tab icon on tool bar*/
                if (mMenu != null) {
                    MenuItem item = mMenu.findItem(R.id.action_addTab);
                    boolean visibitytest = true;
                    if (!new mSettings(context).getTAB_SUNDAY().isEmpty() && !new mSettings(context).getTAB_SATURDAY().isEmpty()) {
                        visibitytest = false;
                    }
                    item.setVisible(visibitytest);
                }
            }
            if (new mSettings(context).isTAB_ABBRIVIATED() && JustSetRenamedFragNamings) {
                JustSetRenamedFragNamings = false;
                Toast.makeText(context, "Renamed to short Tab names", Toast.LENGTH_SHORT).show();
                adapter.renameAllFragstoAbbriviations();
            }
            if (!new mSettings(context).isTAB_ABBRIVIATED() && JustSetRenamedFragNamings) {
                JustSetRenamedFragNamings = false;
                adapter.renameAllFragstoFullName();
                Toast.makeText(context, "Renamed to full Tab names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // super.onBackPressed();
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit.",Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(() -> exit = false, 3 * 1000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!new mSettings(this).getTAB_SUNDAY().isEmpty() && !new mSettings(this).getTAB_SATURDAY().isEmpty()) {
            menu.findItem(R.id.action_addTab).setVisible(false);
        }
        if (isExamTime) {
            menu.findItem(R.id.action_addTab).setVisible(false);
        }

        mMenu = menu;
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.removeChangeListener(realmListener);
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            startActivity(new Intent(this, Settinngs.class));
            return true;
        }

        if (id == R.id.action_addTab) {
            addTab();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void SetExamTimeTable() {

        ShowPorgressDialog(true);
        new NetGetExamTimeTable(this).execute(settings.getUSER_SCHOOL(), settings.getUSER_PROGRAME(), settings.getUSER_LEVEL());
    }

    public void ShowPorgressDialog(boolean shouldshow) {
        if (shouldshow) {
            if (!dialog.isShowing()) {
                dialog.setIndeterminate(true);
                dialog.setCancelable(false);
                dialog.setMessage("Loading. Please wait...");
                dialog.show();
            }
        } else {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }

    }



    private void addTab() {
        wasSat_Day_there = settings.getTAB_SATURDAY().isEmpty();
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.weekends, null);
        final String[] weekend = settings.getTAB_SATURDAY().isEmpty() && settings.getTAB_SUNDAY().isEmpty() ?
                new String[]{BuildsData.WEEKEND_DAYS[0], BuildsData.WEEKEND_DAYS[1]} :
                (settings.getTAB_SATURDAY().isEmpty() && !settings.getTAB_SUNDAY().isEmpty() ?
                        new String[]{BuildsData.WEEKEND_DAYS[0]} :
                        new String[]{BuildsData.WEEKEND_DAYS[1]}
                );// new String[]{""};//settings.getTAB_SATURDAY().isEmpty()?{BuildsData.WEEKEND_DAYS[0]}:BuildsData.WEEKEND_DAYS[0];
        final Spinner spinnerClassDay = (Spinner) content.findViewById(R.id.spinnerClassDay);
        ArrayList<String> temp1 = new ArrayList<>();
        Collections.addAll(temp1, weekend);
        final String[] weeke_endDay = {""};
        final CustomSpinnerAdapter dataAdapter2 = new CustomSpinnerAdapter(this, temp1);
        spinnerClassDay.setAdapter(dataAdapter2);
        spinnerClassDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                weeke_endDay[0] = weekend[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(content)
                .setTitle("Add New Tab")
                .setCancelable(false)
                .setPositiveButton("Add", (dialog1, which) -> {
                    boolean shortHand=settings.isTAB_ABBRIVIATED();

                    if(shortHand && weeke_endDay[0].equalsIgnoreCase("Saturday")){
                        adapter.addFrag(new fragmentDay(),TIMES_DAYS_SHORT[5] );
                    }else  if(shortHand && weeke_endDay[0].equalsIgnoreCase("Sunday")){
                        adapter.addFrag(new fragmentDay(),TIMES_DAYS_SHORT[6]);
                    }else{
                        adapter.addFrag(new fragmentDay(), weeke_endDay[0]);
                    }
                    adapter.notifyDataSetChanged();
                    HowmanyTime_Tabs_Added++;

                    if (HowmanyTime_Tabs_Added == 2 && mPager.getCurrentItem() == 5) {
                        mPager.setCurrentItem(6);
                    } else {
                        mPager.setCurrentItem(5 + HowmanyTime_Tabs_Added);
                    }
                    if (weeke_endDay[0].contentEquals(BuildsData.WEEKEND_DAYS[0])) {
                        settings.setTAB_SATURDAY(weeke_endDay[0]);
                        new SeeToast().message_short(context, "Created Tab for " + weeke_endDay[0]);
                    } else if (weeke_endDay[0].contentEquals(BuildsData.WEEKEND_DAYS[1])) {
                        settings.setTAB_SUNDAY(weeke_endDay[0]);
                        new SeeToast().message_short(context, "Created Tab for " + weeke_endDay[0]);
                    }
                    if (mMenu != null) {
                        MenuItem item = mMenu.findItem(R.id.action_addTab);
                        boolean visibitytest = true;
                        if (!new mSettings(context).getTAB_SUNDAY().isEmpty() && !new mSettings(context).getTAB_SATURDAY().isEmpty()) {
                            visibitytest = false;
                            if (wasSat_Day_there) {
                                adapter.movePagerTab();
                            }

                        }
                        item.setVisible(visibitytest);
                    }

                })
                .setNegativeButton(android.R.string.cancel, (dialog12, which) -> dialog12.dismiss());
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
        dialog.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_notications) {
            // Handle the camera action
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_install) {
            BuildsData.IS_INSTALL_FROM_DRAWER = true;
            startActivity(new Intent(this, WelcomeSetup.class));

        } else if (id == R.id.nav_about) {
            startActivity(new Intent(this, About.class));
        } else if (id == R.id.nav_exam) {
          //  SetExamTimeTable();
            app.setExamMark();
            doRestart(context);

        }else if(id==R.id.nav_study_tips){
            startActivity(new Intent(this, StudyTips.class));
        }
        if (id == R.id.nav_notes) {
            startActivity(new Intent(this, NotesListings.class));
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
