package kinsleykajiva.co.zw.cutstudentapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import Adapters.NotificationRecycler;
import BuildsConfigs.BuildsData;
import DBAccess.RealmDB.NotificationsDB;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import widgets.LinearItermDecorator;
import widgets.MyRecyclerItemClickListener;

public class MessageLists extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private NotificationRecycler adapter;
    private BuildsData configs = new BuildsData();
    Realm realm;
    RealmResults<NotificationsDB> results;
    private Context context=MessageLists.this;
    private RealmChangeListener realmChangeListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            adapter.update(results);
        }

    };//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_middle_read_page);
        realm=Realm.getDefaultInstance();
        results = realm.where(NotificationsDB.class).findAllSorted("notificationID", Sort.ASCENDING);
if(!results.isEmpty()){
    SetUpRecyclerview();
}

    }
private void SetUpRecyclerview(){
    mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_noticationlists);
    mRecyclerView.setHasFixedSize(true);

    adapter = new NotificationRecycler(results, context);
    mRecyclerView.setAdapter(adapter);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
    mRecyclerView.addItemDecoration(new LinearItermDecorator(context, LinearLayoutManager.VERTICAL));
    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    mRecyclerView.addOnItemTouchListener(new MyRecyclerItemClickListener(context, mRecyclerView, new MyRecyclerItemClickListener.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            final NotificationsDB feedItem =   results.get(position);

            startActivity(
                    (
                            new Intent(context,MReadPage.class)
                    )
                            .putExtra(
                                    "rowId",feedItem.getNotificationID()
                            )
            );
        }

        @Override
        public void onItemLongClick(View view, int position) {

        }
    }));

}
    @Override
    protected void onDestroy() {
        super.onDestroy();

        realm.close();
    }
}
