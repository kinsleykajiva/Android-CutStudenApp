package Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import DBAccess.RealmDB.ClassesLecture;
import DBAccess.RealmDB.NotesDB;
import io.realm.RealmResults;
import kinsleykajiva.co.zw.cutstudentapp.R;

/**
 * Created by Kinsley Kajiva on 1/9/2017.
 */

public class NotesRecycler extends RecyclerView.Adapter<NotesRecycler.CustomViewHolder> {

    private Context mContext;
    private LayoutInflater inflater;
    private RealmResults<NotesDB> feedItemList;

    public NotesRecycler(RealmResults<NotesDB> feedItemList, Context mContext) {
        this.feedItemList = feedItemList;
        this.mContext = mContext;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected TextView title, date;


        public CustomViewHolder(View view) {
            super(view);

            this.title = (TextView) view.findViewById(R.id.title);
            this.date = (TextView) view.findViewById(R.id.date);

        }

    }
    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


          return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_row, null));


    }
    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final NotesDB feedItem = feedItemList.get(position);
        holder.title.setText(feedItem.getDescription());
        holder.date.setText(feedItem.getDate());
    }
    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }
    public void update(RealmResults<NotesDB> feedItemList) {
        this.feedItemList = feedItemList;
        notifyDataSetChanged();
    }
}
