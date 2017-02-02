package Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static jonathanfinerty.once.Once.THIS_APP_INSTALL;
import static jonathanfinerty.once.Once.beenDone;
import static jonathanfinerty.once.Once.markDone;

import BuildsConfigs.BuildsData;
import DBAccess.Preffs.mSettings;
import DBAccess.RealmDB.CRUD;
import DBAccess.RealmDB.ClassesLecture;
import Messages.NifftyDialogs;
import fragments.fragmentDay;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;
import io.realm.Realm;
import io.realm.RealmResults;
import jonathanfinerty.once.Once;
import kinsleykajiva.co.zw.cutstudentapp.R;
import mServices.AlarmConfigs;
import widgets.CustomSpinnerAdapter;


/**
 * Created by kinsley kajiva on 6/29/2016.Zvaganzirwa nakinsley kajiva musiwa 6/29/2016
 */
public class ClassLectureRecycler extends RecyclerView.Adapter<ClassLectureRecycler.CustomViewHolder> {
    private RealmResults<ClassesLecture> feedItemList;

    private Context mContext;
    private BuildsData configs = new BuildsData();
    private CRUD crud = new CRUD();
    private LayoutInflater inflater;
    private static int ORDER_CLASS;
    private String DAY_SPINNER_SELECT = "";
    public static final int LECTURE_CLASS = 1;
    public static final int TUTORIAL_CLASS = 0;
    private AlarmConfigs Myalarm;
    private int rowIdTOEdit;

    private FragmentManager fragmentManager;


