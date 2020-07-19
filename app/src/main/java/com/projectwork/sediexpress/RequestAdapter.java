package com.projectwork.sediexpress;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ChildViewHolder> {

    //Member variables
    private ArrayList<RequestModel> mSportsData;
    private Context mContext;

    /**
     * Constructor that passes in the sports data and the context
     *
     * @param sportsData ArrayList containing the sports data
     * @param context    Context of the application
     */
    RequestAdapter(Context context, ArrayList<RequestModel> sportsData) {
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
                inflate(R.layout.request_content, parent, false));
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
        RequestModel currentChild = mSportsData.get(position);

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
    static class ChildViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private PrefManager prefManager;
        private FirebaseAuth auth;
        private FirebaseUser fireuser;
        private FirebaseDatabase mfirebaseDatabase;
        private FirebaseAuth mAuth;
        private DatabaseReference mref;
        private TextView name, title, date, view, payment, approval, status, end, start;
        private Context mContext;
        private RequestModel mCurrentChild;

        ChildViewHolder(final Context context, View itemView) {
            super(itemView);
            prefManager = new PrefManager(context);

            //Initialize the views


            auth = FirebaseAuth.getInstance();
            fireuser = auth.getCurrentUser();
            mfirebaseDatabase = FirebaseDatabase.getInstance();

            mAuth = FirebaseAuth.getInstance();


            mref = FirebaseDatabase.getInstance().getReference();
            mref.keepSynced(true);


            start = itemView.findViewById(R.id.start);
            end = itemView.findViewById(R.id.end);
            status = itemView.findViewById(R.id.status);
            title = itemView.findViewById(R.id.info);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            view = itemView.findViewById(R.id.view);
            payment = itemView.findViewById(R.id.payment);
            approval = itemView.findViewById(R.id.approval);
            end.setOnClickListener(this);
            status.setOnClickListener(this);
            view.setOnClickListener(this);
            payment.setOnClickListener(this);
            approval.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, mCurrentChild.get_id(), Toast.LENGTH_SHORT).show();
                    Intent detailIntent = RequestModel.details(mContext,
                            mCurrentChild.get_id(),
                            mCurrentChild.getId(),
                            mCurrentChild.getPickup(),
                            mCurrentChild.getDrop(),
                            mCurrentChild.getDistance(),
                            mCurrentChild.getAmount(),
                            mCurrentChild.getRecipient(),
                            mCurrentChild.getRecipient_phone(),
                            mCurrentChild.getDesc(),
                            mCurrentChild.getDate(),
                            mCurrentChild.getStatus(),
                            mCurrentChild.getAgent()
                    );
                    //Start the detail activity
                    mContext.startActivity(detailIntent);
                }
            });
            mContext = context;


