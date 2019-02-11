package kesharpaudel.chatfree;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mprofileImage;
    private TextView mProfileName,mProfileStatus,mProfileFriendsCount;
    private Button mProfileSendReqBtn,mProfileDecBtn;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mUserRef;
    private ProgressDialog mProgress;

    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    //private DatabaseReference mNotificationDatabase;

    private DatabaseReference mRootRef;

    private FirebaseUser mCurrentUser;

    private String mCurrent_state;
    private String display_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id=getIntent().getStringExtra("userId");

        mRootRef=FirebaseDatabase.getInstance().getReference();

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase=FirebaseDatabase.getInstance().getReference().child("Friends_req");
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
      //  mNotificationDatabase=FirebaseDatabase.getInstance().getReference().child("notification");
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        mprofileImage=(ImageView)findViewById(R.id.profile_image);
        mProfileName=(TextView)findViewById(R.id.profile_display_name);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
        mProfileFriendsCount=(TextView)findViewById(R.id.profile_totalfriends);
        mProfileSendReqBtn=(Button)findViewById(R.id.profile_send_req_btn);
        mProfileDecBtn=(Button)findViewById(R.id.profile_decline_btn);

        mProfileDecBtn.setVisibility(View.INVISIBLE);
        mProfileDecBtn.setEnabled(false);

        mCurrent_state="not_friend";

        mProgress=new ProgressDialog(this);
        mProgress.setTitle("Loading User Data");
        mProgress.setMessage("Please wait while we load the user data");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                display_name=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                if(!image.equals("default")){
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(mprofileImage);
                }else{
                    Picasso.get().load(image).placeholder(R.drawable.profile).into(mprofileImage);
                }

                //------------ FRIENDS LIST/REQUEST FEATURE -------------
                mFriendReqDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.hasChild(user_id)){
                            String req_type=dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if(req_type.equals("received")){

                                mProfileSendReqBtn.setEnabled(true);
                                mCurrent_state="req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");

                                mProfileDecBtn.setVisibility(View.VISIBLE);
                                mProfileDecBtn.setEnabled(true);


                            }else if(req_type.equals("sent")){
                                mCurrent_state="req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mProfileDecBtn.setVisibility(View.INVISIBLE);
                                mProfileDecBtn.setEnabled(false);
                            }

                            mProgress.dismiss();
                        }else {
                            mFriendDatabase.child(mCurrentUser.getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(user_id)){

                                        mCurrent_state="friends";
                                        mProfileSendReqBtn.setText("UnFriend "+display_name);
                                        mProfileDecBtn.setVisibility(View.INVISIBLE);
                                        mProfileDecBtn.setEnabled(false);



                                    }

                                    mProgress.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                    mProgress.dismiss();

                                }
                            });
                        }







                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });




            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProfileSendReqBtn.setEnabled(false);

                //- --------------- NOT FRIENDS STATE -------------

                if(mCurrent_state.equals("not_friend")){

                    Map requestMap=new HashMap();
                    requestMap.put( "Friends_req/" + mCurrentUser.getUid() + "/" + user_id + "/request_type","sent");
                    requestMap.put( "Friends_req/" + user_id + "/" + mCurrentUser.getUid() +"/request_type","received");

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {


                            if(databaseError==null){
                                mCurrent_state="req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mProfileDecBtn.setVisibility(View.INVISIBLE);
                                mProfileDecBtn.setEnabled(false);
                            }else {
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);


                            /*HashMap<String,String> notificationData=new HashMap<>();
                                        notificationData.put("from",mCurrentUser.getUid());
                                        notificationData.put("type","request");

                                        mNotificationDatabase.child(user_id).push().setValue(notificationData)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {



                                                    }
                                                });*/

                        }
                    });


                }

                //- --------------- CANCEL FRIEND REQUEST -------------
                if(mCurrent_state.equals("req_sent")){

                    Map cancelMap=new HashMap();
                    cancelMap.put("Friends_req/" + mCurrentUser.getUid() + "/" + user_id ,null);
                    cancelMap.put("Friends_req/" + user_id + "/" + mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(cancelMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError==null){

                                mCurrent_state="not_friend";
                                mProfileSendReqBtn.setText("Send Friend Request");
                                mProfileDecBtn.setVisibility(View.INVISIBLE);
                                mProfileDecBtn.setEnabled(false);
                            }else{
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });


                }

                //-------------- REQ RECEIVED STATE

                if(mCurrent_state.equals("req_received")){

                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsMap=new HashMap();
                    friendsMap.put("Friends/" + mCurrentUser.getUid()+ "/" + user_id + "/date",currentDate);
                    friendsMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid() + "/date",currentDate);

                    friendsMap.put("Friends_req/" + mCurrentUser.getUid() + "/" + user_id,null);
                    friendsMap.put("Friends_req/" + user_id + "/" + mCurrentUser.getUid(),null);

                    mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                            if(databaseError==null){

                                mCurrent_state="friends";
                                mProfileSendReqBtn.setText("UnFriend "+display_name);
                                mProfileDecBtn.setVisibility(View.INVISIBLE);
                                mProfileDecBtn.setEnabled(false);
                            }else{
                                String error=databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);

                        }
                    });

                }

                //---------- UNFRIEND FRIEND---------
                 if(mCurrent_state.equals("friends")){

                    Map unfriendMap=new HashMap();
                    unfriendMap.put("Friends/" + mCurrentUser.getUid() + "/" +user_id,null);
                     unfriendMap.put("Friends/" + user_id + "/" + mCurrentUser.getUid(),null);

                     mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                         @Override
                         public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                             if(databaseError==null){

                                 mCurrent_state="not_friend";
                                 mProfileSendReqBtn.setText("Send Friend Request");
                                 mProfileDecBtn.setVisibility(View.INVISIBLE);
                                 mProfileDecBtn.setEnabled(false);
                             }else {
                                 String error=databaseError.getMessage();
                                 Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                             }

                             mProfileSendReqBtn.setEnabled(true);

                         }
                     });



                }

            }
        });




    }
    @Override
    public void onStart() {
        super.onStart();
        mUserRef.child("online").setValue(true);
    }

    @Override
    public void onStop() {
        super.onStop();
        mUserRef.child("online").setValue(false);
    }



}
