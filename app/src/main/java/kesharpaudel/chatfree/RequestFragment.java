package kesharpaudel.chatfree;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mAuth=FirebaseAuth.getInstance();

        mUserRef= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(mAuth.getCurrentUser().getUid());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request, container, false);

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
