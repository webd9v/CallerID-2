package com.example.calleridv2.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calleridv2.R;

public class CallScreen extends AppCompatActivity {
    TextView outputContactInfo;
    Button addContactInDynamics;
    ImageView exitCallScreen;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_screen);
        Bundle bundle=this.getIntent().getExtras();
        String number=bundle.getString("phoneNumber");
        exitCallScreen=findViewById(R.id.exitCallScreen);
        exitCallScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        boolean isFound=bundle.getBoolean("isFound");
        addContactInDynamics=findViewById(R.id.addContactDynamics);
        outputContactInfo=(TextView) findViewById(R.id.outputContactInfo);
        if(!isFound) {
            outputContactInfo.setText("Incoming call from: " + number);
            System.out.println("isFound:"+isFound+" number:"+number);
            addContactInDynamics.setVisibility(View.VISIBLE);
            addContactInDynamics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(CallScreen.this,AddContactScreen.class);
                    intent.putExtra("phoneNumber",number);
                    startActivityForResult(intent,0);
                }
            });
        }else{
            String fullname=bundle.getString("fullname");
            String address=bundle.getString("address");
            String email=bundle.getString("email");
            if(address==null){
                address="No information";
            }
            if(email==null){
                email="No information";
            }
            outputContactInfo.setText(fullname+"\n"+number+"\n"+"Address: "+address+"\n"+"Email: "+email);
            System.out.println("isFound:"+isFound+" number:"+number+" name:"+fullname);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 0:{
                switch (resultCode){
                    case RESULT_OK:{
                        finish();
                    }
                }
            }
        }
    }
}
