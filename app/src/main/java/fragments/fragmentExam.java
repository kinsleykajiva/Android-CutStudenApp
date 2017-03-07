package fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;


import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;
import com.nispok.snackbar.listeners.EventListener;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import Adapters.ExamRecycler;
import BuildsConfigs.BuildsData;

import DBAccess.RealmDB.ExaminationDB;
import Messages.NifftyDialogs;
import Messages.SeeToast;
import Pojo.General_item;
import Pojo.ListItem;
import Pojo.Week_item;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import jonathanfinerty.once.Once;
import kinsleykajiva.co.zw.cutstudentapp.R;
import widgets.CustomSpinnerAdapter;
import widgets.LinearItermDecorator;

import static BuildsConfigs.BuildsData.EXAM_TIME_KEY;
import static BuildsConfigs.BuildsData.doRestart;
import static BuildsConfigs.BuildsData.getColor;
import static BuildsConfigs.BuildsData.getResourceDrawable;
import static BuildsConfigs.BuildsData.isExamTime;
import static DBAccess.RealmDB.CRUD.DeleteAllExams;
import static DBAccess.RealmDB.CRUD.initWriteExamination;
import static DBAccess.RealmDB.CRUD.isExamAlreadySaved;
import static DBAccess.RealmDB.CRUD.writeExam;
import static jonathanfinerty.once.Once.THIS_APP_INSTALL;
import static jonathanfinerty.once.Once.markDone;


/**
 * Created by Kinsley Kajiva on 12/31/2016.
 */

public class fragmentExam extends Fragment {
private Menu menu;
    private NavigationView navigationView;
    private MenuItem nav_exam,nav_install;

    private static String Time_Picked = "",examDate="";
    private static EditText timpicker,datePicker;
    private Realm myRealm;
    private RecyclerView mRecyclerView;
    private ExamRecycler adapter;
    private TextView recycler_state;
    List<ListItem> consolidatedList ;
    private Context context;
    private static String DAY_SELECT = "";
    static String[] Time_Select_start = {""};
    static String[] Time_Select_end = {""};

    private RealmResults<ExaminationDB> results;
    private FloatingActionButton fab;
    private View layout;

