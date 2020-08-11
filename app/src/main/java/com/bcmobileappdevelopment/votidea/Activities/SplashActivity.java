package com.bcmobileappdevelopment.votidea.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.BuildConfig;
import com.bcmobileappdevelopment.votidea.Fragments.SettingsFragment;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

public class SplashActivity extends AppCompatActivity {

    LoginResponse loginResponse, loggedUser;
    Gson gson;
    FirebaseAuth mAuth;
    BasicResponse getVersionCodeResponse;
    LovelyStandardDialog noInternetDialog, updateDialog, maintenanceDialog, bannedDialog;

    private void Initialize() {
        Hawk.init(SplashActivity.this).build();
        loggedUser = new LoginResponse();
        mAuth = FirebaseAuth.getInstance();

        maintenanceDialog = new LovelyStandardDialog(SplashActivity.this);
        maintenanceDialog.setTopColorRes(R.color.colorPrimary);
        maintenanceDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        maintenanceDialog.setIcon(R.drawable.ic_repair_white);
        maintenanceDialog.setTitle(getResources().getString(R.string.under_maintenance_title));
        maintenanceDialog.setMessage(getResources().getString(R.string.under_maintenance_message));
        maintenanceDialog.setCancelable(false);
        maintenanceDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.this.finish();
            }
        });

        noInternetDialog = new LovelyStandardDialog(SplashActivity.this);
        noInternetDialog.setTopColorRes(R.color.colorPrimary);
        noInternetDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        noInternetDialog.setIcon(R.drawable.ic_wifi_off_white);
        noInternetDialog.setTitle(getResources().getString(R.string.no_internet_title));
        noInternetDialog.setMessage(getResources().getString(R.string.no_internet_message));
        noInternetDialog.setCancelable(false);
        noInternetDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.this.finish();
            }
        });

        updateDialog = new LovelyStandardDialog(SplashActivity.this);
        updateDialog.setTopColorRes(R.color.colorPrimary);
        updateDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        updateDialog.setIcon(R.drawable.ic_new_release_white);
        updateDialog.setTitle(getResources().getString(R.string.update_app_title));
        updateDialog.setMessage(getResources().getString(R.string.update_app_message));
        updateDialog.setCancelable(false);
        updateDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.this.finish();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.bcmobileappdevelopment.votidea")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bcmobileappdevelopment.votidea")));
                }
            }
        });

        bannedDialog = new LovelyStandardDialog(SplashActivity.this);
        bannedDialog.setTopColorRes(R.color.real_Black);
        bannedDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        bannedDialog.setIcon(R.drawable.ic_banned_white);
        bannedDialog.setTitle(getResources().getString(R.string.user_banned_title));
        bannedDialog.setMessage(getResources().getString(R.string.user_banned_message));
        bannedDialog.setCancelable(false);
        bannedDialog.setPositiveButton(getResources().getString(R.string.banned_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FirebaseInstanceId.getInstance().deleteInstanceId();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                Hawk.deleteAll();
                Intent myIntent = new Intent(SplashActivity.this, LoginActivity.class);
                SplashActivity.this.startActivity(myIntent);
                SplashActivity.this.finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Initialize();

        if (isNetworkConnected())
            CheckVersionCode(false);
        else{
            noInternetDialog.show();
        }
        // baran token          FmT0nxWPfQ+p4TyzTJZbo4yGQ9hfZzD64AqKpX/T5M4=
        // şıpıdık token        FmT0nxWPfQ+p4TyzTJZbo0qFArE2fA+IqZ1bAqkf/QQ=
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void CheckVersionCode(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(SplashActivity.this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        Log.d("tarja","İşte use backup"+useBackup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL + getResources().getString(R.string.ws_GetVersionCode), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                getVersionCodeResponse = gson.fromJson(response, myType);

                if (getVersionCodeResponse.isIsSuccess()){
                    Log.d("tarja",getVersionCodeResponse.getMessage());
                    if (Integer.parseInt(getVersionCodeResponse.getMessage()) > BuildConfig.VERSION_CODE){
                        updateDialog.show();
                    }
                    else
                        StartAutoLogin();
                }
                else if(getVersionCodeResponse.getMessage().equals("Maintenance")){
                    maintenanceDialog.show();
                    Log.d("tarja",getVersionCodeResponse.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                    CheckVersionCode(true);
                if (useBackup)
                    maintenanceDialog.show();
                //Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
                //SplashActivity.this.startActivity(mainIntent);
                //SplashActivity.this.finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void StartAutoLogin(){
        if (Hawk.contains("loggedUser")){
            loggedUser = Hawk.get("loggedUser");
            if (loggedUser.getLoginView().getID() != 1)
                AutoLogin(false);
            else {
                Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }
        else{
            //Hawk.put("token","FmT0nxWPfQ+p4TyzTJZbo0qFArE2fA+IqZ1bAqkf/QQ=");
            //Hawk.put("LoggedUserID",4);
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    /* Create an Intent that will start the Menu-Activity. */
                    Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }, 0);
        }
    }

    private void AutoLogin(final Boolean useBackup)
    {
        RequestQueue mRequestQueue = Volley.newRequestQueue(SplashActivity.this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_Login), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<LoginResponse>() {
                }.getType();
                loginResponse = gson.fromJson(response, myType);

                if (loginResponse.isIsSuccess()){
                    Log.d("tarja","geldi girdik aq");
                    FirebaseLogin(loginResponse.getLoginView().getEmail(),loginResponse.getToken());
                    //FirebaseRegister(loginResponse.getLoginView().getEmail(),loginResponse.getToken());
                    Hawk.put("loggedUser",loginResponse);
                    Intent myIntent = new Intent(SplashActivity.this, BottomMenuActivity.class);
                    SplashActivity.this.startActivity(myIntent);
                    SplashActivity.this.finish();
                    Log.d("tarja","geldi girdik aq");
                }
                else if(loginResponse.getMessage().equals("user_banned")){
                    bannedDialog.show();
                }
                else {
                    Log.d("tarja","patlak webservice");
                    Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
                    SplashActivity.this.startActivity(mainIntent);
                    SplashActivity.this.finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                    AutoLogin(true);
                if (useBackup)
                    maintenanceDialog.show();
                //Log.d("tarja","splash login error");
                //Intent mainIntent = new Intent(SplashActivity.this,LoginActivity.class);
                //SplashActivity.this.startActivity(mainIntent);
                //SplashActivity.this.finish();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("accountType", loggedUser.getLoginView().getAccountType());
                params.put("username", loggedUser.getLoginView().getUsername());
                params.put("accountID", loggedUser.getLoginView().getAccountID());
                params.put("email", "");
                params.put("nameSurname", "");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void FirebaseLogin(String email, String pass){
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tarja", "login:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tarja", "login:failure", task.getException());
                        }
                    }
                });
    }

    private void FirebaseRegister(String email, String pass){
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //Log.d("tarja", "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("tarja", "createUserWithEmail:failure", task.getException());
                        }

                    }
                });
    }

}
