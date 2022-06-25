package com.example.calleridv2.view;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calleridv2.MainActivity;
import com.example.calleridv2.R;
import com.example.calleridv2.controller.CLAdapter;
import com.example.calleridv2.model.CallLogModel;

import java.util.ArrayList;

public class CallLogScreen extends AppCompatActivity {
//    ListView callsList;
    ArrayList<CallLogModel> info;
    ImageView exitCallLog;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_log_screen);
        recyclerView=findViewById(R.id.mRecyclerView);
        displayCallLog();
        exitCallLog=findViewById(R.id.exitCallLog);
        exitCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void displayCallLog() {
        info = new ArrayList<>();

        String singleRecord = "";
        Uri uriCallLog = Uri.parse("content://call_log/calls");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Cursor cursorCallLogs = getContentResolver().query(uriCallLog, null, null, null);
            cursorCallLogs.moveToFirst();
            do {
                @SuppressLint("Range") String number = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER));
                @SuppressLint("Range") String name = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME));
                @SuppressLint("Range") String type = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE));
                @SuppressLint("Range") String duration = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION));
                Boolean isUnknown=false;
                if (type.equals("3") || type.equals("5")) {
                    type = "Unanswered";
                } else if (type.equals("1")) {
                    type = "Answered Incoming call";
                } else if (type.equals("2")) {
                    type = "Answered Outgoing call";

                }
                duration += "s";
                if (name == null) {
                    String testNbr= MainActivity.contactsByPhone.get(number);
                    if(testNbr==null){
                        name = "Unknown";
                        isUnknown=true;
                    }else{
                        name=testNbr.split(":")[0];
                    }
                }
                int imgSrc=R.drawable.ic_baseline_add_24;
                CallLogModel callLogModel=new CallLogModel(name,number,type,duration,imgSrc,isUnknown);
                System.out.println(callLogModel.toString());
//                singleRecord = "Number: " + number + "\nName: " + name + "\ntype: " + type + "\nDuration: " + duration + "\n";
                info.add(callLogModel);

            } while (cursorCallLogs.moveToNext());
            ArrayList<CallLogModel> info1=new ArrayList<>();
            for(int i=info.size()-1;i>=0;i--){
                CallLogModel call=info.get(i);
                info1.add(call);
            }
            info=info1;
        }
        System.out.println(info.toString());
        CLAdapter adapter=new CLAdapter(CallLogScreen.this,info);
        System.out.println("AD"+adapter.toString());
        if(adapter!=null) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(CallLogScreen.this));
        }
//        ArrayList<CallLogModel> finalInfo = info;
//        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String contact= finalInfo.get(position);
//                String name=contact.split("\n")[1];
//                name=name.split(":")[1].trim();
//                String number=contact.split("\n")[0].split(":")[1].trim();
//
//                if(name.equals("Unknown")){
//                    Intent intent=new Intent(CallLogScreen.this,AddContactScreen.class);
//
//                    intent.putExtra("phoneNumber",number);
//                    startActivityForResult(intent,0);
//
//                }
//
//            }
//        });
    }
}
