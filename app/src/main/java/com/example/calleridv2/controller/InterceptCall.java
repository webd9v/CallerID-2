package com.example.calleridv2.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;

import com.example.calleridv2.MainActivity;
import com.example.calleridv2.view.AddCallScreen;
import com.example.calleridv2.view.CallScreen;

import java.util.Date;

public class InterceptCall extends BroadcastReceiver {
    private static final String TAG = "CustomBroadcastReceiver";
    static long start=0,end=0;
    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            boolean isFound;


            if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){

//                Toast.makeText(context,"Incoming Call State",Toast.LENGTH_SHORT).show();
//                Toast.makeText(context,"Ringing State Number is :"+incomingNumber,Toast.LENGTH_SHORT).show();

                if(incomingNumber!=null) {
                    try{
                        Date date=new Date();
                        this.start=date.getTime();
                        String contactInfo= MainActivity.contactsByPhone.get(incomingNumber);
                        System.out.println("++++++++++HashInfo from intercept:"+contactInfo);
                        System.out.println("+++++++start:"+start);


                        String phoneNumber=contactInfo.split(":")[0];
                        String address=contactInfo.split(":")[1];
                        String email=contactInfo.split(":")[2];
                        isFound=true;
                        Intent intent1 = new Intent(context, CallScreen.class);
                        intent1.putExtra("isFound",isFound);
                        intent1.putExtra("fullname",phoneNumber);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.putExtra("address",address);
                        intent1.putExtra("email",email);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }catch (Exception e){
                        isFound=false;
                        Intent intent1 = new Intent(context, CallScreen.class);
                        intent1.putExtra("isFound",isFound);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }

                }
            }
//            if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))){
//                Toast.makeText(context,"Call Received State",Toast.LENGTH_SHORT).show();
//            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                try {
                    if(MainActivity.contactsByPhone.get(incomingNumber)!=null) {
                        Date date = new Date();
                        this.end = date.getTime();
                        System.out.println("+++++++end:"+end);
                        long difference=end-start;
                        System.out.println("+++++++difference:"+difference);

                        double seconds=(double) difference/1000;
                        System.out.println("+++++++seconds:"+seconds);

                        double duration=(double) seconds/60;
                        System.out.println("+++++++duration:"+duration);
                        String contactInfo=MainActivity.contactsByPhone.get(incomingNumber);
                        String contactId=contactInfo.split(":")[3];
                        System.out.println("Duration:"+duration);
                        Intent intent1 = new Intent(context, AddCallScreen.class);
                        intent1.putExtra("duration",duration);
                        intent1.putExtra("phoneNumber", incomingNumber);
                        intent1.putExtra("contactid",contactId);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(intent1);
                    }

                }catch (Exception e){

                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