//            chat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                }
//            });
        }

        void bindTo(RequestModel currentChild) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (prefManager.getRole().equals("agent")) {
                approval.setText("Accept Request");
                payment.setText("Payment Pending");
                payment.setBackgroundColor(mContext.getResources().getColor(R.color.red));
            } else {
                approval.setText("Pending Request");
                payment.setText("Make Payment");

            }
            //Populate the textviews with data
            name.setText("Tracking ID :  " + currentChild.get_id());
            title.setText((currentChild.getDesc()));
            date.setText((dateFormat.format(new Date(currentChild.getDate()))));
            switch (currentChild.getStatus()) {
                case Utils.PENDING:
                    payment.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                    start.setVisibility(View.GONE);
                    end.setVisibility(View.GONE);
                    status.setVisibility(View.GONE);
                    approval.setVisibility(View.VISIBLE);
                    break;
                case Utils.APPROVED:
                    payment.setVisibility(View.VISIBLE);
                    start.setVisibility(View.GONE);
                    end.setVisibility(View.GONE);
                    status.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                    approval.setVisibility(View.GONE);
                    break;
                case Utils.PAID:
                    payment.setVisibility(View.GONE);

                    if (prefManager.getRole().equals("agent")) {

                        end.setVisibility(View.VISIBLE);
                        status.setVisibility(View.VISIBLE);
                        view.setVisibility(View.GONE);

                    } else {
                        view.setVisibility(View.VISIBLE);
                        end.setVisibility(View.GONE);
                        status.setVisibility(View.GONE);
                    }
                    start.setVisibility(View.GONE);
                    approval.setVisibility(View.GONE);


                    break;
                case Utils.COMPLETE:
                    payment.setVisibility(View.GONE);
                    view.setVisibility(View.VISIBLE);
                    start.setVisibility(View.GONE);
                    end.setVisibility(View.GONE);
                    status.setVisibility(View.GONE);
                    approval.setVisibility(View.GONE);
                    break;

            }
            //Get the current sport
            mCurrentChild = currentChild;

        }

        @Override
        public void onClick(View v) {
            //Set up the detail intent
            switch (v.getId()) {
                case R.id.end:
                    Intent end = RequestModel.EndStatus(mContext,
                            mCurrentChild.get_id(),
                            mCurrentChild.getSecreteCode());
                    //Start the detail activity
                    mContext.startActivity(end);
                    break;

                case R.id.status:
                    Intent status = RequestModel.AddStatus(mContext,
                            mCurrentChild.get_id(),
                            mCurrentChild.getId(),
                            mCurrentChild.getPickup(),
                            mCurrentChild.getDrop(),
                            mCurrentChild.getDistance(),
                            mCurrentChild.getAmount(),
                            mCurrentChild.getRecipient(),
                            mCurrentChild.getRecipient_phone(),
                            mCurrentChild.getDesc(),
                            mCurrentChild.getDate(),
                            mCurrentChild.getStatus(),
                            mCurrentChild.getStatus());
                    //Start the detail activity
                    mContext.startActivity(status);
                    break;
                case R.id.view:
                    Intent detailIntent = RequestModel.starter(mContext,
                            mCurrentChild.get_id(),
                            mCurrentChild.getId(),
                            mCurrentChild.getPickup(),
                            mCurrentChild.getDrop(),
                            mCurrentChild.getDistance(),
                            mCurrentChild.getAmount(),
                            mCurrentChild.getRecipient(),
                            mCurrentChild.getRecipient_phone(),
                            mCurrentChild.getDesc(),
                            mCurrentChild.getDate(),
                            mCurrentChild.getStatus());
                    //Start the detail activity
                    mContext.startActivity(detailIntent);
                    break;
                case R.id.payment:
                    if (prefManager.getRole().equals("agent")) {
                        if (mCurrentChild.getStatus().equals(Utils.APPROVED))
                            Toast.makeText(mContext, "Payment not made yet", Toast.LENGTH_SHORT).show();
                        if (mCurrentChild.getStatus().equals(Utils.PAID)) {

                            Toast.makeText(mContext, "Payment made", Toast.LENGTH_SHORT).show();

                        }

                    } else {
                        Intent intent = new Intent(mContext, PaymentActivity.class);
                        intent.putExtra("amount", mCurrentChild.getAmount());
                        intent.putExtra("uid", mCurrentChild.get_id());
                        mContext.startActivity(intent);
                    }


                    break;
                case R.id.approval:
                    if (prefManager.getRole().equals("agent")) {
                        final String uid = mCurrentChild.get_id();
                        RequestModel db = new RequestModel(Utils.APPROVED, fireuser.getUid());


                        Map<String, Object> messageUpload = new HashMap<>();
                        messageUpload.put("/" + "status", Utils.APPROVED);
                        messageUpload.put("/" + "agent", fireuser.getUid());
                        mref.child("sedi").child("requests").child(uid).updateChildren(messageUpload).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                Toast.makeText(mContext, "Request Accepted", Toast.LENGTH_SHORT).show();

                                Utils.SMS_SEND sms_send = new Utils.SMS_SEND();
                                String msg = "Request ID: " + uid + " has been approve successfully, please make payment";
                                String phone = mCurrentChild.getSender_phone();
                                sms_send.execute(phone, msg);


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Failed", Toast.LENGTH_SHORT).show();

                            }
                        });
                        //  mref = mfirebaseDatabase.getReference().child("sedi").child("requests").child(mCurrentChild.getId()).child("status").setValue(Utils.APPROVED);
                    } else {
                        Toast.makeText(mContext, "Your request is pending approval, please check back shortly", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }

        }
    }
}
