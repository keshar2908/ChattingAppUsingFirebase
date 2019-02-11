package kesharpaudel.chatfree;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

public class UsersActivity extends AppCompatActivity {

    private android.support.v7.widget.Toolbar mToolbar;
    private RecyclerView mUserList;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar= findViewById(R.id.users_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth= FirebaseAuth.getInstance();

        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());




        mUserList=findViewById(R.id.users_list);
        mUserList.setHasFixedSize(true);
        mUserList.setLayoutManager(new LinearLayoutManager(this));







        }
    Query query = FirebaseDatabase.getInstance()
            .getReference()
            .child("Users")
            .limitToLast(50);
    FirebaseRecyclerOptions<Users> options =
            new FirebaseRecyclerOptions.Builder<Users>()
                    .setQuery(query, Users.class)
                    .build();


    FirebaseRecyclerAdapter<Users,ViewHolder> adapter = new FirebaseRecyclerAdapter<Users, ViewHolder>(options) {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.user_single_layout, parent, false);

            return new ViewHolder(view);
        }

        @Override
        protected void onBindViewHolder(ViewHolder holder, int position, @NonNull Users users) {

            holder.setDetail(users);

            final String userId=getRef(position).getKey();

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent profileIntet=new Intent(UsersActivity.this,ProfileActivity.class);
                    profileIntet.putExtra("userId",userId);
                    startActivity(profileIntet);

                }
            });

        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        mUserList.setAdapter(adapter);
        mUserRef.child("online").setValue(true);
    }


    @Override
    public void onStop() {
        super.onStop();
        mUserRef.child("online").setValue(false);
    }






}





