package com.example.calleridv2.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calleridv2.MainActivity;
import com.example.calleridv2.R;
import com.example.calleridv2.controller.APIController;

public class ProfileScreen extends AppCompatActivity {
    TextView name,email;
    Button signInOrOut;
    ImageView exit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        APIController apiController=new APIController(ProfileScreen.this);
        setContentView(R.layout.profile_screen);
        name=findViewById(R.id.username);
        email=findViewById(R.id.email);
        signInOrOut=findViewById(R.id.signInOrOut);
        signInOrOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(signInOrOut.getText().toString().toLowerCase().equals("sign out")){
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        exit=findViewById(R.id.exitProfile);
        name.setText("Name: "+APIController.name.toUpperCase());
        email.setText("Email: "+APIController.activeAccount.getUsername());
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
    public void updateUI(Boolean isLoggedIn){
        if(isLoggedIn){
            name.setVisibility(View.VISIBLE);
            email.setVisibility(View.VISIBLE);
            signInOrOut.setText("sign out");
        }
    }
}