    public ClassLectureRecycler(RealmResults<ClassesLecture> feedItemList, Context mContext, FragmentManager fragmentManager) {
        update(feedItemList);
        this.mContext = mContext;
        this.Myalarm = new AlarmConfigs(mContext);
        this.fragmentManager = fragmentManager;


    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView classStartTime, classEndTime, classVenue, classModuleName, classstate,alamset_state;
        public ImageView overflow;
         LinearLayout itemlayout;
        public CustomViewHolder(View view) {
            super(view);
            this.itemlayout=(LinearLayout) view.findViewById(R.id.itemlayout);
            this.classStartTime = (TextView) view.findViewById(R.id.sample_item_index_text_view11);
            this.classEndTime = (TextView) view.findViewById(R.id.sample_item_index_text_view1);
            this.classModuleName = (TextView) view.findViewById(R.id.sample_item_name_text_view); //
            this.classstate = (TextView) view.findViewById(R.id.classstate);
            this.classVenue = (TextView) view.findViewById(R.id.textView);
            this.overflow = (ImageView) view.findViewById(R.id.imageView2);
            this.alamset_state = (TextView) view.findViewById(R.id.alamset_state);
        }

    }//endof class

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return  viewType == TUTORIAL_CLASS?
                new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lecture_row, null))
                :
                new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tutorial_row, null));
    }

    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final ClassesLecture feedItem = feedItemList.get(position);
        String ClasssTypeof = feedItem.getTypeOfClass();
        String t1 = feedItem.getClassStartTime(), t2 = feedItem.getClassEndTime();
        String dateDay = feedItem.getClassDay();
        holder.classStartTime.setText(t1.substring(0, t1.length() - 3));


        //this is an initial setting of class type(e.g lecture or tutorial) but is open to changing value
        // if the time zones are triggered to check if the time is within time range to set colorings
        if (ClasssTypeof.equalsIgnoreCase(configs.TYPE_OF_CLASS[0])) {
            holder.classstate.setText(configs.TUTORIAL_CLASS_STATE[0]);
        } else {
            holder.classstate.setText(configs.LECTURE_CLASS_STATE[0]);
        }
        //
        if (configs.isTimeBetweenTwoTime(t1, t2) && (BuildsData.WEEK_DAY.equalsIgnoreCase(dateDay))/*&&  BuildsData.isWeekEnd*/) {
            if (!Once.beenDone(THIS_APP_INSTALL, "class_started")) {
                markDone("class_started");

                SimpleTooltip tooltip1 = new SimpleTooltip.Builder(mContext)
                        .anchorView(holder.classStartTime)
                        .text("Red->Class has  started")
                        .gravity(Gravity.TOP)
                        .dismissOnOutsideTouch(true)
                        .dismissOnInsideTouch(true)
                        .textColor(BuildsData.getColor(mContext, R.color.white))
                        .animated(true)
                        .build();
                tooltip1.show();
            }
            holder.classStartTime.setBackgroundColor(BuildsData.getColor(mContext, R.color.onGoin));
            if (ClasssTypeof.equalsIgnoreCase(configs.TYPE_OF_CLASS[0])) {
                holder.classstate.setText(configs.TUTORIAL_CLASS_STATE[1]);
            } else {
                holder.classstate.setText(configs.LECTURE_CLASS_STATE[1]);
            }
            if (configs.isTimeBetweenTwoTime(configs.getTimedifference(t1, t2), t2) && (BuildsData.WEEK_DAY.equalsIgnoreCase(dateDay))/*&&  BuildsData.isWeekEnd*/) {
                holder.classEndTime.setBackgroundColor(BuildsData.getColor(mContext, R.color.almostDone));
                if (!Once.beenDone(THIS_APP_INSTALL, "class_midway")) {
                    markDone("class_midway");
                    SimpleTooltip tooltip2 = new SimpleTooltip.Builder(mContext)
                            .anchorView(holder.classEndTime)
                            .text("Yellow->Class is half-way through")
                            .gravity(Gravity.END)
                            .dismissOnOutsideTouch(true)
                            .dismissOnInsideTouch(true)
                            .animated(true)
                            .textColor(BuildsData.getColor(mContext, R.color.white))
                            .build();
                    tooltip2.show();
                }
                if (ClasssTypeof.equalsIgnoreCase(configs.TYPE_OF_CLASS[0])) {
                    holder.classstate.setText(configs.TUTORIAL_CLASS_STATE[2]);
                } else {
                    holder.classstate.setText(configs.LECTURE_CLASS_STATE[2]);
                }
            }
        }
        if (feedItem.getIsReminderSetyet().equalsIgnoreCase(configs.IS_REMINDER_SET[1])) {
            holder.classModuleName.setTextColor(BuildsData.getColor(mContext, R.color.almostDone));
            holder.alamset_state.setVisibility(View.VISIBLE);
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UNSEtReminder_showPopupMenu(holder.overflow, feedItem.getClassID());
                }
            });

        } else {

            holder.alamset_state.setVisibility(View.INVISIBLE);
            holder.classModuleName.setTextColor(BuildsData.getColor(mContext, R.color.black));
            holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(holder.overflow, feedItem.getClassID());
                }
            });
        }
        holder.classEndTime.setText(t2.substring(0, t1.length() - 3));
        holder.classModuleName.setText(feedItem.getClassModuleName());
        holder.classVenue.setText(feedItem.getClassVenue());

    }

    private void showPopupMenu(View view, int row_id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.recycler_context, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(row_id));
        popup.show();
    }

    private void UNSEtReminder_showPopupMenu(View view, int row_id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.unset_reminder_recycler_context, popup.getMenu());
        popup.setOnMenuItemClickListener(new UNSEtReminder_MyMenuItemClickListener(row_id));
        popup.show();
    }

    @Override
    public int getItemViewType(int position) {
        return (
                feedItemList.get(position).getTypeOfClass().equalsIgnoreCase(configs.TYPE_OF_CLASS[1]) ? TUTORIAL_CLASS : LECTURE_CLASS
        );

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void update(RealmResults<ClassesLecture> feedItemList) {
        this.feedItemList = feedItemList;
        notifyDataSetChanged();
    }

    //private method of your class
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

    private static String Time_Picked = "";
    static String[] Time_Select_start = {""};
    static String[] Time_Select_end = {""};
    private static boolean isTutorial = true;
    private static EditText tutorialTimePicker1;

    private void EditClass(final int rowID) {
        String[] Autovenues = mContext.getResources().getStringArray(R.array.ClassVenue);
        ArrayAdapter<String> AutovenuesAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_list_item_1, Autovenues);
        Realm realm = Realm.getDefaultInstance();
        ClassesLecture rslts = realm.where(ClassesLecture.class).equalTo("classID", rowID).findFirst();
        ORDER_CLASS = rslts.getClassOrder();
        final String there_was_className = rslts.getClassModuleName();
        final String there_wasClassVenu = rslts.getClassVenue();
        isTutorial= rslts.getTypeOfClass().equalsIgnoreCase("Tutorial");

        final String[] classTypeSelected = new String[1];
        Time_Picked = rslts.getClassStartTime();
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.edit_item, null);
        final String[] classarray = mContext.getResources().getStringArray(R.array.classtime_arrays);
        final Spinner spinnerClassTime = (Spinner) content.findViewById(R.id.spinnerClassTime);
        final Spinner spinnerClassDay = (Spinner) content.findViewById(R.id.spinnerClassDay);
        final Spinner spinnerClassType = (Spinner) content.findViewById(R.id.spinnerClassType);//spinnerClassType
        final EditText className_edit = (EditText) content.findViewById(R.id.className_edit);
        tutorialTimePicker1 = (EditText) content.findViewById(R.id.tutorialTimePicker1);
        tutorialTimePicker1.setText(BuildsData.RemoveLastChars(Time_Picked, 3));
        tutorialTimePicker1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tutorialTimePicker1.setShowSoftInputOnFocus(false);


                new TimePickerFragment().show(fragmentManager, "timePicker");
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
        className_edit.setText(there_was_className);
        final AutoCompleteTextView classVenue_edit = (AutoCompleteTextView) content.findViewById(R.id.classVenue_edit);
        classVenue_edit.setText(there_wasClassVenu);
        classVenue_edit.setAdapter(AutovenuesAdapter);
        classVenue_edit.setThreshold(1);
        ///
        ArrayList<String> temp1=new ArrayList<>();
        for (String v : classarray) {temp1.add(v); }
        final CustomSpinnerAdapter dataAdapter = new CustomSpinnerAdapter(mContext,  temp1);


        spinnerClassTime.setAdapter(dataAdapter);
        spinnerClassTime.setSelection(getIndex(spinnerClassTime, (rslts.getClassStartTime().substring(0, 5) + " - " + rslts.getClassEndTime().substring(0, 5))), true);
        Time_Select_start[0] = rslts.getClassStartTime().substring(0, 5);
        Time_Select_end[0] = rslts.getClassEndTime().substring(0, 5);
        if (isTutorial) {

            spinnerClassTime.setEnabled(false);
            tutorialTimePicker1.setEnabled(true);
            spinnerClassTime.setBackgroundResource(R.drawable.background_error);
            tutorialTimePicker1.setBackgroundResource(R.drawable.custom_spinner_background);

        }else{
            spinnerClassTime.setEnabled(true);
            tutorialTimePicker1.setEnabled(false);

            spinnerClassTime.setBackgroundResource(R.drawable.custom_spinner_background);
            tutorialTimePicker1.setBackgroundResource(R.drawable.background_error);

        }
        //
        final ArrayList<String> temp2=new ArrayList<>();
        for (String v : configs.TIMES_DAYS) {temp2.add(v); }
        if(!new mSettings(mContext).getTAB_SATURDAY().isEmpty() && !new mSettings(mContext).getTAB_SUNDAY().isEmpty()){
            temp2.add("Saturday");
            temp2.add("Sunday");
        }else if(!new mSettings(mContext).getTAB_SATURDAY().isEmpty() && new mSettings(mContext).getTAB_SUNDAY().isEmpty() ){
            temp2.add("Saturday");
        }if(new mSettings(mContext).getTAB_SATURDAY().isEmpty() && !new mSettings(mContext).getTAB_SUNDAY().isEmpty() ){
            temp2.add("Sunday");
        }
        final CustomSpinnerAdapter dataAdapter2 =  new CustomSpinnerAdapter(mContext,temp2  );

        spinnerClassDay.setAdapter(dataAdapter2);
        spinnerClassDay.setSelection(getIndex(spinnerClassDay, rslts.getClassDay()), true);
        DAY_SPINNER_SELECT = rslts.getClassDay();
        //
        ArrayList<String> temp3=new ArrayList<>();
        for (String v : configs.TYPE_OF_CLASS) {temp3.add(v); }
        final  CustomSpinnerAdapter dataAdapter3 = new CustomSpinnerAdapter(mContext,temp3  );

        spinnerClassType.setAdapter(dataAdapter3);
        spinnerClassType.setSelection(getIndex(spinnerClassType, rslts.getTypeOfClass()), true);
        classTypeSelected[0] = rslts.getTypeOfClass();

        //
        spinnerClassTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Time_Select_start[0] = configs.TIMES_PERIOD_START[position];
                Time_Select_end[0] = configs.TIMES_PERIOD_END[position];
                ORDER_CLASS = configs.CLASS_ORDER[position];


               /* if (position == 0) {
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


                }*/

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        //

        spinnerClassType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { //tutorial
                    classTypeSelected[0] = configs.TYPE_OF_CLASS[position];
                    isTutorial = true;
                    spinnerClassTime.setEnabled(false);
                    tutorialTimePicker1.setEnabled(true);
                    spinnerClassTime.setBackgroundResource(R.drawable.background_error);
                    tutorialTimePicker1.setBackgroundResource(R.drawable.custom_spinner_background);

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
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        //
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(content)
                .setTitle("Edit Class.")
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!className_edit.getText().toString().trim().isEmpty() && !classVenue_edit.getText().toString().trim().isEmpty()) {

                            CRUD.updateClassDB(
                                    DAY_SPINNER_SELECT,
                                    Time_Select_start[0] + ":00",
                                    Time_Select_end[0] + ":00",
                                    className_edit.getText().toString().trim(),
                                    classVenue_edit.getText().toString().trim(),
                                    "" + ORDER_CLASS,
                                    configs.IS_REMINDER_SET[0],
                                    classTypeSelected[0],
                                    rowID + ""
                            );
                            dialog.dismiss();
                            SnackbarManager.show(
                                    Snackbar.with(mContext)
                                            .text("Class Edited.")
                                            .actionLabel(/*"Undo"*/"") // action button label
                                            .actionColor(Color.RED)
                                            .duration(1000 * 4)
                                            .actionListener(new ActionClickListener() {
                                                @Override
                                                public void onActionClicked(Snackbar snackbar) {
                                                    Toast.makeText(mContext, "UNDoin....", Toast.LENGTH_LONG).show();
                                                }
                                            })

                            );
                        } else {
                            final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
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
                                    .withButton1Text("Save")
                                    .isCancelableOnTouchOutside(true)
                                    .setButton1Click(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            dialogBuilder.dismiss();
                                            rowIdTOEdit = rowID;
                                            EditClass(rowIdTOEdit);
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
        dialog.show();

    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int rowID;

        public MyMenuItemClickListener(int rowID) {
            this.rowID = rowID;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_reminder:
                    Toast.makeText(mContext, "Added Reminder", Toast.LENGTH_SHORT).show();
                    crud.UpdateClassReminders(rowID, "add");
                    Myalarm.CreateAlarm(rowID);
                    return true;
                case R.id.action_add_notes:
                    EditClass(rowID);
                    return true;
                case R.id.action_Delete:
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);
                    dialogBuilder.withTitle("Alert!!")
                            .withTitleColor("#FFFFFF")
                            .withDividerColor("#727272")
                            .withMessage("Are you sure you want to Delete Class!!")
                            .withMessageColor("#FFFFFFFF")
                            .withDialogColor("#FFE74C3C")
                            .withButton1Text("Yes")
                            .withButton2Text("Cancel")
                            .isCancelableOnTouchOutside(false)
                            .withEffect(new NifftyDialogs(mContext).stylepop_up())
                            .withDuration(700)
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    crud.DeleteClass(rowID);
                                    notifyItemRemoved(rowID);
                                    Myalarm.CancelAlarm(mContext, rowID);
                                    dialogBuilder.dismiss();
                                    SnackbarManager.show(
                                            Snackbar.with(mContext)
                                                    .text("Succefully Deleted.")
                                                    .actionLabel(/*"Undo"*/"") // action button label
                                                    .actionColor(Color.RED)
                                                    .duration(1000 * 4)
                                                    .actionListener(new ActionClickListener() {
                                                        @Override
                                                        public void onActionClicked(Snackbar snackbar) {
                                                            Toast.makeText(mContext, "UNDoin....", Toast.LENGTH_LONG).show();
                                                        }
                                                    })

                                    );

                                }
                            })
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialogBuilder.dismiss();

                                    SnackbarManager.show(
                                            Snackbar.with(mContext)
                                                    .text("Eheka.. Usadeleter,waka uya kuzo funda lol...")

                                    );

                                }
                            })
                            .show();
                    return true;
                default:
            }
            return false;
        }
    }

    class UNSEtReminder_MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int rowID;

        public UNSEtReminder_MyMenuItemClickListener(int rowID) {
            this.rowID = rowID;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_remove_reminder:

                    crud.UpdateClassReminders(rowID, "remove");
                    Myalarm.CancelAlarm(mContext, rowID);
                    Toast.makeText(mContext, "Removed Reminder", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_add_notes:
                    EditClass(rowID);
                    return true;
                case R.id.action_Delete:

                    // Toast.makeText(mContext, "You have Just Deleted a Class", Toast.LENGTH_LONG).show();
                    final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(mContext);

                    dialogBuilder.withTitle("Alert!!")
                            .withTitleColor("#FFFFFF")
                            .withDividerColor("#727272")
                            .withMessage("Are you sure you want to Delete Class!!")
                            .withMessageColor("#FFFFFFFF")
                            .withDialogColor("#FFE74C3C")
                            .withButton1Text("Yes")
                            .withButton2Text("Cancel")
                            .isCancelableOnTouchOutside(false)
                            .withEffect(new NifftyDialogs(mContext).stylepop_up())
                            .withDuration(700)
                            .setButton1Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    crud.DeleteClass(rowID);
                                    notifyItemRemoved(rowID);
                                    Myalarm.CancelAlarm(mContext, rowID);
                                    dialogBuilder.dismiss();
                                    SnackbarManager.show(
                                            Snackbar.with(mContext)
                                                    .text("Succefully Deleted.")
                                                    .actionLabel("Undo") // action button label
                                                    .actionColor(Color.RED)
                                                    .actionListener(new ActionClickListener() {
                                                        @Override
                                                        public void onActionClicked(Snackbar snackbar) {
                                                            Toast.makeText(mContext, "UNDoin....", Toast.LENGTH_LONG).show();
                                                        }
                                                    })

                                    );

                                }
                            })
                            .setButton2Click(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    dialogBuilder.dismiss();

                                    SnackbarManager.show(
                                            Snackbar.with(mContext)
                                                    .text("Eheka.. Usadeleter,waka uya kuzo funda lol...")

                                    );

                                }
                            })
                            .show();

                    return true;
                default:
            }
            return false;
        }
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
            now.set(Calendar.HOUR_OF_DAY, hourOfDay);
            now.set(Calendar.MINUTE, minute);
            Time_Picked = new SimpleDateFormat("HH:mm:ss").format(now.getTime());

            Time_Select_start[0] = Time_Picked;
            Time_Select_end[0] = BuildsData.incrementTime(Time_Picked);
            ORDER_CLASS = 1;/*remove this when ever there are real issues with ordering*/
            /*i have made this attribute reduntent as i am now using time for odering in my realm*/

            tutorialTimePicker1.setText(BuildsData.RemoveLastChars(Time_Picked, 3));

        }
    }
}
