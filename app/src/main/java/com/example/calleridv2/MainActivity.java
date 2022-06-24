package com.example.calleridv2;

import static android.Manifest.permission.READ_CALL_LOG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.calleridv2.controller.APIController;
import com.example.calleridv2.view.CallLogScreen;
import com.example.calleridv2.view.SavedContactInfoScreen;
import com.google.gson.JsonObject;
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public String authToken;
    private static final String TAG = MainActivity.class.getSimpleName();
    ImageButton refreshContacts;
    /* UI & Debugging Variables */
    Button signInButton;
    Button signOutButton;
    JSONArray emailResponse;
    TextView logTextView;
    TextView currentUserTextView;
    public static HashMap<String,String> contactsByPhone;
    ArrayList<String> contactsForListView;
    ListView displayContacts;
    LinearLayout contactsLayout;
    ArrayAdapter adapterForContactsList;
    //    ProgressDialog progressDialog;
    Button displayCallLog;
    APIController apiController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiController=new APIController(this);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,Manifest.permission.READ_PHONE_STATE)){
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);

            }else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_PHONE_STATE},1);
            }
        }else{

        }
        int readContactsPermissionLog =
                ContextCompat.checkSelfPermission(this, READ_CALL_LOG);
        if(readContactsPermissionLog != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{READ_CALL_LOG},2); }
        initializeUI();

    }
    public void initializeUI(){
        signInButton = findViewById(R.id.signIn);
        signOutButton = findViewById(R.id.clearCache);
        refreshContacts=findViewById(R.id.refreshContacts);
        refreshContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiController.getSilentToken();
            }
        });
        displayCallLog=findViewById(R.id.displayCallLog);
        displayCallLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, CallLogScreen.class);
                intent.putExtra("token",authToken);
                startActivity(intent);
            }
        });
        logTextView = findViewById(R.id.txt_log);
        currentUserTextView = findViewById(R.id.current_user);
        contactsLayout=findViewById(R.id.contactsSection);
        apiController.loadLoggedInAcc();
        //Sign in user
        signInButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                apiController.signInForUser();
            }
        });

        //Sign out user
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiController.signOutForUser();
            }
        });

        //Interactive
//        callGraphApiInteractiveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSingleAccountApp == null) {
//                    return;
//                }
//                mSingleAccountApp.acquireToken(MainActivity.this, SCOPES, getAuthInteractiveCallback());
//            }
//        });

        //Silent
