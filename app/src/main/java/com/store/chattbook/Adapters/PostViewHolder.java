package com.store.chattbook.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.store.chattbook.Activities.PostActivity;
import com.store.chattbook.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostViewHolder extends RecyclerView.ViewHolder {

    Context context;

    public PostViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
    }

    private TextView date, description, fullname, time, uid;
    private ImageView post_image;
    private CircleImageView profile_image;
    private RelativeLayout rel_animate;
    View mview;


    public PostViewHolder(@NonNull View itemView) {
        super(itemView);
        mview = itemView;


        date = mview.findViewById(R.id.post_date);
        description = mview.findViewById(R.id.post_description);
        fullname = mview.findViewById(R.id.post_profile_name);
        post_image = mview.findViewById(R.id.post_image);
        profile_image = mview.findViewById(R.id.post_profile_image);
        rel_animate=mview.findViewById(R.id.rel_animate);
        //time = mview.findViewById(R.id.post_time);

    }

    public void setDate(String datee) {
        date.setText(datee);
    }

    public void setDescription(String descc) {
        description.setText(descc);
    }

    public void setFullname(String fullnamee) {
        fullname.setText(fullnamee);
    }

    public void setPost_image(Context ctx, final String post_imagee) {
       // Glide.with(ctx).load(post_imagee).into(post_image);
        Picasso.get().load(post_imagee).networkPolicy(NetworkPolicy.OFFLINE).resize(350,200).into(post_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(post_imagee).into(post_image);
            }
        });
    }

    public void setProfile_image(final Context ctx, final String profile_imagee) {
        //Glide.with(ctx).load(profile_imagee).into(profile_image);
        Picasso.get().load(profile_imagee).placeholder(R.drawable.like_icon).networkPolicy(NetworkPolicy.OFFLINE).into(profile_image, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(profile_imagee).into(profile_image);
            }
        });
    }




//    public void setTime(String timee) {
//        time.setText(timee);
//    }


}
