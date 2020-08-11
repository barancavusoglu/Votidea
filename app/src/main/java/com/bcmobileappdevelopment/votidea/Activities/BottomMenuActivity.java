package com.bcmobileappdevelopment.votidea.Activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.BuildConfig;
import com.bcmobileappdevelopment.votidea.Fragments.DialogFragment;
import com.bcmobileappdevelopment.votidea.HelperClass.Author;
import com.bcmobileappdevelopment.votidea.HelperClass.DialogItem;
import com.bcmobileappdevelopment.votidea.HelperClass.MasterChatObject;
import com.bcmobileappdevelopment.votidea.HelperClass.Message;
import com.bcmobileappdevelopment.votidea.Fragments.AddVoteFragment;
import com.bcmobileappdevelopment.votidea.Fragments.ChatFragment;
import com.bcmobileappdevelopment.votidea.Fragments.ExploreFragment;
import com.bcmobileappdevelopment.votidea.Fragments.HomeFragment;
import com.bcmobileappdevelopment.votidea.Fragments.ProfileFragment;
import com.bcmobileappdevelopment.votidea.Fragments.TopFragment;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.CheckNewMessagesResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetMessagesFirstTimeResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import mehdi.sakout.fancybuttons.FancyButton;

public class BottomMenuActivity extends AppCompatActivity {

    BottomBar bottomBar;
    android.support.v4.app.FragmentManager fm;
    android.support.v4.app.Fragment fragment;
    ConstraintLayout big_frame;
    android.support.v4.app.Fragment container_fragment;
    FancyButton btBack, btChat;
    ConstraintLayout home_container, profile_container, explore_container, add_vote_container, top_container;
    BroadcastReceiver myReceiver, newTokenReceiver;
    Gson gson;
    CheckNewMessagesResponse checkNewMessagesResponse;
    LoginResponse loggedUser;
    int lastIncomingMessageID;
    GetMessagesFirstTimeResponse getMessagesFirstTimeResponse;
    BasicResponse insertFirebaseIDResponse, getVersionCodeResponse;
    Boolean isChatAvailable;
    LovelyStandardDialog noInternetDialog, updateDialog, maintenenceDialog;

    @Override
    protected void onDestroy() {
        unregisterReceiver(myReceiver);
        unregisterReceiver(newTokenReceiver);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (isNetworkConnected()) {
            CheckVersionCode(false);
            NotificationManager nManager = ((NotificationManager) BottomMenuActivity.this.getSystemService(Context.NOTIFICATION_SERVICE));
            nManager.cancelAll();
        }
        else{
            noInternetDialog.show();
        }

        super.onResume();
        Log.d("tarja","activity onResume");
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void CheckVersionCode(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(BottomMenuActivity.this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetVersionCode), new Response.Listener<String>() {
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
                        SetupChatMessage();
                }
                else if(getVersionCodeResponse.getMessage().equals("Maintenance")){
                    maintenenceDialog.show();
                    Log.d("tarja",getVersionCodeResponse.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                CheckVersionCode(true);
                if (useBackup){
                    Intent mainIntent = new Intent(BottomMenuActivity.this,LoginActivity.class);
                    BottomMenuActivity.this.startActivity(mainIntent);
                    BottomMenuActivity.this.finish();
                }
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_menu);
        fm = getSupportFragmentManager();
        //fm.beginTransaction().replace(R.id.content_frame, new HomeFragment()).commit();
        Hawk.init(BottomMenuActivity.this).build();
        Hawk.put("container_count",0);
        loggedUser = Hawk.get("loggedUser");
        //big_frame = findViewById(R.id.big_content_frame);
        btBack = findViewById(R.id.btBack);
        btChat = findViewById(R.id.btChat);
        isChatAvailable = false;
        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NewMessageReceived(intent);
                //ChangeChatButtonColor();
            }
        };

        newTokenReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String newToken = intent.getStringExtra("NewToken");
                InsertFirebaseID(newToken,false);
            }
        };

        registerReceiver(myReceiver,new IntentFilter("chat"));
        registerReceiver(newTokenReceiver,new IntentFilter("NewToken"));

        btChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if (isChatAvailable){
                    OpenChat();
                    btChat.setVisibility(View.GONE);
                    HideBottomBar();
                //}
            }
        });
        RegisterFirebaseInstance();

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1){
                    btBack.setVisibility(View.GONE);
                    //if (bottomBar.getCurrentTabPosition() == 0){
                    //    btChat.setVisibility(View.VISIBLE);
                    //}
                    //if (bottomBar.getVisibility() == View.GONE)
                    //    bottomBar.setVisibility(View.VISIBLE);
                }

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(),0);
            }
        });
        //btBack.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        home_container = findViewById(R.id.home_container);
        //        profile_container = findViewById(R.id.profile_container);
        //        if (home_container != null && home_container.getChildCount() > 0 ){
        //            Log.d("tarja","home_container child"+home_container.getChildCount());
        //            home_container.removeViewAt(home_container.getChildCount()-1);
        //            if (home_container.getChildCount() == 0)
        //                btBack.setVisibility(View.GONE);
        //        }
        //        if(profile_container != null && profile_container.getChildCount() > 0 ){
        //            profile_container.removeViewAt(profile_container.getChildCount()-1);
        //            if (profile_container.getChildCount() == 0)
        //                btBack.setVisibility(View.GONE);
        //        }
        //    }
        //});
        //btBack.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        int count = Hawk.get("container_count");
        //        if (count != 0)
        //            Hawk.put("container_count",--count);
        //        container_fragment = getSupportFragmentManager().findFragmentByTag("home_container");
        //        if (container_fragment != null){
        //            fm.beginTransaction().detach(container_fragment).commit();
        //        }
        //        if (count == 0)
        //            //if (getSupportFragmentManager().findFragmentByTag("home_fragment").isVisible())
        //            btBack.setVisibility(View.GONE);
