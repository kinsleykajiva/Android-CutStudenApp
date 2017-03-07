package Adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import BuildsConfigs.BuildsData;
import DBAccess.RealmDB.CRUD;

import DBAccess.RealmDB.ExaminationDB;
import Messages.NifftyDialogs;
import Messages.SeeToast;
import Pojo.Date_item;
import Pojo.General_item;
import Pojo.ListItem;
import Pojo.Week_item;
import fragments.fragmentExam;
import io.realm.Realm;
import io.realm.RealmResults;
import kinsleykajiva.co.zw.cutstudentapp.R;

import static DBAccess.RealmDB.CRUD.UpdateExam;
import static DBAccess.RealmDB.CRUD.initWriteExamination;
import static DBAccess.RealmDB.CRUD.isExamAlreadySaved;

/**
 * Created by Kinsley Kajiva on 12/31/2016.
 */

public class ExamRecycler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<ListItem> consolidatedList = new ArrayList<>();
    private static EditText timpicker,datePicker;
    private static String DAY_SELECT = "";
    private static String Time_Picked = "",examDate="";
    static String[] Time_Select_start = {""};
    static String[] Time_Select_end = {""};
private static String week="";
    private Context mContext;
    private BuildsData configs = new BuildsData();
    private CRUD crud = new CRUD();
    private int rowIdTOEdit;
    private FragmentManager fragmentManager;
    private LayoutInflater inflater;

    public ExamRecycler(List<ListItem> consolidatedList, Context mContext, FragmentManager fragmentManager) {

        this.consolidatedList = consolidatedList;

        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
       // update(results);

    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        protected TextView txtWeek;

        public DateViewHolder(View v) {
            super(v);
            this.txtWeek = (TextView) v.findViewById(R.id.days_week);

        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView classStartTime, classEndTime, classVenue, classModuleName, classstate, alamset_state;
        public ImageView overflow;

        public CustomViewHolder(View view) {
            super(view);
            this.alamset_state = (TextView) view.findViewById(R.id.alamset_state);
            this.classStartTime = (TextView) view.findViewById(R.id.sample_item_index_text_view11);
            this.classEndTime = (TextView) view.findViewById(R.id.sample_item_index_text_view1);
            this.classModuleName = (TextView) view.findViewById(R.id.sample_item_name_text_view); //
            this.classstate = (TextView) view.findViewById(R.id.classstate);
            this.classVenue = (TextView) view.findViewById(R.id.textView);
            this.overflow = (ImageView) view.findViewById(R.id.imageView2);
        }

    }//endof class

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ListItem.TYPE_GENERAL) {
            return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.lecture_row, null));
        } else {
            return new DateViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exam_date, parent, false));
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case ListItem.TYPE_GENERAL:
                final General_item generalItem = (General_item) consolidatedList.get(position);
                final CustomViewHolder customViewHolderViewHolder = (CustomViewHolder) holder;

                customViewHolderViewHolder.classStartTime.setText(generalItem.getPojoArray().getStartTime());
                customViewHolderViewHolder.classEndTime.setText(generalItem.getPojoArray().getEndTime());
                customViewHolderViewHolder.classModuleName.setText(generalItem.getPojoArray().getName());
                customViewHolderViewHolder.classVenue.setText(generalItem.getPojoArray().getVenue());
                customViewHolderViewHolder.classstate.setText(generalItem.getPojoArray().getExamDay());
                customViewHolderViewHolder.alamset_state.setVisibility(View.VISIBLE);

                customViewHolderViewHolder.alamset_state.setText(generalItem.getPojoArray().getdDate());
                //customViewHolderViewHolder.overflow.setVisibility(View.GONE);
                customViewHolderViewHolder.overflow.setImageResource(R.drawable.ic_class_grey_900_24dp);
                /*customViewHolderViewHolder.overflow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        week=generalItem.getPojoArray().getWeek();
                        showPopupMenu(customViewHolderViewHolder.overflow, generalItem.getPojoArray().getID());
                    }
                });*/

                break;
            case ListItem.TYPE_DATE:
                Week_item dateItem = (Week_item) consolidatedList.get(position);
                DateViewHolder weekViewHolder = (DateViewHolder) holder;

                weekViewHolder.txtWeek.setText(ReNumName(Integer.parseInt(dateItem.getWeek())));


                break;
        }

    }
    private void showPopupMenu(View view, int row_id) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.exam_menu, popup.getMenu());
        popup.setOnMenuItemClickListener( new EditContext(row_id ));
        popup.show();
    }
    public void removeItem(int position) {
        consolidatedList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, consolidatedList.size());
    }

    @Override
    public int getItemViewType(int position) {
        return consolidatedList.get(position).getType();
    }

    private static String ReNumName(int number) {
        String ret = "" + number;
        if (number == 1) {
            ret = "First ";
        } else if (number == 2) {
            ret = "Second ";
        } else if (number == 3) {
            ret = "Third ";
        } else if (number == 4) {
            ret = "Fourth";
        } else if (number == 5) {
            ret = "Fifth";
        }

        return ret + " week";

    }

    private void editExam() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.edit_item_exam, null);
        Realm realm = Realm.getDefaultInstance();
        ExaminationDB rw = realm.where(ExaminationDB.class).equalTo("ID", rowIdTOEdit)
                .findFirst();
        timpicker = (EditText) content.findViewById(R.id.tutorialTimePicker1);
        timpicker.setText(rw.getStartTime());
        datePicker = (EditText) content.findViewById(R.id.tutorialDatePicker1);
        datePicker.setText(rw.getdDate());
        final EditText className_edit = (EditText) content.findViewById(R.id.className_edit);
        className_edit.setText(rw.getName());
        final EditText classVenue_edit = (EditText) content.findViewById(R.id.classVenue_edit);
        classVenue_edit.setText(rw.getVenue());
        realm.close();

        timpicker.setOnClickListener(view -> new TimePickerFragment().show(fragmentManager, "timePicker"));
        datePicker.setOnClickListener(view -> new DatePickerFragment().show(fragmentManager, "datePicker"));
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

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(content)
                .setTitle("Edit Exam.")
                .setCancelable(false)
                .setPositiveButton("Edit", (dialog, which) -> {
                    if (!datePicker.getText().toString().trim().isEmpty() && !timpicker.getText().toString().trim().isEmpty() && !className_edit.getText().toString().trim().isEmpty() && !classVenue_edit.getText().toString().trim().isEmpty()) {
                        UpdateExam(
                                rowIdTOEdit,

                                Time_Select_start[0],
                                Time_Select_end[0],
                                classVenue_edit.getText().toString().trim(),
                                className_edit.getText().toString().trim(),
                                examDate,
                                week,
                                DAY_SELECT
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
                                .withButton1Text("OK")
                                .isCancelableOnTouchOutside(true)
                                .setButton1Click(v -> {

                                    dialogBuilder.dismiss();
                                    editExam();
                                })

                                .show();

                    }
                })
                .setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomAnimations_slide;
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return consolidatedList != null ? consolidatedList.size() : 0;
    }

    public void update(List<ListItem> consolidatedList) {

        this.consolidatedList = consolidatedList;
        notifyDataSetChanged();
    }
    class EditContext  implements PopupMenu.OnMenuItemClickListener{
   // private int rowID;
    public EditContext(int rowID) {
        rowIdTOEdit = rowID;
    }
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                editExam();
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
