package com.example.calleridv2.controller;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calleridv2.MainActivity;
import com.example.calleridv2.model.APIModel;
import com.google.gson.JsonObject;
import com.microsoft.identity.client.IAccount;

import org.json.JSONObject;

public class APIController {
    private APIModel apiModel;
    private MainActivity mainActivity;
    public static String name;
    public static IAccount activeAccount;
    private AppCompatActivity appCompatActivity;
    public APIController(MainActivity mainActivity){
        this.apiModel=new APIModel(this);
        this.mainActivity=mainActivity;
        this.appCompatActivity=mainActivity;
    }
    public APIController(AppCompatActivity appCompatActivity){
        this.appCompatActivity=appCompatActivity;
    }
    //Controls the View
    //MainActivity
    public void updateUI(IAccount activeAccount){
        mainActivity.updateUI(activeAccount);
    }
    public void performOperationOnSignOut() {
        mainActivity.performOperationOnSignOut();
    }
    public Context getAppContext(){
        return mainActivity.getApplicationContext();
    }
    public void displayError(Exception e){

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.displayError(e);
            }
        });
    }
    public void displayGraphRes(JsonObject response){

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.displayGraphResult(response);
            }
        });
    }
    public void handleContactsSales(JSONObject response){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.getContactsSaleSuccess(response);

            }
        });
    }
    public void handleEmailResp(JSONObject response){
        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mainActivity.graphAPIEmailSuccess(response);

            }
        });
    }
    //AddCallScreen
    public void addCallApi(String phoneNumber, String duration,String subject,String description,String contactId){
        apiModel.addCalls(phoneNumber,duration,subject,description,contactId);
    }
    //AddContactScreen
    public void addContactApi(String firstname,String lastname,String phoneNumber,String email){
        apiModel.addContact(firstname,lastname,phoneNumber,email);
    }
    //Controls the Model
    public void loadAcc(){
        apiModel.loadAccount();

    }
    public void loadLoggedInAcc(){
        apiModel.loadLoggedInAcc();
    }
    public void signInForUser(){
        apiModel.signInUser(this.appCompatActivity);
    }
    public void signOutForUser(){
        apiModel.signOutUser();
    }
    public void getSilentToken(){
        apiModel.getAuthSilentCallback();
    }
}
