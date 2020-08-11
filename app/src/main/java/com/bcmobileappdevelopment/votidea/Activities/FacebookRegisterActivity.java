package com.bcmobileappdevelopment.votidea.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.FacebookLoginResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetCityResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetCountryAndFlagCodeResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetCountryInfo2Response;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetCountryInfoResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mehdi.sakout.fancybuttons.FancyButton;

public class FacebookRegisterActivity extends AppCompatActivity {

    FacebookLoginResponse facebookLoginResponse;
    TextView tvNameSurname, tvEmail, tvLocation;
    ImageView ivProfilePic,ivBack;
    RequestOptions requestOptionsProfile;
    String accessToken, location, flagCode, url, countryCode, city, country, alpha3Code;
    Gson gson;
    GetCountryInfoResponse getCountryInfoResponse;
    GetCountryInfo2Response getCountryInfo2Response;
    Boolean countryProcessCompleted;
    FancyButton btRegister;
    LoginResponse loginResponse;
    MaterialEditText etUsername;
    FancyButton btBack;
    FirebaseAuth mAuth;
    MultiTransformation multi;
    Dialog loadingDialog;
    GetCountryAndFlagCodeResponse getCountryAndFlagCodeResponse;
    GetCityResponse getCityResponse;
    BasicResponse getCountryNameResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Hawk.init(FacebookRegisterActivity.this).build();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook_register);
        GetCountryName(false);
        Initialize();
        InitializeListeners();
    }

    private void InitializeListeners() {
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etUsername.length() < 5 || etUsername.length() > 30){
                    Toasty.error(FacebookRegisterActivity.this, getResources().getString(R.string.please_enter_between_5_18), Toast.LENGTH_LONG, true).show();
                    etUsername.setError(getResources().getString(R.string.please_check));
                }
                else if(!isAlpha(etUsername.getText().toString()))
                {
                    etUsername.setError(getResources().getString(R.string.please_check));
                    Toasty.error(FacebookRegisterActivity.this, getResources().getString(R.string.please_enter_only_english), Toast.LENGTH_LONG, true).show();
                }
                else {
                    loadingDialog.show();
                    FacebookRegister(false);
                }
            }
        });
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(FacebookRegisterActivity.this, LoginActivity.class);
                FacebookRegisterActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.slide_right_from_left, R.anim.slide_right);
                finish();
            }
        });
    }

    private void GetCountryName(final boolean useBackup){
        country = "";
        city = "";
        alpha3Code = "";
        alpha3Code = Locale.getDefault().getISO3Country();
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetCountryName), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                getCountryNameResponse = gson.fromJson(response, myType);
                if (getCountryNameResponse.isIsSuccess()) {
                    country = getCountryNameResponse.getMessage();
                    if (alpha3Code.equals("TUR"))
                        alpha3Code = "turk";
                    tvLocation.setText(country);
                    tvLocation.setVisibility(View.VISIBLE);
                }
                GetCity();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                    GetCountryName(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("alpha3code", alpha3Code);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void GetCity() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        url = getResources().getString(R.string.get_city_url)+"&fields=region_name,city";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetCityResponse>() {
                }.getType();
                getCityResponse = gson.fromJson(response, myType);
                if (getCityResponse != null) {
                    if (getCityResponse.getCity() != null){
                        city = getCityResponse.getCity();
                        tvLocation.setText(city+ ", "+country);
                    }
                    else if(getCityResponse.getRegion_name() != null){
                        city = getCityResponse.getRegion_name();
                        tvLocation.setText(city+ ", "+country);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {

        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public boolean isAlpha(String name) {
        return name.matches("[a-zA-Z0-9]+");
    }

    private void FacebookRegister(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_FacebookRegister), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<LoginResponse>() {
                }.getType();
                loginResponse = gson.fromJson(response, myType);
                loadingDialog.dismiss();
                if (loginResponse.isIsSuccess()){
                    FirebaseRegister(loginResponse.getLoginView().getEmail(),loginResponse.getToken());
                    Hawk.put("loggedUser",loginResponse);
                    Intent myIntent = new Intent(FacebookRegisterActivity.this, BottomMenuActivity.class);
                    FacebookRegisterActivity.this.startActivity(myIntent);
                    FacebookRegisterActivity.this.finish();
                }
                else if(loginResponse.getMessage().equals("username_used")) {
                    Toasty.error(FacebookRegisterActivity.this, getResources().getString(R.string.username_used), Toast.LENGTH_LONG, true).show();
                }
                else
                    Toasty.error(FacebookRegisterActivity.this, getResources().getString(R.string.security_error), Toast.LENGTH_LONG, true).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                FacebookRegister(true);
                //loadingDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("accessToken", accessToken);
                params.put("email", facebookLoginResponse.getEmail());
                params.put("accountID", facebookLoginResponse.getId());
                params.put("username", etUsername.getText().toString().toLowerCase());
                params.put("profilePicURL", facebookLoginResponse.getProfile_pic());
                params.put("city", city);
                params.put("country", country);
                params.put("flagCode", alpha3Code);
                params.put("nameSurname", facebookLoginResponse.getName());

                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void Initialize() {
        accessToken = getIntent().getStringExtra("accessToken");
        ivBack = findViewById(R.id.ivBack);
        facebookLoginResponse = Hawk.get("facebookLoginResponse");
        multi = new MultiTransformation(
                new BlurTransformation(50),
                new CenterCrop(),
                //new BrightnessFilterTransformation(-0.25f),
                new RoundedCornersTransformation(50,0)
        );
        requestOptionsProfile = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        tvNameSurname = findViewById(R.id.tvNameSurname);
        tvEmail = findViewById(R.id.tvEmail);
        tvLocation = findViewById(R.id.tvLocation);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        countryProcessCompleted = false;
        Glide.with(FacebookRegisterActivity.this).load(R.drawable.white_mspaint)
                .apply(RequestOptions.bitmapTransform(multi))
                .into(ivBack);

        Glide.with(FacebookRegisterActivity.this).load(facebookLoginResponse.getProfile_pic()).apply(requestOptionsProfile).into(ivProfilePic);
        tvNameSurname.setText(facebookLoginResponse.getName());
        tvEmail.setText(facebookLoginResponse.getEmail());
        tvLocation.setText(location);
        btRegister = findViewById(R.id.btRegister);
        etUsername = findViewById(R.id.etUsername);

        btBack = findViewById(R.id.btBack);
        mAuth = FirebaseAuth.getInstance();

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    //private void GetCountryInfo(final String operation){ // Istanbul Turkey TR turk elde ediyorsun
    //    RequestQueue mRequestQueue = Volley.newRequestQueue(this);
    //    if (operation.equals("alpha2")){
    //        url = getResources().getString(R.string.countryAlpha2URL)+"&fields=region_name,country_name,country_code";
    //    }
    //    else {
    //        url = getResources().getString(R.string.countryAlpha3URL)+countryCode+"?fields=alpha3Code";
    //    }
    //    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
    //        @Override
    //        public void onResponse(String response) {
    //            GsonBuilder gsonBuilder = new GsonBuilder();
    //            gson = gsonBuilder.create();
    //            Type myType = new TypeToken<GetCountryInfoResponse>() {
    //            }.getType();
    //            getCountryInfoResponse = gson.fromJson(response, myType);
    //            if (getCountryInfoResponse != null && getCountryInfoResponse.getRegion_name() != null && getCountryInfoResponse.getCountry_name() != null) {
    //                if (operation.equals("alpha2")){
    //                    city = getCountryInfoResponse.getRegion_name();
    //                    country = getCountryInfoResponse.getCountry_name();
    //                    countryCode = getCountryInfoResponse.getCountry_code();
    //                    if (countryCode.equals("TR")){
    //                        alpha3Code = "turk";
    //                        countryProcessCompleted = true;
    //                    }
    //                    else
    //                        GetCountryInfo("alpha3");
    //                }
    //                else {
    //                    alpha3Code = getCountryInfoResponse.getAlpha3Code().toLowerCase();
    //                    countryProcessCompleted = true;
    //                }
    //            }
    //            else {
    //                Log.d("Tarja","null bu"); // Istanbul Turkey TR turk
    //                GetCountryInfo2("alpha2");
    //            }
//
    //            if (countryProcessCompleted){
    //                Log.d("Tarja",city+" "+ country+" "+countryCode+ " " + alpha3Code); // Istanbul Turkey TR turk
    //                tvLocation.setText(city+ ", "+country);
    //                tvLocation.setVisibility(View.VISIBLE);
    //            }
    //        }
    //    }, new Response.ErrorListener() {
    //        @Override
    //        public void onErrorResponse(VolleyError error) {
    //        }
    //    }) {
//
    //    };
    //    stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
    //    mRequestQueue.add(stringRequest);
    //}

    //private void GetCountryInfo2(final String operation){ // Istanbul Turkey TR turk elde ediyorsun
    //    RequestQueue mRequestQueue = Volley.newRequestQueue(this);
    //    if (operation.equals("alpha2")){
    //        url = getResources().getString(R.string.countryAlpha2URL2);
    //    }
    //    else {
    //        url = getResources().getString(R.string.countryAlpha3URL)+countryCode+"?fields=alpha3Code";
    //    }
    //    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
    //        @Override
    //        public void onResponse(String response) {
    //            GsonBuilder gsonBuilder = new GsonBuilder();
    //            gson = gsonBuilder.create();
    //            Type myType = new TypeToken<GetCountryInfo2Response>() {
    //            }.getType();
    //            getCountryInfo2Response = gson.fromJson(response, myType);
    //            Log.d("tarja",getCountryInfo2Response.toString());
    //            if (getCountryInfo2Response != null) {
    //                if (operation.equals("alpha2")){
    //                    city = getCountryInfo2Response.getRegionName();
    //                    country = getCountryInfo2Response.getCountry();
    //                    countryCode = getCountryInfo2Response.getCountryCode();
    //                    if (countryCode.equals("TR")){
    //                        alpha3Code = "turk";
    //                        countryProcessCompleted = true;
    //                    }
    //                    else
    //                        GetCountryInfo("alpha3");
    //                }
    //                else {
    //                    alpha3Code = getCountryInfo2Response.getAlpha3Code().toLowerCase();
    //                    countryProcessCompleted = true;
    //                }
    //            }
    //            if (countryProcessCompleted){
    //                Log.d("Tarja",city+" "+ country+" "+countryCode+ " " + alpha3Code); // Istanbul Turkey TR turk
    //                tvLocation.setText(city+ ", "+country);
    //                tvLocation.setVisibility(View.VISIBLE);
    //            }
    //        }
    //    }, new Response.ErrorListener() {
    //        @Override
    //        public void onErrorResponse(VolleyError error) {
    //        }
    //    }) {
//
    //    };
    //    stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
    //    mRequestQueue.add(stringRequest);
    //}

    @Override
    public void onBackPressed() {
        btBack.callOnClick();
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

                        // ...
                    }
                });
    }

}
