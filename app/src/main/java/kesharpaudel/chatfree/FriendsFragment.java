package kesharpaudel.chatfree;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.zip.Inflater;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {


    private RecyclerView mFriendList;


    private FirebaseAuth mAuth;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mUserRef;

    private DatabaseReference mUserDatabse;

    private String mCurrent_user_id;

    private View mMainView;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendList = mMainView.findViewById(R.id.friend_list);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendDatabase.keepSynced(true);
        mUserDatabse=FirebaseDatabase.getInstance().getReference().child("Users");
        mUserDatabse.keepSynced(true);

        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());

        mFriendList.setHasFixedSize(true);
        mFriendList.setLayoutManager(new LinearLayoutManager(getContext()));


        return mMainView;


    }
    Query query=mFriendDatabase;
    FirebaseRecyclerOptions<Friends> options=
            new FirebaseRecyclerOptions.Builder<Friends>()
            .setQuery(query,Friends.class)
            .build();

    FirebaseRecyclerAdapter<Friends,FriendsViewHolder> friendsAdapter=
            new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Friends model) {

                    holder.setData(model.getDate());

                    String list_user_id=getRef(position).getKey();

                    mUserDatabse.child(list_user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                           String user_name=dataSnapshot.child("name").getValue().toString();
                            String user_image=dataSnapshot.child("image").getValue().toString();
                            //String userOnline=dataSnapshot.child("online").getValue().toString();

                            holder.setName(user_name);
                            holder.setImage(user_image);
                            if(dataSnapshot.hasChild("online")){
                                Boolean userOnline=(boolean)dataSnapshot.child("online").getValue();
                                holder.setUserOnline(userOnline);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }

                @NonNull
                @Override
                public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

                    View view= LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.user_single_layout, viewGroup, false);

                    mFriendList.setAdapter(friendsAdapter);
                    friendsAdapter.startListening();

                    return new FriendsViewHolder(view);


                }
            };

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
