package com.example.calleridv2.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.calleridv2.R;
import com.example.calleridv2.controller.APIController;

public class AddContactScreen extends AppCompatActivity {
    ImageView exitCallScreen;
    Button addContactBtn;
    EditText firstnameField,lastnameField,emailField,phoneNumberField;
    APIController apiController;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_screen);
        apiController=new APIController(this);
        Bundle bundle=this.getIntent().getExtras();
        String number=bundle.getString("phoneNumber");
        phoneNumberField=findViewById(R.id.numberField);
        phoneNumberField.setText(number);
        exitCallScreen=findViewById(R.id.exitCallScreen);
        exitCallScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addContactBtn=findViewById(R.id.addContact);
        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstnameField=findViewById(R.id.firstnameField);
                lastnameField=findViewById(R.id.lastnameField);
                emailField=findViewById(R.id.emailField);

                String firstname=firstnameField.getText().toString();
                String lastname=lastnameField.getText().toString();
                String email=emailField.getText().toString();
                apiController.addContactApi(firstname,lastname,number,email);
                Toast.makeText(AddContactScreen.this,"Contact Added!",Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
