package fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import Adapters.ClassLectureRecycler;
import BuildsConfigs.BuildsData;
import DBAccess.Preffs.mSettings;
import DBAccess.RealmDB.CRUD;
import DBAccess.RealmDB.ClassesLecture;
import DBAccess.RealmDB.ExaminationDB;
import Messages.NifftyDialogs;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import jonathanfinerty.once.Once;
import kinsleykajiva.co.zw.cutstudentapp.MyApplication;
import kinsleykajiva.co.zw.cutstudentapp.R;
import widgets.CustomSpinnerAdapter;
import widgets.LinearItermDecorator;
import widgets.MyRecyclerItemClickListener;

import static BuildsConfigs.BuildsData.EXAM_TIME_KEY;
import static BuildsConfigs.BuildsData.WEEKEND_DAYS;
import static BuildsConfigs.BuildsData.doRestart;
import static BuildsConfigs.BuildsData.isExamTime;

/**
 * Created by kinsley kajiva on 11/8/2016.Zvaganzirwa nakinsley kajiva musiwa 11/8/2016
 */
public class fragmentDay extends Fragment {

    private Menu menu;
    private NavigationView navigationView;
    private MenuItem nav_exam,nav_install;

    private LinearLayout selectTimes;
    private static String Time_Picked = "07:00:00";
    private static EditText tutorialTimePicker1;
    private Realm myRealm;
    private LayoutInflater inflater;
    private RecyclerView mRecyclerView;
    private ClassLectureRecycler adapter;
    private BuildsData configs = new BuildsData();
    private RealmResults results;


    private String DAY_IN_VIEW = null;
    private String DAY_SPINNER_SELECT = "";
    private static int ORDER_CLASS;
    private static String DAY_ON_SCREEN = "day_what";
    private FloatingActionButton fab;
    private View layout;
    private TextView recycler_state;
    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            adapter.update(results);

