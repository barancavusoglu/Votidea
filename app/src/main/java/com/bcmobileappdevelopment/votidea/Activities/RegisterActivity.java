package com.bcmobileappdevelopment.votidea.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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
import com.crashlytics.android.Crashlytics;
import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mehdi.sakout.fancybuttons.FancyButton;

public class RegisterActivity extends AppCompatActivity {

    ConstraintLayout registerLayout;
    AnimationDrawable animationDrawable;
    FancyButton btBack, btRegister;
    MultiTransformation multi;
    ImageView ivBack, ivLoading;
    RequestOptions requestOptionsProfile, requestOptionsPhoto;
    boolean countryProcessCompleted;
    String destinationPath, fileName, croppedImagePath, uploadedImageURL, url, city,country, alpha3Code, countryCode;
    ArrayList<String> returnValue;
    UCrop.Options options;
    TextView tvEmail, tvUsername;
    Dialog loadingDialog;
    ConstraintLayout clPassword,clPasswordAgain;
    MaterialEditText etEmail, etUsername, etPassword, etPasswordAgain, etNameSurname;
    Boolean isFacebookRegister, registerInProgress;
    LovelyStandardDialog photoDialog;
    StorageReference mStorageRef;
    Gson gson;
    LoginResponse loginResponse;
    GetCountryInfoResponse getCountryInfoResponse;
    GetCountryInfo2Response getCountryInfo2Response;
    FirebaseAuth mAuth;
    BasicResponse insertFirebaseIDResponse, getCountryNameResponse;
    GetCityResponse getCityResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Hawk.init(RegisterActivity.this).build();
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initialize();
        GetCountryName(false);
        //GetCountryInfo("alpha2");
        InitializeListeners();
        //Log.d("tarja",FirebaseInstanceId.getInstance().getToken());
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

