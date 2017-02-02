package Adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flipkart.circularImageView.CircularDrawable;
import com.flipkart.circularImageView.OverlayArcDrawer;
import com.flipkart.circularImageView.TextDrawer;
import com.flipkart.circularImageView.notification.CircularNotificationDrawer;

import java.util.Random;

import DBAccess.RealmDB.NotificationsDB;
import io.realm.RealmResults;
import kinsleykajiva.co.zw.cutstudentapp.R;

/**
 * Created by Kinsley Kajiva on 12/30/2016.
 */

public class NotificationRecycler   extends RecyclerView.Adapter<NotificationRecycler.CustomViewHolder>{
    private RealmResults<NotificationsDB> feedItemList;
    private Context mContext;

    public NotificationRecycler(RealmResults<NotificationsDB> feedItemList, Context mContext) {
        this.feedItemList = feedItemList;
        this.mContext = mContext;
    }
    public class CustomViewHolder extends RecyclerView.ViewHolder {
        protected TextView rowTitle,rowDate;

        ImageView drawableLetter;
        public CustomViewHolder(View view) {
            super(view);

            this.rowTitle = (TextView) view.findViewById(R.id.rowTitle);
            this.rowDate = (TextView) view.findViewById(R.id.rowDate);
            this.drawableLetter = (ImageView) view.findViewById(R.id.drawableLetter);
        }
    }//endof class

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new CustomViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_row, null));
    }
    @Override
    public void onBindViewHolder(final CustomViewHolder holder, int position) {
        final NotificationsDB feedItem = feedItemList.get(position);

        holder.rowTitle.setText(feedItem.getTitle());
        holder.rowDate.setText(feedItem.getDate_posted());
        holder.drawableLetter.setImageDrawable(getDrawable(feedItem.getTitle())  );
    }
    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public void update(RealmResults<NotificationsDB> feedItemList){
        this.feedItemList =feedItemList;
        notifyDataSetChanged();
    }
    private Drawable getDrawable(String key) {
        Random rnd = new Random();
        int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        CircularDrawable circularDrawable = new CircularDrawable();
        circularDrawable.setBitmapOrTextOrIcon(new TextDrawer().setText( key).setBackgroundColor(randomColor));
        int badgeCount = (int) (Math.random() * 10f);
        circularDrawable.setNotificationDrawer(new CircularNotificationDrawer().setNotificationText(String.valueOf(badgeCount)).setNotificationAngle(135).setNotificationColor(Color.WHITE, Color.RED));
        circularDrawable.setBorder(Color.WHITE, 3);
        return circularDrawable;

    }
}
