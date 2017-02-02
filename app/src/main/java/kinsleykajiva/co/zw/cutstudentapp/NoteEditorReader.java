package kinsleykajiva.co.zw.cutstudentapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import Messages.NifftyDialogs;
import Messages.SeeToast;
import widgets.EditNoteEditTextView;

import static BuildsConfigs.BuildsData.getResourceDrawable;
import static BuildsConfigs.BuildsData.isExamTime;
import static DBAccess.RealmDB.CRUD.CreateNote;
import static DBAccess.RealmDB.CRUD.DeleteNote;
import static DBAccess.RealmDB.CRUD.UpdateNote;
import static DBAccess.RealmDB.CRUD.getNote;

public class NoteEditorReader extends AppCompatActivity {

    private EditNoteEditTextView note;
    private int RowID=0; // there is never an instance where id will be zero
    private boolean isEdit=false;
    private int Chacount=0;
    private final String RESTORE_getText="note_txt",RESTORE_getID="note_id",RESTORE_getisEditable="note_boolean";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor_reader);

        getSupportActionBar().setTitle("Note");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
         note=  (EditNoteEditTextView) findViewById(R.id.note);
        if(getIntent().hasExtra("RowNote")){
            isEdit=true;
            /*user wants to edit a note */
            RowID=(getIntent().getExtras().getInt("RowNote"));
            String [] resultsDB=getNote(RowID);
            note.setText(resultsDB[1]);

            Chacount=note.getText().toString().length();
        }else{
        //  user wants to create a new note
           // note.setText(   getNote(3)[1]    );
        }
        if(savedInstanceState!=null){
            note.setText(savedInstanceState.getString(RESTORE_getText));
            RowID=savedInstanceState.getInt(RESTORE_getID);
            isEdit=savedInstanceState.getBoolean(RESTORE_getisEditable);

        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        savedInstanceState.putString(RESTORE_getText,!note.getText().toString().trim().isEmpty()?note.getText().toString():"");
        savedInstanceState.putInt(RESTORE_getID,RowID);
        savedInstanceState.putBoolean(RESTORE_getisEditable,isEdit);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            onBackPressed();
            return true;
        }
        if(id==R.id.action_Delete){
            if(isEdit) {

                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
                dialogBuilder
                        .withTitle("Content Manager")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#727272")
                        .withIcon(R.drawable.ic_info_white_24dp)
                        .withMessage("Are you sure you want to delete !")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#FFE74C3C")
                        .isCancelableOnTouchOutside(false)
                        .withDuration(700)
                        .withEffect(new NifftyDialogs(this).stylepop_up())
                        .withButton1Text("Yes")
                        .withButton2Text("Cancel")
                        .isCancelableOnTouchOutside(true)

                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Chacount=note.getText().toString().length();
                                DeleteNote(RowID);

                                dialogBuilder.dismiss();

                                onBackPressed();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
            }else{
                Chacount=note.getText().toString().length();
                onBackPressed();
            }

        }
        if(id==R.id.action_save){
            if(note.getText().toString().trim().isEmpty()){
                new NifftyDialogs(this).messageOkError("Content Manager","Cant save an empty note.");
            }else{

                Chacount=note.getText().toString().length();

                if(isEdit && RowID!=0){
                    UpdateNote(RowID,note.getText().toString());
                    new SeeToast().message_short(this,"Saved");
                }else {

                    CreateNote(note.getText().toString());
                    onBackPressed();
                }
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void ConfirmSaveBeforeExit(){
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
        dialogBuilder
                .withTitle("Content Manager")
                .withTitleColor("#FFFFFF")
                .withDividerColor("#727272")
                .withIcon(R.drawable.ic_info_white_24dp)
                .withMessage("Do you want to Save ?")
                .withMessageColor("#FFFFFFFF")
                .withDialogColor("#FFE74C3C")
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(new NifftyDialogs(this).stylepop_up())
                .withButton1Text("Yes")
                .withButton2Text("Cancel")
                .isCancelableOnTouchOutside(true)

                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Chacount=note.getText().toString().length();
                        UpdateNote(RowID,note.getText().toString());
                        dialogBuilder.dismiss();
                        onBackPressed();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogBuilder.dismiss();
                        Chacount=note.getText().toString().length();
                        onBackPressed();
                    }
                })
                .show();
    }

    @Override
    public void onBackPressed() {

        if(Chacount!=note.getText().toString().length() ){
            // ask save

            if(Chacount==0){

                final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);
                dialogBuilder
                        .withTitle("Content Manager")
                        .withTitleColor("#FFFFFF")
                        .withDividerColor("#727272")
                        .withIcon(R.drawable.ic_info_white_24dp)
                        .withMessage("Do you want to Save ?")
                        .withMessageColor("#FFFFFFFF")
                        .withDialogColor("#FFE74C3C")
                        .isCancelableOnTouchOutside(false)
                        .withDuration(700)
                        .withEffect(new NifftyDialogs(this).stylepop_up())
                        .withButton1Text("Yes")
                        .withButton2Text("Cancel")
                        .isCancelableOnTouchOutside(true)

                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Chacount=note.getText().toString().length();
                                CreateNote(note.getText().toString());

                                dialogBuilder.dismiss();
                                onBackPressed();
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                                Chacount=note.getText().toString().length();
                                onBackPressed();
                            }
                        })
                        .show();
            }else {
                ConfirmSaveBeforeExit();
            }
        }else{
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_note, menu);

        return true;
    }
}
