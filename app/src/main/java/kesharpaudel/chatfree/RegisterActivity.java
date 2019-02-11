package kesharpaudel.chatfree;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.security.PrivateKey;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText mDisplayName;
    private EditText mEmail;
    private EditText mPassword;
    private Button mCreateBtn;

    private android.support.v7.widget.Toolbar mToolbar;
    private DatabaseReference mDatabse;
    private DatabaseReference mUserDatabase;


    //Progress Dialog
    private ProgressDialog mRegProgress;

    //Firebase Auth
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mDisplayName=(EditText) findViewById(R.id.reg_display_name);
        mEmail=(EditText) findViewById(R.id.reg_email);
        mPassword=(EditText) findViewById(R.id.reg_password);
        mCreateBtn=(Button)findViewById(R.id.reg_create_btn);
        mToolbar=findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Progress dialog
        mRegProgress=new ProgressDialog(this);
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String display_name=mDisplayName.getText().toString();
                String email=mEmail.getText().toString();
                String password=mPassword.getText().toString();

                if(!TextUtils.isEmpty(display_name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password)){
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    register_user(display_name,email,password);
                }



            }

            private void register_user(final String display_name, String email, String password) {

                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            FirebaseUser current_user=FirebaseAuth.getInstance().getCurrentUser();
                            String uid=current_user.getUid();

                            mDatabse=FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(uid);
                            HashMap<String,String> userMap=new HashMap<>();
                            userMap.put("name",display_name);
                            userMap.put("status","Hi there i'm using Chatfree Chat App");
                            userMap.put("image","default");


                            mDatabse.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull final Task<Void> task) {

                                    if(task.isSuccessful()){


                                        mRegProgress.dismiss();

                                        String current_user_id=mAuth.getCurrentUser().getUid();
                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        if(task.isSuccessful()){
                                                            Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
                                                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            startActivity(mainIntent);
                                                            finish();
                                                        }else {
                                                            Toast.makeText(RegisterActivity.this, "Something went Wrong! Please try Again.", Toast.LENGTH_SHORT).show();
                                                        }




                                                    }
                                                });



                                    }

                                }
                            });


                        }else {
                            mRegProgress.hide();
                            Toast.makeText(RegisterActivity.this, "Something went wrong! Please wait a while.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });
    }
}
//apply plugin: 'com.google.gms.google-services'