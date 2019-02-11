package kesharpaudel.chatfree;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartActivity extends AppCompatActivity {

    private Button mRegBtn;
    private Button mRegBtn1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mRegBtn=(Button)findViewById(R.id.start_reg_btn);
        mRegBtn1=(Button)findViewById(R.id.start_reg_btn1);

        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent= new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(reg_intent);
            }
        });
        mRegBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent login_intent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }
}
