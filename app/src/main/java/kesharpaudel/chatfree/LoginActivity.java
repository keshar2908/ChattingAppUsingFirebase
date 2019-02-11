package kesharpaudel.chatfree;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private EditText mLogEmail;
    private EditText mLogPassword;
    private Button mLogBtn;
    private android.support.v7.widget.Toolbar mLogToolbar;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mLogEmail=(EditText) findViewById(R.id.log_email);
        mLogPassword=(EditText) findViewById(R.id.log_password);
        mLogBtn=(Button)findViewById(R.id.log_btn);
        mLogToolbar=findViewById(R.id.log_toolbar);
        setSupportActionBar(mLogToolbar);
        getSupportActionBar().setTitle("Login");

        mLoginProgress=new ProgressDialog(this);
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users");


        mLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email=mLogEmail.getText().toString();
                String password=mLogPassword.getText().toString();
                if(!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Logging In");
                    mLoginProgress.setMessage("Please wait while we check your credential.");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();
                    LoginUser(email,password);

                }
            }
        });
    }

    private void LoginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull final Task<AuthResult> task) {

                if(task.isSuccessful()){

                    mLoginProgress.dismiss();

                    String current_user_id=mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    if(task.isSuccessful()){
                                        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    }else {
                                        Toast.makeText(LoginActivity.this, "Something went wrong! Please Try Again.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });



                }else {
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Cannot Sign in. Please check the form and try again", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
