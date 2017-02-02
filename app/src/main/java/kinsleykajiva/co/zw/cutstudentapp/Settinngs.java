package kinsleykajiva.co.zw.cutstudentapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import DBAccess.Preffs.mSettings;
import Messages.NifftyDialogs;
import Messages.SeeToast;
import Netwox.NetGetTimeTable;
import Netwox.NetwoxRequest;
import jonathanfinerty.once.Once;

import static BuildsConfigs.BuildsData.EXAM_TIME_KEY;
import static BuildsConfigs.BuildsData.JustClearedAllClassesDb;
import static BuildsConfigs.BuildsData.JustClearedAllExamsDb;

import static BuildsConfigs.BuildsData.JustRemovedTAB_SARTARDAY;
import static BuildsConfigs.BuildsData.JustRemovedTAB_SUNDAY;
import static BuildsConfigs.BuildsData.JustSetRenamedFragNamings;
import static BuildsConfigs.BuildsData.RandomInt;
import static BuildsConfigs.BuildsData.isExamTime;
import static DBAccess.RealmDB.CRUD.DeleteAllClasses;
import static DBAccess.RealmDB.CRUD.DeleteAllExams;
import static DBAccess.RealmDB.CRUD.getClassLEctureBackup;
import static DBAccess.RealmDB.CRUD.getExamBackup;
import static DBAccess.RealmDB.CRUD.isClassLectureBackupEmpty;
import static DBAccess.RealmDB.CRUD.isClassLectureDBEmpty;
import static DBAccess.RealmDB.CRUD.isExamBackupEmpty;
import static DBAccess.RealmDB.CRUD.isExamDBEmpty;
import static Netwox.NetwoxRequest.processExamJson;

