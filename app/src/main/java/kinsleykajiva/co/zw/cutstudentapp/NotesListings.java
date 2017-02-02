package kinsleykajiva.co.zw.cutstudentapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import Adapters.ClassLectureRecycler;
import Adapters.NotesRecycler;
import DBAccess.RealmDB.NotesDB;
import Messages.NifftyDialogs;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import widgets.LinearItermDecorator;
import widgets.MyRecyclerItemClickListener;

import static DBAccess.RealmDB.CRUD.CreateNote;

public class NotesListings extends AppCompatActivity {
    private Realm myRealm;
    private RealmResults<NotesDB> results;
    private NotesRecycler adapter;
    private RecyclerView recycler;
    private TextView recycler_state;
    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            recycler_state.setVisibility(!results.isEmpty()?View.GONE:View.VISIBLE);
            adapter.update(results);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_listings);
        getSupportActionBar().setTitle("Notes");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recycler= (RecyclerView) findViewById(R.id.recycler);
        recycler_state= (TextView) findViewById(R.id.recycler_state);

        myRealm=Realm.getDefaultInstance();

        results= myRealm.where(NotesDB.class).findAllSorted("ID", Sort.DESCENDING);
        results.addChangeListener(realmChangeListener);

        recycler_state.setVisibility(!results.isEmpty()?View.GONE:View.VISIBLE);

        adapter = new NotesRecycler(results, this);
        recycler.setAdapter(adapter);
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.addItemDecoration(new LinearItermDecorator(this, LinearLayoutManager.VERTICAL));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.addOnItemTouchListener(new MyRecyclerItemClickListener(NotesListings.this, recycler, new MyRecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                NotesDB feedItem = results.get(position);
                startActivity(new Intent(NotesListings.this,NoteEditorReader.class).putExtra("RowNote",feedItem.getID()));

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


    }


    @Override
    protected void onDestroy() {
        results.removeChangeListener(realmChangeListener);
        myRealm.close();
        super.onDestroy();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (id == R.id.action_addNote) {
           startActivity(new Intent(this,NoteEditorReader.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_note, menu);

        return true;
    }

}
