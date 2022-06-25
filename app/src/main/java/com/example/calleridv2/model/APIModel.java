package com.example.calleridv2.model;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.calleridv2.MainActivity;
import com.example.calleridv2.R;
import com.example.calleridv2.controller.APIController;
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

public class APIModel {
    private APIController apiController;
    private final static String[] SCOPES = {"Files.Read","Mail.Read"};
    /* Azure AD v2 Configs */
    final static String AUTHORITY = "https://login.microsoftonline.com/common";
    final static String MSSMESSAGES_URL="https://apimd365.azure-api.net/api/contacts";
    final static  String EMAILS_URL="https://graph.microsoft.com/v1.0/me/messages/?$select=from,bodyPreview,subject,receivedDateTime";
    final static String MSSCALLS_URL="https://apimd365.azure-api.net/api/phonecalls";
    final static String MSSCONTACTS_URL="https://apimd365.azure-api.net/api/contacts";

    private ISingleAccountPublicClientApplication mSingleAccountApp;
    public String authToken;
    public static IAccount activeAccount;
    public APIModel(APIController apiController){
        this.apiController=apiController;

    }
    public void loadAccount() {
        if (mSingleAccountApp == null) {
            return;
        }

        mSingleAccountApp.getCurrentAccountAsync(new ISingleAccountPublicClientApplication.CurrentAccountCallback() {
            @Override
            public void onAccountLoaded(@Nullable IAccount activeAccount) {
                // You can use the account data to update your UI or your app database.

                APIController.activeAccount=activeAccount;
                APIModel.activeAccount=activeAccount;
                apiController.updateUI(activeAccount);
            }

            @Override
            public void onAccountChanged(@Nullable IAccount priorAccount, @Nullable IAccount currentAccount) {
                if (currentAccount == null) {
                    // Perform a cleanup task as the signed-in account changed.
                    apiController.performOperationOnSignOut();
                }
            }

            @Override
            public void onError(@NonNull MsalException exception) {
                apiController.displayError(exception);
            }
        });
    }
    public void loadLoggedInAcc(){
        PublicClientApplication.createSingleAccountPublicClientApplication(apiController.getAppContext(),
                R.raw.auth_config_single_account, new IPublicClientApplication.ISingleAccountApplicationCreatedListener() {
                    @Override
                    public void onCreated(ISingleAccountPublicClientApplication application) {

                        mSingleAccountApp = application;
                        loadAccount();
                        mSingleAccountApp.acquireTokenSilentAsync(SCOPES, AUTHORITY, getAuthSilentCallback());
                    }
                    @Override
                    public void onError(MsalException exception) {
                        apiController.displayError(exception);
                    }
                });
    }
    public SilentAuthenticationCallback getAuthSilentCallback() {
        return new SilentAuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                callGraphAPI(authenticationResult);
                callGraphApiForEmail(authenticationResult);

                getContactsSales();
                authToken=authenticationResult.getAccessToken();
            }
            @Override
            public void onError(MsalException exception) {
                apiController.displayError(exception);
            }
        };
    }
    public AuthenticationCallback getAuthInteractiveCallback() {
        return new AuthenticationCallback() {
            @Override
            public void onSuccess(IAuthenticationResult authenticationResult) {
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                /* Update UI */
                IAccount activeAccount=authenticationResult.getAccount();
                APIModel.activeAccount=activeAccount;

                apiController.updateUI(activeAccount);
                /* call graph */
                callGraphAPI(authenticationResult);
                callGraphApiForEmail(authenticationResult);

                getContactsSales();
                authToken=authenticationResult.getAccessToken();

            }

            @Override
            public void onError(MsalException exception) {
                /* Failed to acquireToken */
                apiController.displayError(exception);
            }
            @Override
            public void onCancel() {
                /* User canceled the authentication */
            }
        };
    }
    public void signInUser(AppCompatActivity appCompatActivity){
        {
            if (mSingleAccountApp == null) {
                return;
            }
            mSingleAccountApp.signIn(appCompatActivity, null, SCOPES, getAuthInteractiveCallback());
        }
    }
    public void signOutUser(){
        if (mSingleAccountApp == null){
            return;
        }
        mSingleAccountApp.signOut(new ISingleAccountPublicClientApplication.SignOutCallback() {
            @Override
            public void onSignOut() {
                apiController.updateUI(null);
                apiController.performOperationOnSignOut();

            }
            @Override
            public void onError(@NonNull MsalException exception){
                apiController.displayError(exception);
            }
        });
    }
    public void getContactsSales(){


        RequestQueue queue= Volley.newRequestQueue(apiController.getAppContext());
        JSONObject parameters=new JSONObject();
        try{
            parameters.put("key","value");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, MSSMESSAGES_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                apiController.handleContactsSales(response);
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error "+error.networkResponse);


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
//                headers.put("Content-Type","application/json");
//                headers.put("Authorization","Bearer "+authenticationResult.getAccessToken());
//                headers.put("Accept","application/json");
//                headers.put("OData-MaxVersion","4.0");
//                headers.put("OData-Version","4.0");
//                headers.put("If-None-Match","null");
                headers.put("Host","apimd365.azure-api.net");
                headers.put("Ocp-Apim-Subscription-Key","ff217ee6bbf74c54972a77cf853a7436");
                headers.put("Ocp-Apim-Trace","true");
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000*2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
    public void callGraphApiForEmail(IAuthenticationResult authenticationResult){
        final String accessToken = authenticationResult.getAccessToken();

        RequestQueue queue= Volley.newRequestQueue(apiController.getAppContext());
        JSONObject parameters=new JSONObject();
        try{
            parameters.put("key","value");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, EMAILS_URL, parameters, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                apiController.handleEmailResp(response);

            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error);
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();


                headers.put("Authorization","Bearer " + accessToken);

                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                300,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
    public void callGraphAPI(IAuthenticationResult authenticationResult) {

        final String accessToken = authenticationResult.getAccessToken();

        IGraphServiceClient graphClient =
                GraphServiceClient
                        .builder()
                        .authenticationProvider(new IAuthenticationProvider() {
                            @Override
                            public void authenticateRequest(IHttpRequest request) {
                                request.addHeader("Authorization", "Bearer " + accessToken);}
                        })
                        .buildClient();
        graphClient
                .me()
                .drive()
                .buildRequest()
                .get(new ICallback<Drive>() {
                    @Override
                    public void success(final Drive drive) {

                        apiController.displayGraphRes(drive.getRawObject());
                    }

                    @Override
                    public void failure(ClientException ex) {
                        apiController.displayError(ex);
                    }
                });
    }
    public void addCalls(String phoneNumber, String duration,String subject,String description,String contactId){
        RequestQueue queue= Volley.newRequestQueue(apiController.getAppContext());
        JSONObject body=new JSONObject();
        try{
            body.put("phonenumber",phoneNumber);
            body.put("activitytypecode","phonecall");
            body.put("scheduleddurationminutes",duration);
            body.put("actualdurationminutes",duration);
            body.put("subject",subject);
            body.put("description",description);
            body.put("directioncode",false);
            body.put("regardingobjectid_contact@odata.bind",contactId);
            JSONArray jsonArray=new JSONArray();
            JSONObject obj=new JSONObject();
            obj.put("partyid_contact@odata.bind",contactId);
            obj.put("participationtypemask",1);
            jsonArray.put(obj);
            body.put("phonecall_activity_parties",jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, MSSCALLS_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("Success API");
            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error "+error.networkResponse);


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
//
                headers.put("Host","apimd365.azure-api.net");
                headers.put("Ocp-Apim-Subscription-Key","ff217ee6bbf74c54972a77cf853a7436");
                headers.put("Ocp-Apim-Trace","true");
                return headers;
            }
        };
        jsonObjectRequest.setShouldCache(false);
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                300,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
    public void addContact(String firstname,String lastname,String phoneNumber,String email){
        RequestQueue queue= Volley.newRequestQueue(apiController.getAppContext());
//        JSONObject parameters=new JSONObject();
        JSONObject body=new JSONObject();
        try{
//            parameters.put("key","value");
            body.put("firstname",firstname);
            body.put("lastname",lastname);
            body.put("mobilephone",phoneNumber);
            body.put("emailaddress1",email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.POST, MSSCONTACTS_URL, body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }



        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Error "+error.networkResponse);


            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers=new HashMap<>();
                headers.put("Content-Type","application/json");
//                headers.put("Authorization","Bearer "+authenticationResult.getAccessToken());
//                headers.put("Accept","application/json");
//                headers.put("OData-MaxVersion","4.0");
//                headers.put("OData-Version","4.0");
//                headers.put("If-None-Match","null");
                headers.put("Host","apimd365.azure-api.net");
                headers.put("Ocp-Apim-Subscription-Key","ff217ee6bbf74c54972a77cf853a7436");
                headers.put("Ocp-Apim-Trace","true");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000*2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);
    }
}
