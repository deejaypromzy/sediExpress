package com.projectwork.sediexpress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class ReqAdapter extends RecyclerView.Adapter<ReqAdapter.ChildViewHolder> {
    private static final int VIEW_TYPE_TOP = 0;
    private static final int VIEW_TYPE_MIDDLE = 1;
    private static final int VIEW_TYPE_BOTTOM = 2;

    //Member variables
    private ArrayList<RequestInfo> mSportsData;
    private Context mContext;

    ReqAdapter(Context context, ArrayList<RequestInfo> sportsData) {
        this.mSportsData = sportsData;
        this.mContext = context;

    }


    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChildViewHolder(mContext, LayoutInflater.from(mContext).
                inflate(R.layout.content_details, parent, false));
    }


    @Override
    public int getItemCount() {
        return mSportsData.size();
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_TOP;
        } else if (position == mSportsData.size() - 1) {
            return VIEW_TYPE_BOTTOM;
        } else {
            return VIEW_TYPE_MIDDLE;
        }


    }

    @Override
    public void onBindViewHolder(ChildViewHolder holder, int position) {

        //Get the current sport
        RequestInfo currentChild = mSportsData.get(position);

        //Bind the data to the views
        holder.bindTo(currentChild);

        // Populate views...
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TOP:
                // The top of the line has to be rounded
                holder.mItemLine.setBackgroundResource(R.drawable.line_bg_top);
                holder.imageView.setBackgroundResource(R.drawable.btn_login_bg2);
                holder.imageView.setImageResource(R.drawable.ic_location2);
                break;
            case VIEW_TYPE_MIDDLE:
                // Only the color could be enough
                // but a drawable can be used to make the cap rounded also here
                holder.mItemLine.setBackgroundResource(R.drawable.line_bg_middle);
                holder.imageView.setBackgroundResource(R.drawable.btn_login_bg);
                break;
            case VIEW_TYPE_BOTTOM:
                holder.mItemLine.setBackgroundResource(R.drawable.line_bg_bottom);
                holder.imageView.setBackgroundResource(R.drawable.btn_login_bg3);
                holder.imageView.setImageResource(R.drawable.ic_location2);

                break;
        }
    }

    static class ChildViewHolder extends RecyclerView.ViewHolder {
        TextView mItemTitle;
        TextView mItemSubtitle, title;
        FrameLayout mItemLine;
        ImageView imageView;
        private Context mContext;
        private RequestInfo mCurrentChild;


        ChildViewHolder(final Context context, View itemView) {
            super(itemView);


            //Initialize the views
            mItemTitle = (TextView) itemView.findViewById(R.id.item_title);
            mItemSubtitle = (TextView) itemView.findViewById(R.id.item_subtitle);
            title = (TextView) itemView.findViewById(R.id.title);
            mItemLine = itemView.findViewById(R.id.item_line);
            imageView = itemView.findViewById(R.id.imageView3);


            mContext = context;


        }

        void bindTo(RequestInfo currentChild) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy HH:mm", Locale.US);

            //Populate the textviews with data
            mItemTitle.setText(currentChild.getDestination().toUpperCase());
            title.setText((currentChild.getDescription()));
            mItemSubtitle.setText((dateFormat.format(new Date(currentChild.getDate()))));

            //Get the current sport
            mCurrentChild = currentChild;

        }
    }

}