    //private void GetCountryAndFlagCode(final boolean useBackup) {
    //    country = "";
    //    city = "";
    //    alpha3Code = "";
    //    countryCode = "";
    //    countryCode = Locale.getDefault().getLanguage();
    //    RequestQueue mRequestQueue = Volley.newRequestQueue(this);
    //    String webserviceURL = getResources().getString(R.string.webservice_main);
    //    if (useBackup)
    //        webserviceURL = getResources().getString(R.string.webservice_backup);
    //    StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetCountryAndFlagCode), new Response.Listener<String>() {
    //        @Override
    //        public void onResponse(String response) {
    //            GsonBuilder gsonBuilder = new GsonBuilder();
    //            gson = gsonBuilder.create();
    //            Type myType = new TypeToken<GetCountryAndFlagCodeResponse>() {
    //            }.getType();
    //            getCountryAndFlagCodeResponse = gson.fromJson(response, myType);
    //            if (getCountryAndFlagCodeResponse.isIsSuccess()) {
    //                country = getCountryAndFlagCodeResponse.getCountryAndFlagCode().getName();
//
    //                if (getCountryAndFlagCodeResponse.getCountryAndFlagCode().getAlpha3Code().equals("TUR"))
    //                    alpha3Code = "turk";
    //                else
    //                    alpha3Code = getCountryAndFlagCodeResponse.getCountryAndFlagCode().getAlpha3Code();
    //            }
    //            GetCity();
    //        }
    //    }, new Response.ErrorListener() {
    //        @Override
    //        public void onErrorResponse(VolleyError error) {
    //            if (!useBackup)
    //            GetCountryAndFlagCode(true);
    //        }
    //    }) {
    //        @Override
    //        protected Map<String, String> getParams() {
    //            Map<String, String> params = new HashMap<String, String>();
    //            params.put("masterPass", getResources().getString(R.string.masterpass));
    //            params.put("alpha2code", countryCode);
    //            return params;
    //        }
    //    };
    //    stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
    //    mRequestQueue.add(stringRequest);
    //}

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
                    if (getCityResponse.getCity() != null)
                        city = getCityResponse.getCity();
                    else if(getCityResponse.getRegion_name() != null)
                        city = getCityResponse.getRegion_name();
                }
                Log.d("tarja",country+countryCode+city+alpha3Code);
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

    private void Initialize() {
        isFacebookRegister = false;
        registerInProgress = false;
        registerLayout = findViewById(R.id.registerLayout);
        animationDrawable = (AnimationDrawable) registerLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        btBack = findViewById(R.id.btBack);
        ivBack = findViewById(R.id.ivBack);
        btRegister = findViewById(R.id.btRegister);
        clPassword = findViewById(R.id.clPassword);
        clPasswordAgain = findViewById(R.id.clPassword2);
        etNameSurname = findViewById(R.id.etNameSurname);

        countryProcessCompleted = false;
        etEmail = findViewById(R.id.etEmail);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etPasswordAgain = findViewById(R.id.etPasswordAgain);

        multi = new MultiTransformation(
                new BlurTransformation(50),
                new CenterCrop(),
                //new BrightnessFilterTransformation(-0.25f),
                new RoundedCornersTransformation(50,0)
        );
        requestOptionsProfile = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        requestOptionsPhoto = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCompressionQuality(50);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setToolbarTitle(getResources().getString(R.string.editPhoto));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(R.color.blue));

        Glide.with(RegisterActivity.this).load(R.drawable.white_mspaint)
                .apply(RequestOptions.bitmapTransform(multi))
                .into(ivBack);

        //if (getIntent().getStringExtra("IsFacebookRegister") != null && getIntent().getStringExtra("IsFacebookRegister").equals("evetcnm")){
        //    isFacebookRegister = true;
        //    facebookLoginResponse = Hawk.get("facebookLoginResponse");
        //    Glide.with(RegisterActivity.this).load(facebookLoginResponse.getProfile_pic()).apply(requestOptionsProfile).into(ivProfilePic);
        //    etEmail.setText(facebookLoginResponse.getEmail());
        //    etEmail.setEnabled(false);
        //    clPassword.setVisibility(View.GONE);
        //    clPasswordAgain.setVisibility(View.GONE);
        //}

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ivLoading = loadingDialog.findViewById(R.id.ivLoading);
        Glide.with(RegisterActivity.this).load(R.drawable.loading).into(ivLoading);

        photoDialog = new LovelyStandardDialog(RegisterActivity.this);
        photoDialog.setTopColorRes(R.color.colorPrimary);
        photoDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        photoDialog.setIcon(R.drawable.ic_photo_camera);
        photoDialog.setPositiveButton(getResources().getString(R.string.continue_no_photo), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.show();
                RegisterEmailUser(false);
            }
        });
        photoDialog.setNegativeButton(getResources().getString(R.string.upload_photo), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pix.start(RegisterActivity.this,1);
            }
        });
        photoDialog.setTitle(getResources().getString(R.string.you_didnt_upload_photo));
        photoDialog.setMessage(getResources().getString(R.string.sure_to_not_upload_photo));
        mAuth = FirebaseAuth.getInstance();

        city = "Unknown";
        country = "Unknown";
        alpha3Code = "-";
        countryCode = "-";
    }

    private void UploadPhoto(){
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(croppedImagePath));
        final StorageReference riversRef = mStorageRef.child("images"+CreateFileName());
        riversRef.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        uploadedImageURL = downloadUrl.toString();
                        //photoUploaded = true;
                        RegisterEmailUser(false);
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Log.d("Tarja","UPLOAD FAILURE FUCK");
                        loadingDialog.dismiss();
                    }
                });
    }

    private void InitializeListeners() {
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(myIntent);
                overridePendingTransition(R.anim.slide_right_from_left, R.anim.slide_right);
                finish();
            }
        });
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InputVerification() && !registerInProgress){
                    loadingDialog.show();
                    RegisterEmailUser(false);
                }
                // check input
                // upload photo
                // call register
                // if any errors -> show errors
                // call register again, dont upload photo. If photo changed -> upload photo
            }
        });
    }

    private void RegisterEmailUser(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        registerInProgress = true;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_RegisterEmailUser), new Response.Listener<String>() {
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
                    InsertFirebaseID(FirebaseInstanceId.getInstance().getToken(),false);
                    Intent myIntent = new Intent(RegisterActivity.this, BottomMenuActivity.class);
                    RegisterActivity.this.startActivity(myIntent);
                    RegisterActivity.this.finish();
                }
                else if(loginResponse.getMessage().equals("username_is_used")) {
                    Toasty.error(RegisterActivity.this, getResources().getString(R.string.username_used), Toast.LENGTH_LONG, true).show();
                    registerInProgress = false;
                    //loadingDialog.dismiss();
                }
                else if(loginResponse.getMessage().equals("email_is_used")) {
                    Toasty.error(RegisterActivity.this, getResources().getString(R.string.email_is_used), Toast.LENGTH_LONG, true).show();
                    registerInProgress = false;
                    //loadingDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                RegisterEmailUser(true);
                //loadingDialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("nameSurname", etNameSurname.getText().toString());
                params.put("email", etEmail.getText().toString().toLowerCase());
                params.put("profilePicURL", "default");
                params.put("city", city);
                params.put("country", country);
                params.put("flagCode", alpha3Code);
                params.put("username", etUsername.getText().toString().toLowerCase());
                params.put("password", etPassword.getText().toString());
                params.put("language", Locale.getDefault().getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(15000,10,1.0f));
        mRequestQueue.add(stringRequest);
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
                params.put("userID", String.valueOf(loginResponse.getLoginView().getID()));
                params.put("token", loginResponse.getToken());
                params.put("firebaseInstanceID", token);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public boolean isAlphaUsername(String name) {
        return name.matches("[a-zA-Z0-9_.\\-]+");
    }

    public boolean isAlphaNameSurnameOnlyLetters(String name) {
        return name.matches("[\\p{L} ]+");
    }

    public boolean isAlphaEmail(String name) {
        return name.matches("[a-zA-Z0-9\\-@_.]+");
    }

    private boolean InputVerification() {
        if (etUsername.length()<5 || etUsername.length()>30)
        {
            etUsername.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.enter_specific_range_of_characters)+ "\n-" + getResources().getString(R.string.username), Toast.LENGTH_LONG, true).show();
        }
        else if (!isAlphaUsername(etUsername.getText().toString()))
        {
            etUsername.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.use_specific_punctuation_marks_username)+ "\n-" + getResources().getString(R.string.username), Toast.LENGTH_LONG, true).show();
        }
        else if(etEmail.length()<5 || etEmail.length()>100){
            etEmail.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.enter_specific_range_of_characters)+ "\n-" + getResources().getString(R.string.email), Toast.LENGTH_LONG, true).show();
        }
        else if(!isAlphaEmail(etEmail.getText().toString())){
            etEmail.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.use_specific_punctuation_marks_email) + "\n-" + getResources().getString(R.string.email), Toast.LENGTH_LONG, true).show();
        }
        else if(etNameSurname.length()>100){
            etNameSurname.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.enter_specific_range_of_characters)+ "\n-" + getResources().getString(R.string.nameSurname), Toast.LENGTH_LONG, true).show();
        }
        else if(!isAlphaNameSurnameOnlyLetters(etNameSurname.getText().toString())){
            etNameSurname.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.enter_only_letters)+ "\n-" + getResources().getString(R.string.nameSurname), Toast.LENGTH_LONG, true).show();
        }
        else if(etPassword.length()<4 || etPassword.length()>30){
            etPassword.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.enter_specific_range_of_characters)+ "\n-" + getResources().getString(R.string.password), Toast.LENGTH_LONG, true).show();
        }
        else if(!etPassword.getText().toString().equals(etPasswordAgain.getText().toString())){
            etPasswordAgain.setError(getResources().getString(R.string.please_check));
            Toasty.error(RegisterActivity.this, getResources().getString(R.string.passwords_dont_match), Toast.LENGTH_LONG, true).show();
        }
        else
            return true;
        return false;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK ){
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Uri source = Uri.fromFile(new File(returnValue.get(0)));
            destinationPath = getFilesDir().toString()+CreateFileName();
            Uri destination = Uri.fromFile(new File(destinationPath));
            UCrop.of(source, destination)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(800,800)
                    .withOptions(options)
                    .start(RegisterActivity.this,2);

        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            Uri resultUri = UCrop.getOutput(data);
            //Glide.with(this).load(resultUri.getPath()).apply(requestOptionsProfile).into(ivProfilePic);
            //btRemovePic.setVisibility(View.VISIBLE);
            Uri resultCropUri = UCrop.getOutput(data);
            croppedImagePath = resultCropUri.getPath();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String CreateFileName(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
        fileName = "/p_"+df.format(calendar.getTime())+".jpg";
        return  fileName;
    }

    @Override
    public void onBackPressed() {
        btBack.callOnClick();
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
    //                else if(alpha3Code != null) {
    //                    alpha3Code = getCountryInfoResponse.getAlpha3Code().toLowerCase();
    //                    countryProcessCompleted = true;
    //                }
    //            }
    //            else {
    //                GetCountryInfo2("alpha2");
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
    //                    if (city != null)
    //                        city = getCountryInfo2Response.getRegionName();
    //                    if(country != null)
    //                        country = getCountryInfo2Response.getCountry();
    //                    if (countryCode != null){
    //                        countryCode = getCountryInfo2Response.getCountryCode();
    //                        if (countryCode.equals("TR")){
    //                            alpha3Code = "turk";
    //                            countryProcessCompleted = true;
    //                        }
    //                        else
    //                            GetCountryInfo("alpha3");
    //                    }
    //                }
    //                else {
    //                    alpha3Code = getCountryInfo2Response.getAlpha3Code().toLowerCase();
    //                    countryProcessCompleted = true;
    //                }
    //            }
    //            else
    //            {
    //                countryCode = Locale.getDefault().getLanguage().toUpperCase();
    //                GetCountryInfo2("alpha3");
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