//
        //    }
        //});

        bottomBar = findViewById(R.id.bottom_bar);
        //bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
        //    @Override
        //    public void onTabSelected(int tabId) {
        //        switch (tabId){
        //            case R.id.tab_home:
        //                fm.beginTransaction().replace(R.id.content_frame, new HomeFragment(),"fragment_home").commit();
        //                break;
        //            case R.id.tab_vote:
        //                fm.beginTransaction().replace(R.id.content_frame, new VoteFragment(),"fragment_vote").commit();
        //                break;
        //            case R.id.tab_add:
        //                fm.beginTransaction().replace(R.id.content_frame, new AddVoteFragment(),"fragment_add_vote").commit();
        //                break;
        //            case R.id.tab_profile:
        //                fm.beginTransaction().replace(R.id.content_frame, new ProfileFragment(),"fragment_profile").commit();
        //                break;
        //            case R.id.tab_settings:
        //                fm.beginTransaction().replace(R.id.content_frame, new SettingsFragment(),"fragment_settings").commit();
        //                break;
        //        }
        //    }
        //});
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {

                while(getSupportFragmentManager().getBackStackEntryCount() > 0){
                    getSupportFragmentManager().popBackStackImmediate();
                }

                //home_container = findViewById(R.id.home_container);
                //explore_container = findViewById(R.id.explore_container);
                //if (home_container!= null && home_container.getChildCount() > 0)
                //    home_container.removeAllViews();
                //if (explore_container != null && explore_container.getChildCount() > 0)
                //    explore_container.removeAllViews();//TODO neden yaptık anlamadım

                //HomeFragment hm = (HomeFragment) getSupportFragmentManager().findFragmentByTag("");
                //hm.RefreshRecyclerView();

                //btBack.callOnClick();
                //Hawk.put("container_count",0);
                btBack.setVisibility(View.GONE);
                btChat.setVisibility(View.GONE);

                if(fm.findFragmentByTag("explore_fragment") != null){
                    fm.beginTransaction().hide(fm.findFragmentByTag("explore_fragment")).commit();
                }
                if(fm.findFragmentByTag("profile_fragment") != null){
                    fm.beginTransaction().detach(fm.findFragmentByTag("profile_fragment")).commit();
                }
                if(fm.findFragmentByTag("home_fragment") != null){
                    fm.beginTransaction().hide(fm.findFragmentByTag("home_fragment")).commit();
                }
                if(fm.findFragmentByTag("other_profile_fragment") != null){
                    fm.beginTransaction().detach(fm.findFragmentByTag("other_profile_fragment")).commit();
                }
                if(fm.findFragmentByTag("add_vote_fragment") != null){
                    fm.beginTransaction().detach(fm.findFragmentByTag("add_vote_fragment")).commit();
                }
                if(fm.findFragmentByTag("top_fragment") != null){
                    fm.beginTransaction().detach(fm.findFragmentByTag("top_fragment")).commit();
                }

                switch (tabId){
                    case R.id.tab_home:
                        btChat.setVisibility(View.VISIBLE);

                        if (fm.findFragmentByTag("home_fragment") != null){
                            fm.beginTransaction().show(fm.findFragmentByTag("home_fragment")).commit();
                        }
                        else
                            fm.beginTransaction().add(R.id.content_frame,new HomeFragment(),"home_fragment").commit();
                        break;
                    case R.id.tab_explore:
                        if(fm.findFragmentByTag("explore_fragment") != null){
                            fm.beginTransaction().show(fm.findFragmentByTag("explore_fragment")).commit();
                        }
                        else
                            fm.beginTransaction().add(R.id.content_frame, new ExploreFragment(),"explore_fragment").commit();
                        break;
                    case R.id.tab_add:
                        if(fm.findFragmentByTag("add_vote_fragment") != null){
                            fm.beginTransaction().remove(fm.findFragmentByTag("add_vote_fragment")).commit();
                        }
                        fm.beginTransaction().add(R.id.content_frame, new AddVoteFragment(),"add_vote_fragment").commit();
                        break;
                    case R.id.tab_profile:
                        if (fm.findFragmentByTag("profile_fragment") != null){
                            fm.beginTransaction().remove(fm.findFragmentByTag("profile_fragment")).commit();
                        }
                        fm.beginTransaction().add(R.id.content_frame,new ProfileFragment(),"profile_fragment").commit();
                        break;
                    case R.id.tab_top:
                        if(fm.findFragmentByTag("top_fragment") != null){
                            fm.beginTransaction().remove(fm.findFragmentByTag("top_fragment")).commit();
                        }
                        fm.beginTransaction().add(R.id.content_frame, new TopFragment(),"top_fragment").commit();
                        break;
                }
                //fragment.setRetainInstance(true);
                //fm.beginTransaction().replace(R.id.content_frame,fragment,tag).commit();
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(int tabId) {
                if (tabId == R.id.tab_home)
                {
                    home_container = findViewById(R.id.home_container);
                    if (home_container.getChildCount() > 0){
                        btBack.setVisibility(View.GONE);
                        home_container.removeAllViews();
                    }
                    else {
                        HomeFragment hm = (HomeFragment) getSupportFragmentManager().findFragmentByTag("home_fragment");
                        hm.GoTop();
                    }
                }

                else if(tabId == R.id.tab_profile)
                {
                    profile_container = findViewById(R.id.profile_container);
                    if (profile_container.getChildCount() > 0){
                        btBack.setVisibility(View.GONE);
                        profile_container.removeAllViews();
                    }
                    else {
                        ProfileFragment pf = (ProfileFragment) getSupportFragmentManager().findFragmentByTag("profile_fragment");
                        pf.GoTop();
                    }
                }

                else if(tabId == R.id.tab_explore){
                    explore_container = findViewById(R.id.explore_container);
                    if (explore_container.getChildCount() > 0){
                        btBack.setVisibility(View.GONE);
                        explore_container.removeAllViews();
                    }
                    else {
                        ExploreFragment ef = (ExploreFragment) getSupportFragmentManager().findFragmentByTag("explore_fragment");
                        ef.GoTop();
                    }
                }

                else if(tabId == R.id.tab_top){
                    top_container = findViewById(R.id.top_container);
                    if (top_container.getChildCount() > 0){
                        btBack.setVisibility(View.GONE);
                        top_container.removeAllViews();
                    }
                    else {
                        TopFragment tf = (TopFragment) getSupportFragmentManager().findFragmentByTag("top_fragment");
                        tf.GoTop();
                    }
                }

            }
        });

        maintenenceDialog = new LovelyStandardDialog(BottomMenuActivity.this);
        maintenenceDialog.setTopColorRes(R.color.colorPrimary);
        maintenenceDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        maintenenceDialog.setIcon(R.drawable.ic_repair_white);
        maintenenceDialog.setTitle(getResources().getString(R.string.under_maintenance_title));
        maintenenceDialog.setMessage(getResources().getString(R.string.under_maintenance_message));
        maintenenceDialog.setCancelable(false);
        maintenenceDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuActivity.this.finish();
            }
        });

        noInternetDialog = new LovelyStandardDialog(BottomMenuActivity.this);
        noInternetDialog.setTopColorRes(R.color.colorPrimary);
        noInternetDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        noInternetDialog.setIcon(R.drawable.ic_wifi_off_white);
        noInternetDialog.setTitle(getResources().getString(R.string.no_internet_title));
        noInternetDialog.setMessage(getResources().getString(R.string.no_internet_message));
        noInternetDialog.setCancelable(false);
        noInternetDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuActivity.this.finish();
            }
        });

        updateDialog = new LovelyStandardDialog(BottomMenuActivity.this);
        updateDialog.setTopColorRes(R.color.colorPrimary);
        updateDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        updateDialog.setIcon(R.drawable.ic_new_release_white);
        updateDialog.setTitle(getResources().getString(R.string.update_app_title));
        updateDialog.setMessage(getResources().getString(R.string.update_app_message));
        updateDialog.setCancelable(false);
        updateDialog.setPositiveButton(getResources().getString(R.string.ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuActivity.this.finish();
            }
        });
    }

    private void RegisterFirebaseInstance() {
        if (Hawk.contains("FirebaseInstanceID"))
        {
            if (!FirebaseInstanceId.getInstance().getToken().equals(Hawk.get("FirebaseInstanceID"))){
                InsertFirebaseID(FirebaseInstanceId.getInstance().getToken(),false);
                Log.d("tarja","Inserting new FirebaseInstanceID");
            }
        }
        else {
            InsertFirebaseID(FirebaseInstanceId.getInstance().getToken(),false);
            Log.d("tarja","Inserting new FirebaseInstanceID");
        }
    }

    private void InsertFirebaseID(final String token, final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_InsertFirebaseInstanceID), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                insertFirebaseIDResponse = gson.fromJson(response, myType);
                if (insertFirebaseIDResponse.isIsSuccess()) {
                    Log.d("tarja","Inserting new FirebaseInstanceID SUCCESS");
                    Hawk.put("FirebaseInstanceID",FirebaseInstanceId.getInstance().getToken());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                InsertFirebaseID(token,true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("token", loggedUser.getToken());
                params.put("firebaseInstanceID", token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2 && resultCode == RESULT_OK) {
            Log.d("Tarja","wtf");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void ChangeChatButtonColor(){
        btChat.setIconColor(getResources().getColor(R.color.pink));
    }

    public void HideBottomBar(){
        bottomBar.setVisibility(View.GONE);
    }

    public void Logout(){
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
        Intent myIntent = new Intent(BottomMenuActivity.this, LoginActivity.class);
        BottomMenuActivity.this.startActivity(myIntent);
        this.finish();
    }

    private void OpenChat(){
        BottomBar host = findViewById(R.id.bottom_bar);
        if (host.getCurrentTabPosition() == 0){
            home_container = findViewById(R.id.home_container);

            ChatFragment chatFragment = new ChatFragment();
            //FancyButton btBack = getActivity().findViewById(R.id.btBack);
            btBack.setVisibility(View.VISIBLE);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.home_container, chatFragment,"chat_fragment")
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        BottomBar host = findViewById(R.id.bottom_bar);
        //if(Hawk.get("IsOtherProfileFragmentActive",false)){
        //    fm.beginTransaction().show(fm.findFragmentByTag("home_fragment")).commit();
        //    fm.beginTransaction().remove(fm.findFragmentByTag("other_profile_fragment")).commit();
        //    Hawk.put("IsOtherProfileFragmentActive",false);
        //    Log.d("tarja","giriyorvalla");
        //}
        //else if(Hawk.get("IsCommentsFragmentActive",false)){
        //}
        home_container = findViewById(R.id.home_container);
        profile_container = findViewById(R.id.profile_container);
        explore_container = findViewById(R.id.explore_container);
        add_vote_container = findViewById(R.id.add_vote_container);
        top_container = findViewById(R.id.top_container);

        if ((home_container != null && home_container.getChildCount() > 0)|| (profile_container != null && profile_container.getChildCount() > 0) || (explore_container != null && explore_container
                .getChildCount() >0) || (add_vote_container!= null && add_vote_container.getChildCount() > 0) || (top_container != null && top_container.getChildCount() > 0 )){
            btBack.callOnClick();
        }

        //if(!Hawk.get("container_count").equals(0) ){
        //    btBack.callOnClick();
        //}
        else if (host.getCurrentTabPosition() == 0){
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        else
            host.selectTabAtPosition(0,true);
        //super.onBackPressed();
    }

    private void SetupChatMessage(){
        isChatAvailable = false;
        //btChat.setVisibility(View.GONE);
        if (!Hawk.contains("MasterChatObjects"))
        {
            ArrayList<MasterChatObject> masterChatObjects = new ArrayList<>();
            Hawk.put("MasterChatObjects",masterChatObjects);
        }
        if (!Hawk.contains("LastIncomingMessageID")){
            Hawk.put("LastIncomingMessageID",0);
            GetMessagesFirstTime(false);
            Log.d("tarja","GetMessagesFirstTime");
        }
        else{
            lastIncomingMessageID = Hawk.get("LastIncomingMessageID");
            CheckNewMessages(false);
            Log.d("tarja","CheckNewMessages");
        }
    }

    private void GetMessagesFirstTime(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetMessagesFirstTime), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetMessagesFirstTimeResponse>() {
                }.getType();
                getMessagesFirstTimeResponse = gson.fromJson(response, myType);
                if (getMessagesFirstTimeResponse.isIsSuccess()){
                    Hawk.put("LastIncomingMessageID",getMessagesFirstTimeResponse.getMaxID());
                    if(getMessagesFirstTimeResponse.getDialogs().size()>0){
                        ArrayList<MasterChatObject> masterChatObjects = new ArrayList<>();
                        MasterChatObject masterChatObject;
                        //for(int i =0;i<checkNewMessagesResponse.getDialogs().size();i++){
                        //
                        //}
                        for (GetMessagesFirstTimeResponse.DialogsBean item: getMessagesFirstTimeResponse.getDialogs()) {
                            masterChatObject = new MasterChatObject();
                            Message lastMessage = new Message();
                            Author author = new Author();
                            List<Author> authorList = new ArrayList<>();
                            ArrayList<Message> messages = new ArrayList<>();
                            author.setName(item.getFromUsername());
                            author.setId(item.getFromUserID());
                            author.setAvatar(item.getPhoto());
                            authorList.add(author);
                            lastMessage.setAuthor(author);
                            String comment = item.getMessages().get(item.getMessages().size()-1).getText();
                            byte[] data = Base64.decode(comment, Base64.DEFAULT);
                            try {
                                String emojiMessage = new String(data, "UTF-8");
                                lastMessage.setText(emojiMessage);
                            }catch (Exception e){

                            }
                            lastMessage.setId(item.getMessages().get(item.getMessages().size()-1).getID());

                            try{
                                //lastMessage.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(item.getMessages().get(item.getMessages().size()-1).getDate()));
                                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                Date newDate = dateFormat.parse(item.getMessages().get(item.getMessages().size()-1).getDate());
                                lastMessage.setDate(newDate);

                                //Log.d("tarja","Date= "+ lastMessage.getCreatedAt().toString());
                            }
                            catch (Exception e){
                                lastMessage.setDate(new Date());
                                Log.d("tarja","Hata="+e.getMessage());
                            }
                            masterChatObject.getDialogItem().setUnreadCount(item.getUnreadCount());
                            masterChatObject.getDialogItem().setLastMessage(lastMessage);
                            masterChatObject.getDialogItem().setPhoto(item.getPhoto());
                            masterChatObject.getDialogItem().setFromUsername(item.getFromUsername());
                            masterChatObject.getDialogItem().setID(item.getID());
                            masterChatObject.getDialogItem().setFromUserID(item.getFromUserID());
                            masterChatObject.getDialogItem().setUserList(authorList);
                            for (GetMessagesFirstTimeResponse.DialogsBean.MessagesBean messageItem: item.getMessages()){
                                Message messageToAdd = new Message();
                                Author messageAuthor = new Author();
                                messageAuthor.setId(String.valueOf(messageItem.getFromUserID()));
                                messageAuthor.setAvatar(author.getAvatar());
                                messageAuthor.setName("");
                                messageToAdd.setAuthor(messageAuthor);
                                try{
                                    //messageToAdd.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(messageItem.getDate()));
                                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                    Date newDate = dateFormat.parse(messageItem.getDate());
                                    messageToAdd.setDate(newDate);
                                }catch (Exception e){
                                    messageToAdd.setDate(new Date());
                                }
                                messageToAdd.setId(messageItem.getID());
                                comment = messageItem.getText();
                                data = Base64.decode(comment, Base64.DEFAULT);
                                try {
                                    String emojiMessage = new String(data, "UTF-8");
                                    messageToAdd.setText(emojiMessage);
                                }catch (Exception e){

                                }
                                messages.add(messageToAdd);
                            }
                            masterChatObject.setMessages(messages);
                            masterChatObjects.add(masterChatObject);
                        }
                        Hawk.put("MasterChatObjects",masterChatObjects);
                    }
                }
                btChat.setVisibility(View.VISIBLE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetMessagesFirstTime(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void CheckNewMessages(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_CheckNewMessages), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<CheckNewMessagesResponse>() {
                }.getType();
                checkNewMessagesResponse = gson.fromJson(response, myType);
                if (checkNewMessagesResponse.isIsSuccess()) {
                    if(checkNewMessagesResponse.getTotalUnreadCount() > 0){
                        Hawk.put("LastIncomingMessageID",checkNewMessagesResponse.getMaxID());
                        ArrayList<MasterChatObject> masterChatObjects = Hawk.get("MasterChatObjects");
                        Boolean dialogExists;

                        for (CheckNewMessagesResponse.DialogsBean responseItem: checkNewMessagesResponse.getDialogs()) {
                            dialogExists = false;
                            for (MasterChatObject localItem: masterChatObjects)
                            {
                                if (localItem.getDialogItem().getId().equals("-1")){
                                    if(localItem.getDialogItem().getFromUsername().equals(responseItem.getFromUsername())){
                                        localItem.getDialogItem().setID(responseItem.getID());

                                        //List<Author> authorListAdd = new ArrayList<>();
                                        //Author authorAdd = new Author();
                                        //authorAdd.setName(responseItem.getFromUsername());
                                        //authorAdd.setAvatar(responseItem.getPhoto());
                                        //authorAdd.setId(responseItem.getFromUserID());
                                        //authorListAdd.add(authorAdd);
                                        //localItem.getDialogItem().setUserList(authorListAdd);
                                    }
                                }
                                if(localItem.getDialogItem().getId().equals(responseItem.getID())){
                                    dialogExists = true;
                                    ArrayList<Message> localMessages = localItem.getMessages();
                                    Message messageToAdd = new Message();
                                    for (CheckNewMessagesResponse.DialogsBean.MessagesBean messageItem: responseItem.getMessages()){
                                        messageToAdd = new Message();
                                        Author messageAuthor = new Author();
                                        messageAuthor.setId(String.valueOf(messageItem.getFromUserID()));
                                        messageAuthor.setAvatar(responseItem.getPhoto());
                                        messageAuthor.setName("");
                                        messageToAdd.setAuthor(messageAuthor);
                                        try{
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                            //dateFormat.setTimeZone(TimeZone.getDefault());
                                            Date newDate = dateFormat.parse(messageItem.getDate());
                                            messageToAdd.setDate(newDate);
                                            //dateFormat.setTimeZone(TimeZone.getDefault());
                                            //newDate = dateFormat.parse(newDate.toString());
                                            //messageToAdd.setDate(newDate);

                                            //messageToAdd.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(messageItem.getDate()));
                                        }catch (Exception e){
                                            messageToAdd.setDate(new Date());
                                        }
                                        messageToAdd.setId(messageItem.getID());

                                        String comment = messageItem.getText();
                                        byte[] data = Base64.decode(comment, Base64.DEFAULT);
                                        try {
                                            String emojiMessage = new String(data, "UTF-8");
                                            messageToAdd.setText(emojiMessage);
                                        }catch (Exception e){

                                        }
                                        //messageToAdd.setText(messageItem.getText());
                                        localMessages.add(messageToAdd);
                                    }
                                    localItem.setMessages(localMessages);
                                    localItem.getDialogItem().setLastMessage(messageToAdd);
                                    localItem.getDialogItem().setUnreadCount(localItem.getDialogItem().getUnreadCount() + responseItem.getUnreadCount());
                                }
                            }
                            if (!dialogExists){
                                MasterChatObject masterChatObjectAdd = new MasterChatObject();
                                DialogItem dialogItemAdd = new DialogItem();
                                dialogItemAdd.setUnreadCount(responseItem.getUnreadCount());
                                dialogItemAdd.setPhoto(responseItem.getPhoto());
                                dialogItemAdd.setID(responseItem.getID());
                                dialogItemAdd.setFromUserID(responseItem.getFromUserID());
                                dialogItemAdd.setFromUsername(responseItem.getFromUsername());
                                List<Author> authorListAdd = new ArrayList<>();
                                Author authorAdd = new Author();
                                authorAdd.setName(responseItem.getFromUsername());
                                authorAdd.setAvatar(responseItem.getPhoto());
                                authorAdd.setId(responseItem.getFromUserID());
                                authorListAdd.add(authorAdd);
                                dialogItemAdd.setUserList(authorListAdd);
                                Message messageToAdd = new Message();
                                ArrayList<Message> messageListAdd = new ArrayList<>();
                                for (CheckNewMessagesResponse.DialogsBean.MessagesBean messageItemAdd: responseItem.getMessages()){
                                    messageToAdd = new Message();
                                    messageToAdd.setAuthor(authorAdd);
                                    messageToAdd.setId(messageItemAdd.getID());
                                    try{
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                                        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                                        //dateFormat.setTimeZone(TimeZone.getDefault());
                                        Date newDate = dateFormat.parse(messageItemAdd.getDate());
                                        messageToAdd.setDate(newDate);
                                    }catch (Exception e){
                                        messageToAdd.setDate(new Date());
                                    }

                                    String comment = messageItemAdd.getText();
                                    byte[] data = Base64.decode(comment, Base64.DEFAULT);
                                    try {
                                        String emojiMessage = new String(data, "UTF-8");
                                        messageToAdd.setText(emojiMessage);
                                    }catch (Exception e){

                                    }

                                    //messageToAdd.setText(messageItemAdd.getText());
                                    messageListAdd.add(messageToAdd);
                                }
                                dialogItemAdd.setLastMessage(messageToAdd);
                                masterChatObjectAdd.setDialogItem(dialogItemAdd);
                                masterChatObjectAdd.setMessages(messageListAdd);
                                masterChatObjects.add(masterChatObjectAdd);
                                Log.d("tarja","yeni dialog eklendi");
                            }
                        }
                        Hawk.put("MasterChatObjects",masterChatObjects);
                    }
                }
                isChatAvailable = true;
                //BottomBar host = findViewById(R.id.bottom_bar);
                //if (host.getCurrentTabPosition() == 0){
                //    btChat.setVisibility(View.VISIBLE);
                //}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                CheckNewMessages(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("lastIncomingMessageID",String.valueOf(lastIncomingMessageID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void NewMessageReceived(Intent intent) {
        String messageID = intent.getStringExtra("messageID");
        String senderUserProfilePic = intent.getStringExtra("senderUserProfilePic");
        String senderUserID = intent.getStringExtra("senderUserID");
        String content = intent.getStringExtra("content");
        String senderUserName = intent.getStringExtra("senderUserName");
        String dialogID = intent.getStringExtra("dialogID");
        Boolean dialogExists = false;

        ArrayList<MasterChatObject> masterChatObjects = Hawk.get("MasterChatObjects");
        for (MasterChatObject masterChatObject: masterChatObjects){
            if (masterChatObject.getDialogItem().getId().equals(dialogID)){
                dialogExists = true;
                ArrayList<Message> localMessages = masterChatObject.getMessages();
                Message messageToAdd = new Message();

                messageToAdd.setAuthor(masterChatObject.getDialogItem().getUserList().get(0));
                try{
                    messageToAdd.setDate(new Date());
                }catch (Exception e){
                    messageToAdd.setDate(new Date());
                }
                messageToAdd.setId(messageID);
                byte[] data = Base64.decode(content, Base64.DEFAULT);
                try {
                    String emojiMessage = new String(data, "UTF-8");
                    messageToAdd.setText(emojiMessage);
                }catch (Exception e){

                }
                localMessages.add(messageToAdd);
                masterChatObject.setMessages(localMessages);
                masterChatObject.getDialogItem().setLastMessage(messageToAdd);

                DialogFragment df =(DialogFragment) getSupportFragmentManager().findFragmentByTag("dialog_fragment");
                if (df == null){
                    masterChatObject.getDialogItem().setUnreadCount(masterChatObject.getDialogItem().getUnreadCount() + 1);
                }
                else if (!df.GetActiveFromUserID().equals(senderUserID)){
                    masterChatObject.getDialogItem().setUnreadCount(masterChatObject.getDialogItem().getUnreadCount() + 1);
                }
            }
        }
        if (!dialogExists){
            MasterChatObject masterChatObjectAdd = new MasterChatObject();
            DialogItem dialogItemAdd = new DialogItem();
            dialogItemAdd.setUnreadCount(1);
            dialogItemAdd.setPhoto(senderUserProfilePic);
            dialogItemAdd.setID(dialogID);
            dialogItemAdd.setFromUserID(senderUserID);
            dialogItemAdd.setFromUsername(senderUserName);
            List<Author> authorListAdd = new ArrayList<>();
            Author authorAdd = new Author();
            authorAdd.setName(senderUserName);
            authorAdd.setAvatar(senderUserProfilePic);
            authorAdd.setId(senderUserID);
            authorListAdd.add(authorAdd);
            dialogItemAdd.setUserList(authorListAdd);
            Message messageToAdd = new Message();
            ArrayList<Message> messageListAdd = new ArrayList<>();

            messageToAdd = new Message();
            messageToAdd.setAuthor(authorAdd);
            messageToAdd.setId(messageID);
            try{
                messageToAdd.setDate(new Date());
            }catch (Exception e){
                messageToAdd.setDate(new Date());
            }
            byte[] data = Base64.decode(content, Base64.DEFAULT);
            try {
                String emojiMessage = new String(data, "UTF-8");
                messageToAdd.setText(emojiMessage);
            }catch (Exception e){

            }
            messageListAdd.add(messageToAdd);

            dialogItemAdd.setLastMessage(messageToAdd);
            masterChatObjectAdd.setDialogItem(dialogItemAdd);
            masterChatObjectAdd.setMessages(messageListAdd);
            masterChatObjects.add(masterChatObjectAdd);
            Log.d("tarja","yeni dialog eklendi");
        }
        Hawk.put("MasterChatObjects",masterChatObjects);
        Hawk.put("LastIncomingMessageID",Integer.parseInt(messageID));
    }
}