package kesharpaudel.chatfree;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ViewHolder extends RecyclerView.ViewHolder{

    View mView;



    public  ViewHolder(@NonNull View itemView) {
        super(itemView);

        mView=itemView;
    }

    public void setDetail(final Users users){

        TextView mName=(TextView)mView.findViewById(R.id.user_single_name);
        TextView mStatus=(TextView)mView.findViewById(R.id.user_single_status);
        CircleImageView mImage=(CircleImageView)mView.findViewById(R.id.user_single_image);

        mName.setText(users.getName());
        mStatus.setText(users.getStatus());
        if(!users.getImage().equals("default")){
            Picasso.get().load(users.getImage()).placeholder(R.drawable.profile).into(mImage);
        }else{
            Picasso.get().load(users.getImage()).placeholder(R.drawable.profile).into(mImage);
        }
    }
}