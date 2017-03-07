package Adapters.StudyTipsRecyclerAdapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import android.support.v4.util.ArrayMap;
import kinsleykajiva.co.zw.cutstudentapp.R;

/**
 * Created by Kinsley Kajiva on 3/4/2017.
 */

public class TipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int PARENT = 0;
    public static final int CHILD = 1;
    private Context mContext;
    private ArrayMap<String, ArrayList<DataModel>> backup = new ArrayMap<>();
    private ArrayList<DataModel> mTipModel;


    public TipsAdapter(Context context, ArrayList<DataModel> models) {
        mTipModel = models;
        mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == PARENT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_header, parent, false);
            return new ParentViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_child, parent, false);
            return new ChildViewHolder(view);
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ParentViewHolder) {
            ((ParentViewHolder) holder).mTxtHeader.setText(mTipModel.get(position).getItemName());
            if (position != mTipModel.size() - 1 && mTipModel.get(position + 1).getType() == 0) {
                ((ParentViewHolder) holder).mImgExpandCollapse.setVisibility(View.GONE); //remove expand/collapse if item has no child

            }
            if (position == mTipModel.size() - 1) {   //incase last item has no children
                ((ParentViewHolder) holder).mImgExpandCollapse.setVisibility(View.GONE);
                ((ParentViewHolder) holder).mViewShadow.setVisibility(View.VISIBLE);

            }

        } else {
            ((ChildViewHolder) holder).mTxtChild.setText(mTipModel.get(position).getItemName());
        }
    }

    @Override
    public int getItemCount() {
        return mTipModel.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mTipModel.get(position).getType();
    }




    private class ParentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTxtHeader;
        private View mViewShadow;
        private ImageView mImgExpandCollapse;

         ParentViewHolder(View itemView) {
            super(itemView);
            mTxtHeader = (TextView) itemView.findViewById(R.id.header_title);
            mImgExpandCollapse = (ImageView) itemView.findViewById(R.id.header_expand_click);
            mViewShadow = itemView.findViewById(R.id.shadow);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if (mTipModel.get(getLayoutPosition()).isCollapsed()) {
                mTipModel.get(getLayoutPosition()).setIsCollapsed(false);
                expand(mTipModel.get(getLayoutPosition()).getItemName(), getLayoutPosition());
                mImgExpandCollapse.animate().rotation(0f).setDuration(300).start();
                if (getLayoutPosition() != mTipModel.size() - 1) {
                    mViewShadow.setVisibility(View.GONE);
                }
            } else {
                mTipModel.get(getLayoutPosition()).setIsCollapsed(true);
                mImgExpandCollapse.animate().rotation(180f).setDuration(300).start();
                new Handler().postDelayed(() -> mViewShadow.setVisibility(View.VISIBLE), 500);
                collapse();
            }

        }

        private void collapse() {
            if (getLayoutPosition() != mTipModel.size() - 1) {
                int pos = getLayoutPosition() + 1;
                if(pos==mTipModel.size()){
                    pos--;
                }

                int removeLastIndex = 0;
                int removeStartIndex = pos;

                ArrayList<DataModel> temp = new ArrayList<>();
                while (mTipModel.get(pos).getType() != 0) {
                    temp.add(mTipModel.get(pos));
                    removeLastIndex++;
                    mTipModel.remove(pos);
                }
                backup.put(mTipModel.get(getLayoutPosition()).getItemName(), temp);
                notifyItemRangeRemoved(removeStartIndex, removeLastIndex);
            }
        }

        private void expand(String item, int pos) {
            int addStartIndex = pos + 1;
            int addLastIndex = 0;
            if (backup.containsKey(item)) {
                ArrayList<DataModel> model = backup.get(item);
                for (DataModel temp : model) {
                    mTipModel.add(++pos, temp);
                    addLastIndex = addLastIndex + 1;
                }
            }
            notifyItemRangeInserted(addStartIndex, addLastIndex);
        }
    }

    private class ChildViewHolder extends RecyclerView.ViewHolder  {
        private TextView mTxtChild;

         ChildViewHolder(View itemView) {
            super(itemView);
            mTxtChild = (TextView) itemView.findViewById(R.id.child_title);

        }


    }
}