            recycler_state.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);
        }
    };
    private static IntentFilter s_intentFilter;
    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        // s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }
    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
                    action.equals(Intent.ACTION_TIMEZONE_CHANGED))
            {
                Log.e("xxx", "onReceive:->  changed now");

            }
        }
    };
    public static fragmentDay newInstance(int position) {
        fragmentDay f = new fragmentDay();
        Bundle b = new Bundle();
        b.putInt(DAY_ON_SCREEN, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initObjects();
       // Log.e("xxx", "onCreate:->  "+recursiveFactoriall(5));

    }

    private void initObjects() {
        myRealm = Realm.getDefaultInstance();
        InitPassedValues("" + (getArguments().getInt(DAY_ON_SCREEN) + 1));

            results = myRealm.where(ClassesLecture.class).equalTo("classDay", DAY_IN_VIEW)
                    .findAllSortedAsync(/*"classOrder"*/"classStartTime", Sort.ASCENDING);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_day, container, false);
        initViews();
        setUpRecyclerview();
        setOnclickListners();
        if(!myRealm.where(ExaminationDB.class).findAll().isEmpty()){
            nav_exam.setTitle("Go to Exam-Timetable");
            nav_exam.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    MyApplication  app = (MyApplication) getActivity().getApplication();
                    app.setExamMark();
                    doRestart(getContext());
                    return false;
                }
            });
        }


      //  getActivity().registerReceiver(m_timeChangedReceiver, s_intentFilter);
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        results.addChangeListener(realmChangeListener);
    }

    private void setOnclickListners() {
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AddnewClass();
            }

        });

    }

    @Override
    public void onDestroy() {
        results.removeChangeListener(realmChangeListener);
        myRealm.close();
        super.onDestroy();
       // getActivity().unregisterReceiver(m_timeChangedReceiver);
    }

    private void setUpRecyclerview() {
        adapter = new ClassLectureRecycler(results, getActivity(),getFragmentManager());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new LinearItermDecorator(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());


    }


    int recursiveFactoriall(int $num){
        return $num < 0?-1:$num == 0?1:$num * recursiveFactoriall($num-1);
    }

    private void InitPassedValues(String passedValue) {
        if (passedValue.equalsIgnoreCase("5")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[4];
        } else if (passedValue.equalsIgnoreCase("4")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[3];
        } else if (passedValue.equalsIgnoreCase("3")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[2];
        } else if (passedValue.equalsIgnoreCase("2")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[1];
        } else if (passedValue.equalsIgnoreCase("1")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[0];
        } else if (passedValue.equalsIgnoreCase("7")) {
            DAY_IN_VIEW =  WEEKEND_DAYS[1]; // this is sunday
        } else if (passedValue.equalsIgnoreCase("6")) {
            DAY_IN_VIEW =  WEEKEND_DAYS[0]; // this is saturday
        }

    }

    private String PassedValues(String passedValue) {
        if (passedValue.equalsIgnoreCase("4")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[4];
        } else if (passedValue.equalsIgnoreCase("3")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[3];
        } else if (passedValue.equalsIgnoreCase("2")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[2];
        } else if (passedValue.equalsIgnoreCase("1")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[1];
        } else if (passedValue.equalsIgnoreCase("0")) {
            DAY_IN_VIEW = configs.TIMES_DAYS[0];
        }else if (passedValue.equalsIgnoreCase("5")) {

            if(!new mSettings(getContext()).getTAB_SATURDAY().isEmpty()){
                DAY_IN_VIEW = WEEKEND_DAYS[0]; // this is saturday
            }else{
                if(!new mSettings(getContext()).getTAB_SUNDAY().isEmpty()){
                    DAY_IN_VIEW = WEEKEND_DAYS[1]; //this is a sunday
                }
            }
        }else if (passedValue.equalsIgnoreCase("6") ) {

            DAY_IN_VIEW = WEEKEND_DAYS[1]; //this is a sunday
            /*if(!new mSettings(getContext()).getTAB_SUNDAY().isEmpty()  ) {
                 DAY_IN_VIEW = WEEKEND_DAYS[1]; //this is a sunday
            }else{
                if(!new mSettings(getContext()).getTAB_SATURDAY().isEmpty()){
                    DAY_IN_VIEW = WEEKEND_DAYS[0]; // this is saturday
                }
            }*/

        }

        return DAY_IN_VIEW;

    }


    CustomSpinnerAdapter dataAdapter;
    private static boolean isTutorial = true;

    private void initViews() {
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        nav_exam= menu.findItem(R.id.nav_exam);
        selectTimes = (LinearLayout) layout.findViewById(R.id.selectTimes);

        recycler_state = (TextView) layout.findViewById(R.id.recycler_state);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_viewClass);
        recycler_state.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);


    }

    private int getIndex(Spinner spinner, String myString) {
        int index = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                index = i;
                break;
            }
        }
        return index;
    }

    static String[] Time_Select_start = {""};
    static String[] Time_Select_end = {""};

    private void AddnewClass() {


        final String[] classTypeSelected = new String[1];
        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.edit_item, null);
        final String[] classarray = getResources().getStringArray(R.array.classtime_arrays);

        final Spinner spinnerClassTime = (Spinner) content.findViewById(R.id.spinnerClassTime);
        spinnerClassTime.setEnabled(false);
        final Spinner spinnerClassDay = (Spinner) content.findViewById(R.id.spinnerClassDay);
        final Spinner spinnerClassType = (Spinner) content.findViewById(R.id.spinnerClassType);//spinnerClassType
        final EditText className_edit = (EditText) content.findViewById(R.id.className_edit);
        final EditText classVenue_edit = (EditText) content.findViewById(R.id.classVenue_edit);
        tutorialTimePicker1 = (EditText) content.findViewById(R.id.tutorialTimePicker1);
        tutorialTimePicker1.setText("07:00");
        tutorialTimePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tutorialTimePicker1.setShowSoftInputOnFocus(false);

                new TimePickerFragment().show(getFragmentManager(), "timePicker");
            }
        });
        tutorialTimePicker1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = tutorialTimePicker1.getInputType(); // backup the input type
                tutorialTimePicker1.setInputType(InputType.TYPE_NULL); // disable soft input
                tutorialTimePicker1.onTouchEvent(event); // call native handler
                tutorialTimePicker1.setInputType(inType); // restore input type
                return true; // consume touch even
            }
        });
        ArrayList<String> temp1=new ArrayList<>();
        for (String v : classarray) {temp1.add(v); }

        dataAdapter = new CustomSpinnerAdapter(getActivity(),  temp1);

        spinnerClassTime.setAdapter(dataAdapter);
        //
        final ArrayList<String> temp2=new ArrayList<>();
        for (String v : configs.TIMES_DAYS) {temp2.add(v); }
        if(!new mSettings(getContext()).getTAB_SATURDAY().isEmpty() && !new mSettings(getContext()).getTAB_SUNDAY().isEmpty()){
            temp2.add("Saturday");
            temp2.add("Sunday");
        }else if(!new mSettings(getContext()).getTAB_SATURDAY().isEmpty() && new mSettings(getContext()).getTAB_SUNDAY().isEmpty() ){
            temp2.add("Saturday");
        }if(new mSettings(getContext()).getTAB_SATURDAY().isEmpty() && !new mSettings(getContext()).getTAB_SUNDAY().isEmpty() ){
            temp2.add("Sunday");
        }

        final CustomSpinnerAdapter dataAdapter2 =  new CustomSpinnerAdapter(getActivity(),temp2  );

        spinnerClassDay.setAdapter(dataAdapter2);
        spinnerClassDay.setSelection(getIndex(spinnerClassDay, PassedValues(BuildsData.WEEK_DAY_SELECTED)), true);
        DAY_SPINNER_SELECT = PassedValues(BuildsData.WEEK_DAY_SELECTED);
        //
        ArrayList<String> temp3=new ArrayList<>();
        for (String v : configs.TYPE_OF_CLASS) {temp3.add(v); }
        final  CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(getActivity(),temp3  );

        spinnerClassType.setAdapter(dataAdapter3);
        //
        spinnerClassTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                    if (position == 0) {
                        Time_Select_start[0] = configs.TIMES_PERIOD_START[position];
                        Time_Select_end[0] = configs.TIMES_PERIOD_END[position];
                        ORDER_CLASS = configs.CLASS_ORDER[position];

                    } else if (position == 1) {
                        Time_Select_start[0] = configs.TIMES_PERIOD_START[position];
                        Time_Select_end[0] = configs.TIMES_PERIOD_END[position];
                        ORDER_CLASS = configs.CLASS_ORDER[position];

                    } else if (position == 2) {
                        Time_Select_start[0] = configs.TIMES_PERIOD_START[position];
                        Time_Select_end[0] = configs.TIMES_PERIOD_END[position];
                        ORDER_CLASS = configs.CLASS_ORDER[position];


                    } else if (position == 3) {
                        Time_Select_start[0] = configs.TIMES_PERIOD_START[position];
                        Time_Select_end[0] = configs.TIMES_PERIOD_END[position];
                        ORDER_CLASS = configs.CLASS_ORDER[position];


                    } else if (position == 4) {
                        Time_Select_start[0] = configs.TIMES_PERIOD_START[position];
                        Time_Select_end[0] = configs.TIMES_PERIOD_END[position];
                        ORDER_CLASS = configs.CLASS_ORDER[position];


                    }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerClassDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DAY_SPINNER_SELECT=   temp2.get(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //

        spinnerClassType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position == 0) { //tutorial

                    classTypeSelected[0] = configs.TYPE_OF_CLASS[position];

                    spinnerClassTime.setEnabled(false);
                    tutorialTimePicker1.setEnabled(true);
                    spinnerClassTime.setBackgroundResource(R.drawable.background_error);
                    tutorialTimePicker1.setBackgroundResource(R.drawable.custom_spinner_background);

                    isTutorial = true;

                } else if (position == 1) { //lecture
                    classTypeSelected[0] = configs.TYPE_OF_CLASS[position];

                    isTutorial = false;
                    spinnerClassTime.setEnabled(true);
                    tutorialTimePicker1.setEnabled(false);

                    spinnerClassTime.setBackgroundResource(R.drawable.custom_spinner_background);
                    tutorialTimePicker1.setBackgroundResource(R.drawable.background_error);


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content)
                .setTitle("Add a New class.")
                .setCancelable(false)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        if (!className_edit.getText().toString().trim().isEmpty() && !classVenue_edit.getText().toString().trim().isEmpty()) {
                            if (CRUD.isClassAvailable(className_edit.getText().toString().trim())) {
                                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                                dialogBuilder
                                        .withTitle("Failed !!")
                                        .withTitleColor("#FFFFFF")
                                        .withDividerColor("#727272")
                                        .withIcon(R.drawable.ic_info_white_24dp)
                                        .withMessage("There is already a class with that name.")
                                        .withMessageColor("#FFFFFFFF")
                                        .withDialogColor("#FFE74C3C")
                                        .isCancelableOnTouchOutside(false)
                                        .withDuration(700)
                                        .withEffect(Effectstype.Fadein)
                                        .withButton1Text("OK")
                                        .isCancelableOnTouchOutside(true)
                                        .setButton1Click(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                dialogBuilder.dismiss();
                                                AddnewClass();
                                            }
                                        })

                                        .show();
                            } else {
                               // Log.e("xxx", "onClick:->  day is what "+DAY_SPINNER_SELECT);
                                new CRUD().writeToDb(
                                        DAY_SPINNER_SELECT,
                                        Time_Select_start[0],
                                        Time_Select_end[0],
                                        className_edit.getText().toString().trim(),
                                        classVenue_edit.getText().toString().trim(),
                                        "" + ORDER_CLASS,
                                        configs.IS_REMINDER_SET[0],
                                        classTypeSelected[0]

                                );
                                setUpRecyclerview();
                                dialog.dismiss();
                            }

                        } else {


                            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(getActivity());
                            dialogBuilder
                                    .withTitle("Failed !!")
                                    .withTitleColor("#FFFFFF")
                                    .withDividerColor("#727272")
                                    .withIcon(R.drawable.ic_info_white_24dp)
                                    .withMessage("Please put details needed")
                                    .withMessageColor("#FFFFFFFF")
                                    .withDialogColor("#FFE74C3C")
                                    .isCancelableOnTouchOutside(false)
                                    .withDuration(700)
                                    .withEffect(Effectstype.Fadein)
                                    .withButton1Text("OK")
                                    .isCancelableOnTouchOutside(true)
                                    .setButton1Click(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialogBuilder.dismiss();
                                            AddnewClass();
                                        }
                                    })

                                    .show();

                        }


                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
        dialog.show();

    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user

            Calendar now = Calendar.getInstance();
            now.set(Calendar.HOUR_OF_DAY,hourOfDay);
            now.set(Calendar.MINUTE,minute);
           Time_Picked  = new SimpleDateFormat("HH:mm:ss").format(now.getTime());

            Time_Select_start[0] = Time_Picked;
            Time_Select_end[0] = BuildsData.incrementTime(Time_Picked);
            ORDER_CLASS = 1;/*remove this when ever there are real issues with ordering*/
            /*i have made this attribute reduntent as i am now using time for odering in my realm*/

            tutorialTimePicker1.setText(BuildsData.RemoveLastChars(Time_Picked,3));

        }
    }
}