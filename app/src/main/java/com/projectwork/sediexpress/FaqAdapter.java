package com.projectwork.sediexpress;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ChildViewHolder> {

    //Member variables
    private ArrayList<Model> mSportsData;
    private Context mContext;

    /**
     * Constructor that passes in the sports data and the context
     *
     * @param sportsData ArrayList containing the sports data
     * @param context    Context of the application
     */
    FaqAdapter(Context context, ArrayList<Model> sportsData) {
        this.mSportsData = sportsData;
        this.mContext = context;

//        //Prepare gray placeholder
//        mGradientDrawable = new GradientDrawable();
//        mGradientDrawable.setColor(Color.GRAY);
//
//        //Make the placeholder same size as the images
//        Drawable drawable = ContextCompat.getDrawable
//                (mContext,R.drawable.avatar);
//        if(drawable != null) {
//            mGradientDrawable.setSize(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
//        }
    }


    /**
     * Required method for creating the viewholder objects.
     *
     * @param parent   The ViewGroup into which the new View is added after it is
     *                 bound to an adapter position.
     * @param viewType The view type of the new View.
     * @return The newly create ChildViewHolder.
     */
    @Override
    public ChildViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ChildViewHolder(mContext, LayoutInflater.from(mContext).
                inflate(R.layout.faq_content, parent, false));
    }

    /**
     * Required method that binds the data to the viewholder.
     *
     * @param holder   The viewholder into which the data should be put.
     * @param position The adapter position.
     */
    @Override
    public void onBindViewHolder(ChildViewHolder holder, int position) {

        //Get the current sport
        Model currentChild = mSportsData.get(position);

        //Bind the data to the views
        holder.bindTo(currentChild);

    }


    /**
     * Required method for determining the size of the data set.
     *
     * @return Size of the data set.
     */
    @Override
    public int getItemCount() {
        return mSportsData.size();
    }


    /**
     * ChildViewHolder class that represents each row of data in the RecyclerView
     */
    static class ChildViewHolder extends RecyclerView.ViewHolder {

        private TextView name, title;
        private Context mContext;
        private Model mCurrentChild;


        /**
         * Constructor for the ChildViewHolder, used in onCreateViewHolder().
         *
         * @param itemView The rootview of the list_item.xml layout file
         */
        ChildViewHolder(final Context context, View itemView) {
            super(itemView);

            //Initialize the views

            title = itemView.findViewById(R.id.info);
            name = itemView.findViewById(R.id.name);

            mContext = context;


//            chat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                }
//            });
        }

        void bindTo(Model currentChild) {
            //Populate the textviews with data
            name.setText(currentChild.getName());
            title.setText((currentChild.getTitle()));
            //Get the current sport
            mCurrentChild = currentChild;

        }
    }
}
