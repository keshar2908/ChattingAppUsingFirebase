package kesharpaudel.chatfree;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

class FriendsViewHolder extends RecyclerView.ViewHolder {
    View mView;
    public FriendsViewHolder(View view) {
        super(view);
        mView=view;

    }

    public void setData(String date){
        TextView userStatusView=mView.findViewById(R.id.user_single_status);
        userStatusView.setText(date);
    }

    public void setName(String name){
        TextView userNameVies=mView.findViewById(R.id.user_single_name);
        userNameVies.setText(name);




    }
    public void setImage(String image){
        CircleImageView userImageView=mView.findViewById(R.id.user_single_image);
        Picasso.get().load(image).placeholder(R.drawable.profile).into(userImageView);
    }
    public void setUserOnline(boolean online_status){
        ImageView userOnlineView=mView.findViewById(R.id.user_single_online_icon);
        if(online_status==true){
            userOnlineView.setVisibility(View.VISIBLE);
        }else{
            userOnlineView.setVisibility(View.INVISIBLE);
        }
    }

    }

