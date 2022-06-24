package com.example.calleridv2.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calleridv2.R;
import com.example.calleridv2.controller.APIController;

import java.text.DecimalFormat;

public class AddCallScreen extends AppCompatActivity {
    EditText phoneField,durationField,subjectField,descriptionField;
    Button addCall;
    TextView alertText;
    ImageView exitScreen;
    ProgressDialog progressDialog;
    APIController apiController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_call_screen);
        apiController=new APIController(this);
//        progressDialog=new ProgressDialog(this);
        Bundle bundle=this.getIntent().getExtras();
        double duration= (double) bundle.get("duration");
        String phoneNumber=bundle.getString("phoneNumber");
        String contactId=bundle.getString("contactid");
        String contactIdRequest="/contacts("+contactId+")";
        phoneField=findViewById(R.id.numberField);
        phoneField.setText(phoneNumber);
        durationField=findViewById(R.id.duration);
        subjectField=findViewById(R.id.subject);
        descriptionField=findViewById(R.id.description);
        addCall=findViewById(R.id.addCall);
        alertText=findViewById(R.id.alertText);
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        String durationString=df.format(Math.ceil(duration)) +" min";
        durationField.setText(durationString);

        exitScreen=findViewById(R.id.exitCallScreen);
        exitScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(descriptionField.getText()==null || subjectField.getText()==null || descriptionField.getText().toString().equals("") || subjectField.getText().toString().equals("")){
                    alertText.setVisibility(View.VISIBLE);
                }else{
//                    progressDialog.setMessage("Adding your call!");
//                    progressDialog.show();
                    String subject=subjectField.getText().toString();
                    String description=descriptionField.getText().toString();
                    apiController.addCallApi(phoneNumber,durationString.split(" ")[0],subject,description,contactIdRequest);
                    Toast.makeText(AddCallScreen.this,"Call Added!",Toast.LENGTH_LONG).show();

                }
            }
        });

    }
}