public class Settinngs extends AppCompatActivity {
private LinearLayout opensorce,linear_bugs;
    private Context context = Settinngs.this;
    private final Handler DELAY_THREAD = new Handler();
    private ProgressDialog progressDialog;
    private TextView removeSaturday,removeSunday;
    private SwitchCompat clear_classtimetable,clear_examtimetable,revert_classtimetable,revert_examtimetable;
    private CheckBox full_tab_name,app_shortcuts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settinngs);

        initViews();
        settingViewsValues();
        initClickListeners();
    }

    private void removeSaturday() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withTitle("Removing Tab")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#727272")
                .withIcon(R.drawable.ic_info_white_24dp)
                .withMessage("You really want to remove Saturday?")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(new NifftyDialogs(context).stylepop_up())
                .withButton1Text("Yes")
                .withButton2Text("Cancel")
                .isCancelableOnTouchOutside(true)
                .setButton1Click(v -> {
                    showProgressLoading(true, "Removing ");
                    dialogBuilder.dismiss();
                    Runnable runnable = () -> {
                        new mSettings(context).setTAB_SATURDAY("");
                        JustRemovedTAB_SARTARDAY=true;
                        removeSaturday.setEnabled(false);
                        showProgressLoading(false, "");

                    };
                    DELAY_THREAD.postDelayed(runnable, (RandomInt(1, 2)) * 100); //for initial delay..

                })
                .setButton2Click(v -> dialogBuilder.dismiss())
                .show();
    }
    private void addShortcut() {

        Intent shortcutIntent = new Intent(getApplicationContext(),Settinngs.class);

        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "C.U.T Student");
        //addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                R.drawable.logo));

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        getApplicationContext().sendBroadcast(addIntent);
    }
    private void removeShortCut() {
        Intent shortcutInt = new Intent(getApplicationContext(),Settinngs.class);
        shortcutInt.setAction(Intent.ACTION_MAIN);
        Intent addInt = new Intent();
        addInt.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutInt);
        addInt.putExtra(Intent.EXTRA_SHORTCUT_NAME, "C.U.T Student");
        addInt.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");

        getApplicationContext().sendBroadcast(addInt);
    }
    private void removeSunday() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder
                .withTitle("Removing Tab")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#727272")
                .withIcon(R.drawable.ic_info_white_24dp)
                .withMessage("You really want to remove Sunday?")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(new NifftyDialogs(context).stylepop_up())
                .withButton1Text("Yes")
                .withButton2Text("Cancel")
                .isCancelableOnTouchOutside(true)
                .setButton1Click(v -> {
                    showProgressLoading(true, "Removing ");
                    dialogBuilder.dismiss();
                    Runnable runnable = () -> {
                        new mSettings(context).setTAB_SUNDAY("");
                        JustRemovedTAB_SUNDAY=true;
                        removeSunday.setEnabled(false);
                        showProgressLoading(false, "");

                    };
                    DELAY_THREAD.postDelayed(runnable, (RandomInt(1, 2)) * 100); //for initial delay..

                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        dialogBuilder.dismiss();
                    }
                })
                .show();
    }
    private void settingViewsValues() {
        clear_classtimetable.setChecked(isClassLectureDBEmpty());
        clear_examtimetable.setChecked(isExamDBEmpty());
        revert_classtimetable.setChecked(isClassLectureBackupEmpty());
        revert_examtimetable.setChecked(isExamBackupEmpty());
        removeSunday.setEnabled(!new mSettings(context).getTAB_SUNDAY().isEmpty());
        removeSaturday.setEnabled(!new mSettings(context).getTAB_SATURDAY().isEmpty());
        full_tab_name.setChecked(new mSettings(context).isTAB_ABBRIVIATED());
        if (isExamTime) {
            removeSaturday.setEnabled(false);
            removeSunday.setEnabled(false);
        }
        if(new mSettings(context).hasSHORT_CUT()){
            app_shortcuts.setChecked(true);
            app_shortcuts.setText("Remove App Shortcut");
        }


    }

    private void initClickListeners() {
        app_shortcuts.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b){
                addShortcut();
                new SeeToast().message_short(context,"Short-cut will be created");
                new mSettings(context).setAPP_SHORT_CUT(b);
                app_shortcuts.setText("Remove App Shortcut");
            }else{
                new SeeToast().message_short(context,"Short-cut will be removed");
                removeShortCut();
                new mSettings(context).setAPP_SHORT_CUT(b);
                app_shortcuts.setText("Create App Shortcut");
            }
        });
        linear_bugs.setOnClickListener(view -> startActivity(new Intent(context, About.class).putExtra("Settinngs-bugs", "known_bugs")));

        full_tab_name.setOnCheckedChangeListener((compoundButton, checked) -> {
            JustSetRenamedFragNamings=true;
            if(checked){

               // new SeeToast().message_short(context,"Changes will be made after App restarts .");
                new mSettings(context).setTAB_ABBRIVIATED(true);

            }else{

              // new SeeToast().message_short(context,"Changes will be made after App restarts .");
                new mSettings(context).setTAB_ABBRIVIATED(false);

            }

        });
        removeSaturday.setOnClickListener(view -> removeSaturday());
        removeSunday.setOnClickListener(view -> removeSunday());
        revert_classtimetable.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!b){
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle("Reverting ")
                        .withTitleColor("#FFFFFF")
                        .withDialogColor( Build.VERSION.SDK_INT>21? ContextCompat.getColor(context, R.color.colorPrimary): context.getResources().getColor(R.color.colorPrimary))
                        .withDividerColor("#727272")
                        .withIcon(R.drawable.ic_info_white_24dp)
                        .withMessage("Your want to reset to last set default ?")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#FFE74C3C")
                        .isCancelableOnTouchOutside(false)
                        .withDuration(700)
                        .withEffect(new NifftyDialogs(context).stylepop_up())
                        .withButton1Text("Yes")
                        .withButton2Text("Cancel")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(v -> {
                            showProgressLoading(true, "Reverting ");
                            dialogBuilder.dismiss();
                            Runnable runnable = () -> {

                                RevertClasses();
                                revert_classtimetable.setChecked(false);
                                showProgressLoading(false, "");
                                new NifftyDialogs(context).messageOk("Done");
                            };
                            DELAY_THREAD.postDelayed(runnable, (RandomInt(1, 3)) * 100); //for initial delay..

                        })
                        .setButton2Click(v -> {

                            revert_classtimetable.setChecked(isClassLectureBackupEmpty());
                            dialogBuilder.dismiss();
                        })
                        .show();
            }else{
                revert_classtimetable.setChecked(isClassLectureBackupEmpty());
            }
        });
        revert_examtimetable.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!b){
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle("Reverting ")
                        .withTitleColor("#FFFFFF")
                        .withDialogColor( Build.VERSION.SDK_INT>21? ContextCompat.getColor(context, R.color.colorPrimary): context.getResources().getColor(R.color.colorPrimary))
                        .withDividerColor("#727272")
                        .withIcon(R.drawable.ic_info_white_24dp)
                        .withMessage("Your want to reset to last set default ?")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#FFE74C3C")
                        .isCancelableOnTouchOutside(false)
                        .withDuration(700)
                        .withEffect(new NifftyDialogs(context).stylepop_up())
                        .withButton1Text("Yes")
                        .withButton2Text("Cancel")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(v -> {
                            showProgressLoading(true, "Reverting ");
                            dialogBuilder.dismiss();
                            Runnable runnable = () -> {

                                RevertExams();
                                revert_examtimetable.setChecked(true);
                                showProgressLoading(false, "");
                                new NifftyDialogs(context).messageOk("Done");
                            };
                            DELAY_THREAD.postDelayed(runnable, (RandomInt(1, 3)) * 100); //for initial delay..

                        })
                        .setButton2Click(v -> {

                            revert_examtimetable.setChecked(isExamBackupEmpty());
                            dialogBuilder.dismiss();
                        })
                        .show();
            }else{
                revert_examtimetable.setChecked(isExamBackupEmpty());
            }
        });

        clear_examtimetable.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle("Clear All Exams")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#727272")
                        .withIcon(R.drawable.ic_info_white_24dp)
                        .withMessage("Are you sure you want to delete all Exam")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#FFE74C3C")
                        .isCancelableOnTouchOutside(false)
                        .withDuration(700)
                        .withEffect(new NifftyDialogs(context).stylepop_up())
                        .withButton1Text("Yes")
                        .withButton2Text("Cancel")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(v -> {
                            showProgressLoading(true, "Deleting ");
                            dialogBuilder.dismiss();
                            Runnable runnable = () -> {
                                JustClearedAllExamsDb = true;
                                DeleteAllExams();
                                clear_examtimetable.setChecked(false);
                                showProgressLoading(false, "");
                                new NifftyDialogs(context).messageOk("All Exams Deleted.");
                            };
                            DELAY_THREAD.postDelayed(runnable, (RandomInt(1, 3)) * 100); //for initial delay..

                        })
                        .setButton2Click(v -> {

                            clear_examtimetable.setChecked(isExamDBEmpty());
                            dialogBuilder.dismiss();
                        })
                        .show();
            } else {
                clear_examtimetable.setChecked(isExamDBEmpty());
            }
        });

        clear_classtimetable.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!b) {
                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle("Clear All Classes")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#727272")
                        .withIcon(R.drawable.ic_info_white_24dp)
                        .withMessage("Are you sure you want to delete all Classes")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#FFE74C3C")
                        .isCancelableOnTouchOutside(false)
                        .withDuration(700)
                        .withEffect(new NifftyDialogs(context).stylepop_up())
                        .withButton1Text("Yes")
                        .withButton2Text("Cancel")
                        .isCancelableOnTouchOutside(true)
                        .setButton1Click(v -> {
                            showProgressLoading(true, "Deleting ");
                            dialogBuilder.dismiss();
                            Runnable runnable = () -> {
                                JustClearedAllClassesDb = true;
                                DeleteAllClasses();
                                clear_classtimetable.setChecked(false);
                                showProgressLoading(false, "");
                                new NifftyDialogs(context).messageOk("All Classes Deleted.");
                            };
                            DELAY_THREAD.postDelayed(runnable, (RandomInt(1, 3)) * 100); //for initial delay..

                        })
                        .setButton2Click(v -> {

                            clear_classtimetable.setChecked(isClassLectureDBEmpty());
                            dialogBuilder.dismiss();
                        })
                        .show();
            } else {
                clear_classtimetable.setChecked(isClassLectureDBEmpty());
            }
        });


        opensorce.setOnClickListener(view -> startActivity(new Intent(context, About.class).putExtra("Settinngs-licenses", "licenses")));

    }



    private void RevertClasses() {
        DeleteAllClasses();
    new NetwoxRequest().ProcessJson(getClassLEctureBackup());
    }
    private void RevertExams(){
        DeleteAllExams();
       processExamJson(getExamBackup());
    }

    private void initViews() {
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        opensorce=(LinearLayout) findViewById(R.id.opensorce);
        linear_bugs=(LinearLayout) findViewById(R.id.linear_bugs);
        clear_classtimetable=(SwitchCompat) findViewById(R.id.clear_classtimetable);
        clear_examtimetable=(SwitchCompat) findViewById(R.id.clear_examtimetable);
        revert_classtimetable=(SwitchCompat) findViewById(R.id.revert_classtimetable);
        revert_examtimetable=(SwitchCompat) findViewById(R.id.revert_examtimetable);
        progressDialog=new ProgressDialog(context);
        removeSunday= (TextView) findViewById(R.id.removeSunday);
        removeSaturday= (TextView) findViewById(R.id.removeSaturday);
        full_tab_name= (CheckBox) findViewById(R.id.full_tab_name);
        app_shortcuts=(CheckBox) findViewById(R.id.app_shortcuts);
        isExamTime = Once.beenDone(Once.THIS_APP_INSTALL, EXAM_TIME_KEY);
    }
    private void showProgressLoading(boolean state, String doingWhat) {

        if (state) {
            progressDialog.setMessage(doingWhat + "...Please wait.");
            progressDialog.setCancelable(false);
            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

        } else {
            progressDialog.dismiss();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