    private Paint p = new Paint();
    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            processRecyclerAdapter( results );
            adapter.update(consolidatedList);
           // nav_install.setTitle(results.isEmpty()?"Go to Timetable":"Timetable");
            recycler_state.setVisibility(results.isEmpty() ? View.VISIBLE : View.GONE);

        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getContext();
        initObjects();

    }
    public void forceCrash() {
        throw new RuntimeException("This is a crash");

    }

    private void initObjects() {
        BuildsData.ExamLoadedFromFragment=true;
        myRealm = Realm.getDefaultInstance();
        results = myRealm.where(ExaminationDB.class)
                .findAllSorted("dDate", Sort.ASCENDING);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.fragment_day, container, false);
        initViews();
        setUpRecyclerview();
        setOnclickListners();
        initSwipe();
        return layout;
    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT /*| ItemTouchHelper.RIGHT*/){

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position=viewHolder.getAdapterPosition();
                General_item generalItem = (General_item)consolidatedList.get(position);
                final boolean[] unDone = {false};
                if(direction == ItemTouchHelper.LEFT){   ///
                    adapter.removeItem(position);
                    myRealm.beginTransaction();
                    RealmResults<ExaminationDB> rs=myRealm.where(ExaminationDB.class).equalTo("ID",generalItem.getPojoArray().getID()).findAll();
                    rs.deleteAllFromRealm();
                    myRealm.commitTransaction();
                    SnackbarManager.show(Snackbar.with(context).text( generalItem.getPojoArray().getName()+" (Deleted) Marked as Done")
                    .actionLabel("").actionColor(getColor(context,R.color.red)).actionListener(new ActionClickListener() {
                                        @Override
                                        public void onActionClicked(Snackbar snackbar) {
                                            unDone[0] =true;
                                            new SeeToast().message_short(context,"Undone");
                                        }
                                    }).eventListener(new EventListener() {
                        @Override
                        public void onShow(Snackbar snackbar) {
                            //fab.offsetTopAndBottom(snackbar.getHeight());
                        }

                        @Override
                        public void onShowByReplace(Snackbar snackbar) {

                        }

                        @Override
                        public void onShown(Snackbar snackbar) {

                        }

                        @Override
                        public void onDismiss(Snackbar snackbar) {
                            if(!unDone[0]) {

                            }
                            fab.offsetTopAndBottom(snackbar.getHeight());
                        }

                        @Override
                        public void onDismissByReplace(Snackbar snackbar) {

                        }

                        @Override
                        public void onDismissed(Snackbar snackbar) {

                        }
                    }),getActivity()
                    );

                }
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (ListItem.TYPE_DATE == consolidatedList.get(viewHolder.getAdapterPosition()).getType()) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                    Bitmap icon;
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        View itemView = viewHolder.itemView;
                        float height = (float) itemView.getBottom() - (float) itemView.getTop();
                        float width = height / 3;
                        if (dX > 0) {
                            p.setColor(getColor(context, R.color.red));
                            RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                            c.drawRect(background, p);
                            icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                            RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                            c.drawBitmap(icon, null, icon_dest, p);
                        } else if (dX < 0) {
                            p.setColor(Color.parseColor("#D32F2F"));
                            RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                            c.drawRect(background, p);
                            icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                            RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                            c.drawBitmap(icon, null, icon_dest, p);
                        }
                    }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    @Override
    public void onStart() {
        super.onStart();
        results.addChangeListener(realmChangeListener);
    }

    @Override
    public void onDestroy() {
        results.removeChangeListener(realmChangeListener);
        myRealm.close();
        super.onDestroy();
    }

    private void setOnclickListners() {
        fab.setOnClickListener(v -> {
         //   DeleteAllExams();
            AddnewExam();
        });

    }

    private void AddnewExam() {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.edit_item_exam, null);
        timpicker = (EditText) content.findViewById(R.id.tutorialTimePicker1);
        datePicker = (EditText) content.findViewById(R.id.tutorialDatePicker1);
        final EditText className_edit = (EditText) content.findViewById(R.id.className_edit);
        final EditText classVenue_edit = (EditText) content.findViewById(R.id.classVenue_edit);




        timpicker.setOnClickListener(view -> new TimePickerFragment().show(getFragmentManager(), "timePicker"));
        datePicker.setOnClickListener(view -> new DatePickerFragment().show(getFragmentManager(), "datePicker"));
        timpicker.setOnTouchListener((v, event) -> {
            int inType = timpicker.getInputType(); // backup the input type
            timpicker.setInputType(InputType.TYPE_NULL); // disable soft input
            timpicker.onTouchEvent(event); // call native handler
            timpicker.setInputType(inType); // restore input type
            return true; // consume touch even
        });
        datePicker.setOnTouchListener((v, event) -> {
            int inType = timpicker.getInputType(); // backup the input type
            datePicker.setInputType(InputType.TYPE_NULL); // disable soft input
            datePicker.onTouchEvent(event); // call native handler
            datePicker.setInputType(inType); // restore input type
            return true; // consume touch even
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(content)
                .setTitle("Add Exam.")
                .setCancelable(false)
                .setPositiveButton("Add", (dialog, which) -> {
                    if (!datePicker.getText().toString().trim().isEmpty() && !timpicker.getText().toString().trim().isEmpty() && !className_edit.getText().toString().trim().isEmpty() && !classVenue_edit.getText().toString().trim().isEmpty()) {
                        ArrayList<ExaminationDB> tp = new ArrayList<>();
                        tp.add(new ExaminationDB(DAY_SELECT, Time_Select_start[0], Time_Select_end[0], classVenue_edit.getText().toString().trim(), className_edit.getText().toString().trim(), examDate, 0, false, false));
                        if(isExamAlreadySaved(className_edit.getText().toString().trim())){
                            new NifftyDialogs(getContext()).messageOkError("Error","That Exam Module already exist.Delete then Add.");
                        }else{
                            initWriteExamination(tp);
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
                                .setButton1Click(v -> {

                                    dialogBuilder.dismiss();
                                    AddnewExam();
                                })

                                .show();

                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
      dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
        dialog.show();

    }

    private void setUpRecyclerview() {



        processRecyclerAdapter( results );

        adapter = new ExamRecycler(consolidatedList, getActivity(),getFragmentManager());
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new LinearItermDecorator(getActivity(), LinearLayoutManager.VERTICAL));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
    public void processRecyclerAdapter( RealmResults<ExaminationDB> results ){
        consolidatedList = new ArrayList<>();
        HashMap<String, List<ExaminationDB>> groupedHashMap = groupDataIntoHashMap(results);
        for (String week : groupedHashMap.keySet()) {

            Week_item week_item = new Week_item();
            week_item.setWeek(week);
            consolidatedList.add(week_item);

            for (ExaminationDB pojoOfArray : groupedHashMap.get(week)) {
                General_item generalItem = new General_item();
                generalItem.setPojoArray(pojoOfArray);
                consolidatedList.add(generalItem);
            }
        }

    }
    private void initViews() {

        recycler_state = (TextView) layout.findViewById(R.id.recycler_state);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        menu = navigationView.getMenu();
        nav_exam= menu.findItem(R.id.nav_exam);
        nav_install= menu.findItem(R.id.nav_install);

        recycler_state.setVisibility(results.isEmpty()?View.VISIBLE:View.GONE);

        if(isExamTime) {
            nav_install.setTitle("Go to Timetable");
            nav_install.setIcon(BuildsData.getResourceDrawable(context,R.drawable.ic_class_black_24dp));
            nav_exam.setVisible(false);
        }
        nav_install.setOnMenuItemClickListener(menuItem -> {
            Once.clearDone(EXAM_TIME_KEY);
            doRestart(context);
            return false;
        });
        mRecyclerView = (RecyclerView) layout.findViewById(R.id.recycler_viewClass);

        if (!Once.beenDone(THIS_APP_INSTALL, "red_swipe")) {
            markDone("red_swipe");
            final Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
            final Drawable droid = getResourceDrawable(context, R.drawable.ic_class_black_24dp);//getResources().getgetDrawable(R.drawable.ic_class_black_24dp, getTheme());
            final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);

            droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);

            TapTargetSequence sequence = new TapTargetSequence(getActivity())
                    .targets(
                            TapTarget.forBounds(droidTarget, "Tip", "Swipe From Right to Left to delete items.")
                                    .cancelable(true)
                                    .icon(droid)
                                    .id(1)
                    );
            sequence.start();
        }
    }
    private HashMap<String, List<ExaminationDB>> groupDataIntoHashMap(RealmResults<ExaminationDB> result) {

        List<ExaminationDB> pojoArray=myRealm.copyFromRealm(result);

        HashMap<String, List<ExaminationDB>> groupedHashMap = new HashMap<>();

        for (ExaminationDB pojoOfJsonArray : pojoArray) {

            String hashMapKey = pojoOfJsonArray.getWeek();


            if (groupedHashMap.containsKey(hashMapKey)) {
                // The key is already in the HashMap; add the pojo object
                // against the existing key.
                groupedHashMap.get(hashMapKey).add(pojoOfJsonArray);
            } else {
                // The key is not there in the HashMap; create a new key-value pair
                List<ExaminationDB> list = new ArrayList<>();
                list.add(pojoOfJsonArray);
                groupedHashMap.put(hashMapKey, list);
            }
        }


        return groupedHashMap;
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
            Time_Select_end[0] = BuildsData.incrementTime(Time_Picked,3);

            Time_Select_end[0]= BuildsData.RemoveLastChars(Time_Select_end[0],3);
            Time_Select_start[0]= BuildsData.RemoveLastChars(Time_Select_start[0],3);

            timpicker.setText(BuildsData.RemoveLastChars(Time_Picked,3));


        }
    }
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
             Calendar c = Calendar.getInstance();
            c.set(year,month,day);
            examDate=(month + 1) + "/" +day  + "/" + year;
            DAY_SELECT=new DateFormatSymbols().getWeekdays()[c.get(Calendar.DAY_OF_WEEK)];
            datePicker.setText(examDate );
        }
    }
}