//        callGraphApiSilentButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (mSingleAccountApp == null){
//                    return;
//                }
//                mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
//            }
//        });
    }



    public void updateUI(@Nullable final IAccount account) {
        if (account != null) {
            signInButton.setEnabled(false);
            signOutButton.setEnabled(true);
            currentUserTextView.setText("User: "+account.getUsername());
            contactsLayout.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {displayCallLog.setVisibility(View.VISIBLE);
                }
            }, 3000);
        } else {
            signInButton.setEnabled(true);
            signOutButton.setEnabled(false);
            displayCallLog.setVisibility(View.GONE);
            contactsLayout.setVisibility(View.GONE);
            currentUserTextView.setText("");
            logTextView.setText("Please Sign in to Display your informations!");
            logTextView.setText("");
        }
    }
    public void displayError(@NonNull final Exception exception) {
//        logTextView.setText(exception.toString());
        logTextView.setText("Session Expired ! Please sign in again.");
    }
    public void displayGraphResult(@NonNull final JsonObject graphResponse) {
        System.out.println(graphResponse);
        JsonObject obj1=graphResponse.getAsJsonObject("owner");
        JsonObject obj2=obj1.getAsJsonObject("user");

        String name=obj2.get("displayName").toString();
        logTextView.setText("Hello "+name.replaceAll("\"",""));


    }
    public void performOperationOnSignOut() {
        final String signOutText = "Signed Out.";
        currentUserTextView.setText("");
        Toast.makeText(getApplicationContext(), signOutText, Toast.LENGTH_SHORT)
                .show();
    }
    //Phone call
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED){

                    }else{
                        Toast.makeText(this,"Permission denied!",Toast.LENGTH_SHORT).show();
                    }
                    return ;
                }
        }
    }
    public void getContactsSaleSuccess(JSONObject response){
        contactsByPhone=new HashMap<String,String>();
        displayContacts=findViewById(R.id.contactVolley);
        contactsForListView=new ArrayList<>();
        adapterForContactsList=new ArrayAdapter(getApplicationContext(),R.layout.list_item_style,contactsForListView);
        displayContacts.setAdapter(adapterForContactsList);
        displayContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String contact=contactsForListView.get(position);
                String number=contact.split("\n")[1].split(":")[1].trim();
                String contactInfoNAE=contactsByPhone.get(number);
                String name=contactInfoNAE.split(":")[0];
                String address=contactInfoNAE.split(":")[1];
                String email=contactInfoNAE.split(":")[2];
                System.out.println("Email from hash: "+email);
                Intent intent=new Intent(MainActivity.this, SavedContactInfoScreen.class);
                intent.putExtra("phoneNumber",number);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                intent.putExtra("address",address);
                HashMap<String,String> emails=new HashMap<>();
                ArrayList<String> ids=new ArrayList<>();
                String dateReceived,bodyPreview,subject,objId;
                JSONArray valuesEmails=emailResponse;
                int i=0;
                int j;
                if(valuesEmails!=null){
                    for(j=0;j<valuesEmails.length() || j<100;j++){
                        try {
                            JSONObject mailInfo=valuesEmails.optJSONObject(j);
                            System.out.println("+++mailInfo:"+mailInfo+"\nvaluesEmails"+valuesEmails);
                            JSONObject from=mailInfo.optJSONObject("from");
                            System.out.println("+++from"+from);
                            JSONObject emailAddressObj=from.optJSONObject("emailAddress");
                            String emailAddress=emailAddressObj.optString("address");
                            if(emailAddress.equals(email)){
                                dateReceived=mailInfo.optString("receivedDateTime");
                                bodyPreview=mailInfo.optString("bodyPreview");
                                subject=mailInfo.optString("subject");
                                objId=mailInfo.getString("id");
                                emails.put(objId,dateReceived+":%"+subject+":%"+bodyPreview);
                                System.out.println("Item in hash:"+emails.get(objId));
                                ids.add(objId);
                                i++;

                            }
                            if (i == 2) {
                                break;
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            System.out.println("Counter: "+i);
                            break;
                        }

                    }
                    intent.putExtra("emails",emails);
                    intent.putExtra("ids",ids);
                    startActivity(intent);
                }else{
                    apiController.getSilentToken();
                }
            }
        });
        System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++Success");
        JSONArray values = response.optJSONArray("value");
        JSONObject contactObject;

        //mobilephone , fullname , addressComposite, emailaddress1
        String mobilephone,fullname,addressComposite,emailaddress1,contactId;
        for(int i = 0;i<values.length();i++){
            contactObject=values.optJSONObject(i);
            mobilephone=contactObject.optString("mobilephone");
            fullname=contactObject.optString("fullname");
            addressComposite=contactObject.optString("address1_composite");
            emailaddress1=contactObject.optString("emailaddress1");
            contactId=contactObject.optString("contactid");
            if(mobilephone==null || mobilephone.equals("null")){
                contactsForListView.add("Contact Name: "+fullname+"\n"+"Number: N/A");

            }else{
                contactsForListView.add("Contact Name: "+fullname+"\n"+"Number: "+mobilephone);

            }
            contactsByPhone.put(mobilephone,fullname+":"+addressComposite+":"+emailaddress1+":"+contactId);

        }
        System.out.println("+++++++++++++++++++++HashMap Content"+contactsByPhone.toString()+" "+contactsByPhone.get("423-555-0123"));

    }
    public void graphAPIEmailSuccess(JSONObject response){
        System.out.println("+++++++Success: ");
        emailResponse=response.optJSONArray("value");
        System.out.println(emailResponse);
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}