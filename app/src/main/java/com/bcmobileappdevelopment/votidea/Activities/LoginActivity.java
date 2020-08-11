package com.bcmobileappdevelopment.votidea.Activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mehdi.sakout.fancybuttons.FancyButton;


public class LoginActivity extends AppCompatActivity {

    ConstraintLayout loginScreenLayout;
    AnimationDrawable animationDrawable;
    ImageView ivBack, ivLoading;
    MultiTransformation multi;
    Button btFacebook;
    Gson gson;
    GetCountryInfoResponse getCountryInfoResponse;
    GetCountryInfo2Response getCountryInfo2Response;
    String city, country, countryCode, alpha3Code, url, facebookAccessToken, forgottenPasswordUserID;
    Boolean countryProcessCompleted,isEmail, isGuest, sendForgottenVerificationClicked, isEmailF, isReadyForApprovingCode;
    EditText etEmailUsername,etPassword, etEmailUsernameF, etVerificationCode, etForgottenPassword, etForgottenPasswordAgain;
    FancyButton btn_login, btn_register, btGuest, btSendVerification, btChangePassword;
    LoginResponse loginResponse, loggedUser;
    CallbackManager callbackManager;
    FacebookLoginResponse facebookLoginResponse;
    Dialog loadingDialog;
    FirebaseAuth mAuth;
    Dialog forgotPasswordDialog, forgotPasswordApproveDialog;
    TextView tvForgotPassword;
    BasicResponse forgottenPasswordVerificationResponse;
    LovelyStandardDialog bannedDialog;
    GetCityResponse getCityResponse;
    GetCountryAndFlagCodeResponse getCountryAndFlagCodeResponse;
    BasicResponse getCountryNameResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Hawk.init(LoginActivity.this).build();
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());
        setContentView(R.layout.activity_login);
        Initialize();
        InitializeListeners();
        //Register();
        if (FirebaseInstanceId.getInstance().getToken() == null)
            FirebaseInstance();
    }


    private void FirebaseInstance(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("tarja", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        Log.d("tarja","token bu amk -> "+ token);
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
                            Log.d("tarja", "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tarja", "createUserWithEmail:failure", task.getException());
                        }

                        // ...
                    }
                });
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
                            //FirebaseInstance();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tarja", "login:failure", task.getException());
                        }
                    }
                });
    }

    public void LoginFacebook(){
        Log.i("tarja","Login Başlayacak inş");
        callbackManager = CallbackManager.Factory.create();
        ArrayList<String> permissionList = new ArrayList<String>();
        permissionList.add("email");
        permissionList.add("public_profile");
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, permissionList);

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("tarja","LoginFacebookSuccess");
                        Log.d("tarja", AccessToken.getCurrentAccessToken().getToken());
                        RequestFacebookData();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("tarja", "On cancel");
                        //btFacebook.setText("Facebook İle GİRİş Yap");
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Log.d("tarja", error.toString());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void RequestFacebookData() {
        facebookAccessToken = AccessToken.getCurrentAccessToken().getToken();
        Log.d("tarja","facebooktoken = " + facebookAccessToken);
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<FacebookLoginResponse>() {
                }.getType();
                facebookLoginResponse = gson.fromJson(object.toString(), myType);
                facebookLoginResponse.setProfile_pic("https://graph.facebook.com/"+facebookLoginResponse.getId()+"/picture?type=large");
                //Log.d("tarja",AccessToken.getCurrentAccessToken().getToken());
                //Log.d("tarja", "userID-> "+facebookLoginResponse.getId());
                FacebookLogin(false);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,first_name,last_name");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void FacebookLogin(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_FacebookLogin), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<LoginResponse>() {
                }.getType();
                loginResponse = gson.fromJson(response, myType);
                loadingDialog.dismiss();
                Log.d("tarja","access_token = "+facebookAccessToken);
                Log.d("tarja","account_ID = "+facebookLoginResponse.getId().toString());
                if (loginResponse.isIsSuccess()){
                    if(loginResponse.getMessage().equals("facebook_login")){
                        Hawk.put("loggedUser",loginResponse);
                        Intent myIntent = new Intent(LoginActivity.this, BottomMenuActivity.class);
                        LoginActivity.this.startActivity(myIntent);
                        LoginActivity.this.finish();
                    }
                    else if(loginResponse.getMessage().equals("choose_username")){
                        Intent myIntent = new Intent(LoginActivity.this, FacebookRegisterActivity.class);
                        Hawk.put("facebookLoginResponse",facebookLoginResponse);
                        myIntent.putExtra("accessToken",facebookAccessToken);
                        LoginActivity.this.startActivity(myIntent);
                        overridePendingTransition(R.anim.slide_left_from_right, R.anim.slide_left);
                        finish();
                    }
                }
                else{
                    if(loginResponse.getMessage().equals("user_banned")){
                        bannedDialog.show();
                    }
                    else if(loginResponse.getMessage().equals("email_used")){
                        //TODO var mı burda bşy
                        //TODO error de geç amk :DD:
                        Toasty.error(LoginActivity.this, getResources().getString(R.string.basic_error), Toast.LENGTH_LONG, true).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                FacebookLogin(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("accessToken", facebookAccessToken);
                params.put("email", facebookLoginResponse.getEmail());
                params.put("accountID", facebookLoginResponse.getId());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void Initialize(){
        etEmailUsername = findViewById(R.id.etEmailUsername);
        etPassword = findViewById(R.id.etPassword);
        loginScreenLayout = findViewById(R.id.loginScreenLayout);
        animationDrawable = (AnimationDrawable) loginScreenLayout.getBackground();
        animationDrawable.setEnterFadeDuration(3000);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        btFacebook = findViewById(R.id.btFacebook);
        countryProcessCompleted = false;
        btGuest = findViewById(R.id.btGuest);
        isGuest = false;
        btn_login = findViewById(R.id.btn_login);
        sendForgottenVerificationClicked = false;

        btn_register = findViewById(R.id.btn_register);

        loadingDialog = new Dialog(this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        ivLoading = loadingDialog.findViewById(R.id.ivLoading);
        Glide.with(LoginActivity.this).load(R.drawable.loading).into(ivLoading);

        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        isReadyForApprovingCode = false;

        bannedDialog = new LovelyStandardDialog(LoginActivity.this);
        bannedDialog.setTopColorRes(R.color.real_Black);
        bannedDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        bannedDialog.setIcon(R.drawable.ic_banned_white);
        bannedDialog.setTitle(getResources().getString(R.string.user_banned_title));
        bannedDialog.setMessage(getResources().getString(R.string.user_banned_message));
        bannedDialog.setCancelable(false);
        bannedDialog.setPositiveButton(getResources().getString(R.string.banned_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LoginActivity.this.finish();
            }
        });

        forgotPasswordDialog = new Dialog(this);
        forgotPasswordDialog.setContentView(R.layout.dialog_forgot_password);
        etEmailUsernameF = forgotPasswordDialog.findViewById(R.id.etEmailUsername);
        btSendVerification = forgotPasswordDialog.findViewById(R.id.btVerificate);

        forgotPasswordApproveDialog = new Dialog(this);
        forgotPasswordApproveDialog.setContentView(R.layout.dialog_approve_forgot_password);
        btChangePassword = forgotPasswordApproveDialog.findViewById(R.id.btVerificate);

        etVerificationCode = forgotPasswordApproveDialog.findViewById(R.id.etVerificationCode);
        etForgottenPassword = forgotPasswordApproveDialog.findViewById(R.id.etPassword);
        etForgottenPasswordAgain = forgotPasswordApproveDialog.findViewById(R.id.etPasswordAgain);

        Window window = forgotPasswordDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        Window window2 = forgotPasswordApproveDialog.getWindow();
        window2.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window2.setGravity(Gravity.CENTER);

        forgotPasswordDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        forgotPasswordApproveDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        //ColorDrawable dialogColor = new ColorDrawable(Color.WHITE);
        //dialogColor.setAlpha(1);
        //forgotPasswordDialog.getWindow().setBackgroundDrawable(dialogColor);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        ivBack = findViewById(R.id.ivBack);

        multi = new MultiTransformation(
                new BlurTransformation(50),
                new CenterCrop(),
                //new BrightnessFilterTransformation(-0.25f),
                new RoundedCornersTransformation(50,0)
        );
        Glide.with(LoginActivity.this).load(R.drawable.white_mspaint)
                .apply(RequestOptions.bitmapTransform(multi))
                .into(ivBack);

        mAuth = FirebaseAuth.getInstance();

        city = "-";
        country = "-";
        alpha3Code = "-";
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
                CreateAndLoginGuestUser();
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

    private void InitializeListeners() {

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InputVerification()){
                    loadingDialog.show();
                    LoginButtonClicked();
                }
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                RegisterButtonClicked();
            }
        });

        btGuest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isGuest = true;
                loadingDialog.show();
                GetCountryName(false);
            }
        });
        btFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.show();
                LoginFacebook();
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isReadyForApprovingCode)
                    forgotPasswordApproveDialog.show();
                else
                    forgotPasswordDialog.show();
            }
        });

        btSendVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etEmailUsernameF.length() == 0 || etEmailUsernameF.length() > 100 ){
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.login_input), Toast.LENGTH_LONG, true).show();
                }
                else if (!sendForgottenVerificationClicked){
                    isEmailF = etEmailUsernameF.getText().toString().contains("@");
                    btSendVerification.setBackgroundColor(getResources().getColor(R.color.orange));
                    btSendVerification.setText(getResources().getString(R.string.pleaseWait));
                    sendForgottenVerificationClicked = true;
                    Log.d("tarja","gidiyor");
                    loadingDialog.show();
                    SendForgottenPasswordVerification(false);
                }
            }
        });

        btChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etVerificationCode.length()!=6) {
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.six_characters_for_code), Toast.LENGTH_LONG, true).show();
                    return;
                }
                if (etForgottenPassword.getText().toString().length() < 4 || etForgottenPasswordAgain.length() < 4){
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.at_least_four_characters), Toast.LENGTH_LONG, true).show();
                    return;
                }
                if (!etForgottenPassword.getText().toString().equals(etForgottenPasswordAgain.getText().toString())){
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.passwords_dont_match), Toast.LENGTH_LONG, true).show();
                    return;
                }
                btChangePassword.setBackgroundColor(getResources().getColor(R.color.orange));
                btChangePassword.setText(getResources().getString(R.string.pleaseWait));
                ApproveForgottenPasswordCode(false);
            }
        });
    }

    private void SendForgottenPasswordVerification(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_SendForgottenPasswordCode), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                forgottenPasswordVerificationResponse = gson.fromJson(response, myType);

                if (forgottenPasswordVerificationResponse.isIsSuccess()){
                    forgotPasswordDialog.dismiss();
                    isReadyForApprovingCode = true;
                    forgottenPasswordUserID = forgottenPasswordVerificationResponse.getMessage();
                    forgotPasswordApproveDialog.show();
                    loadingDialog.dismiss();
                    sendForgottenVerificationClicked = false;
                    Log.d("tarja","okay");
                }
                else if(forgottenPasswordVerificationResponse.getMessage().equals("facebook_account")){
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.this_is_facebook_account ), Toast.LENGTH_LONG, true).show();
                    btSendVerification.setBackgroundColor(getResources().getColor(R.color.green));
                    btSendVerification.setText(getResources().getString(R.string.send_verification_code));
                    etEmailUsernameF.setText("");
                    sendForgottenVerificationClicked = false;
                }
                else if(forgottenPasswordVerificationResponse.getMessage().equals("wait")){
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.please_wait_for_multiple_request), Toast.LENGTH_LONG, true).show();
                    btSendVerification.setBackgroundColor(getResources().getColor(R.color.green));
                    btSendVerification.setText(getResources().getString(R.string.send_verification_code));
                    sendForgottenVerificationClicked = false;
                    Log.d("tarja","else");
                }
                else if(forgottenPasswordVerificationResponse.getMessage().equals("user_not_found")){
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.user_not_found), Toast.LENGTH_LONG, true).show();
                    btSendVerification.setBackgroundColor(getResources().getColor(R.color.green));
                    btSendVerification.setText(getResources().getString(R.string.send_verification_code));
                    etEmailUsernameF.setText("");
                    sendForgottenVerificationClicked = false;
                    Log.d("tarja","else");
                }
                else{
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.wrong_login), Toast.LENGTH_LONG, true).show();
                    btSendVerification.setBackgroundColor(getResources().getColor(R.color.green));
                    btSendVerification.setText(getResources().getString(R.string.send_verification_code));
                    sendForgottenVerificationClicked = false;
                    Log.d("tarja","else");
                }
                loadingDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                SendForgottenPasswordVerification(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                if (isEmailF){
                    params.put("email", etEmailUsernameF.getText().toString());
                    params.put("username", "");
                }
                else
                {
                    params.put("username", etEmailUsernameF.getText().toString());
                    params.put("email", "");
                }
                params.put("language", Locale.getDefault().getLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void ApproveForgottenPasswordCode(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_ApproveForgottenPasswordCode), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                forgottenPasswordVerificationResponse = gson.fromJson(response, myType);

                if (forgottenPasswordVerificationResponse.isIsSuccess()){
                    forgotPasswordApproveDialog.dismiss();
                    isReadyForApprovingCode = false;
                    sendForgottenVerificationClicked = false;
                    btChangePassword.setBackgroundColor(getResources().getColor(R.color.green));
                    btChangePassword.setText(getResources().getString(R.string.send_verification_code));
                    Toasty.success(LoginActivity.this, getResources().getString(R.string.password_changed), Toast.LENGTH_LONG, true).show();
                }
                else if(forgottenPasswordVerificationResponse.getMessage().equals("wait")){
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.please_wait_for_multiple_request), Toast.LENGTH_LONG, true).show();
                    btChangePassword.setBackgroundColor(getResources().getColor(R.color.green));
                    btChangePassword.setText(getResources().getString(R.string.send_verification_code));
                    sendForgottenVerificationClicked = false;
                }
                else{
                    Toasty.error(LoginActivity.this, "error", Toast.LENGTH_LONG, true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                ApproveForgottenPasswordCode(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("userID", forgottenPasswordUserID);
                params.put("code", etVerificationCode.getText().toString());
                params.put("newPassword", etForgottenPassword.getText().toString());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public void CreateAndLoginGuestUser(){
        LoginResponse guest = new LoginResponse();
        LoginResponse.LoginViewBean guestLoginView = new LoginResponse.LoginViewBean();
        guest.setLoginView(guestLoginView);
        guest.getLoginView().setNameSurname(getResources().getString(R.string.guest_user_name_surname));
        guest.getLoginView().setUsername(getResources().getString(R.string.guest));
        guest.getLoginView().setCountry(country);
        guest.getLoginView().setCity(city);
        guest.getLoginView().setFlagCode(alpha3Code);
        guest.getLoginView().setVotedCount(0);
        guest.getLoginView().setHasVoteCount(0);
        guest.getLoginView().setCommentCount(0);
        guest.getLoginView().setFollowerCount(0);
        guest.getLoginView().setFollowingCount(0);
        guest.getLoginView().setIsActive(true);
        guest.getLoginView().setIsEmailApproved(false);
        guest.getLoginView().setID(1); // guest ID = 1
        guest.setToken(getResources().getString(R.string.guestToken));
        List<Integer> followList = new ArrayList<>();
        guest.setFollowingList(followList);

        Hawk.put("loggedUser",guest);
        Intent myIntent = new Intent(LoginActivity.this, BottomMenuActivity.class);
        LoginActivity.this.startActivity(myIntent);
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        //super.onBackPressed();
    }

    private void LoginButtonClicked(){
        String input = etEmailUsername.getText().toString();
        isEmail = input.contains("@");
        EmailUsernameLogin(false);
        if (isEmail)
            Log.d("tarja","bu bir email");
        else
            Log.d("tarja","bu bir username");

    }

    private void RegisterButtonClicked(){
        Intent myIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(myIntent);
        overridePendingTransition(R.anim.slide_left_from_right, R.anim.slide_left);
        finish();
    }

    private boolean InputVerification(){
        if (etEmailUsername.length() == 0 ){
            Toasty.error(LoginActivity.this, getResources().getString(R.string.login_input), Toast.LENGTH_LONG, true).show();
            return false;
        }
        else if (etEmailUsername.length() > 100 ){
            Toasty.error(LoginActivity.this, getResources().getString(R.string.login_input), Toast.LENGTH_LONG, true).show();
            return false;
        }
        if (etPassword.length() == 0 ){
            Toasty.error(LoginActivity.this, getResources().getString(R.string.login_input), Toast.LENGTH_LONG, true).show();
            return false;
        }
        else if (etPassword.length() > 30){
            Toasty.error(LoginActivity.this, getResources().getString(R.string.login_input), Toast.LENGTH_LONG, true).show();
            return false;
        }
        else
            return true;
    }


    private void EmailUsernameLogin(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
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
                    Hawk.put("loggedUser",loginResponse);
                    Log.d("tarja",String.valueOf(loginResponse.getFollowingList().size()));
                    Intent myIntent = new Intent(LoginActivity.this, BottomMenuActivity.class);
                    LoginActivity.this.startActivity(myIntent);
                    LoginActivity.this.finish();
                }
                else if (loginResponse.getMessage().equals("user_banned"))
                {
                    loadingDialog.dismiss();
                    bannedDialog.show();
                }
                else{
                    Toasty.error(LoginActivity.this, getResources().getString(R.string.wrong_login), Toast.LENGTH_LONG, true).show();
                    loadingDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                EmailUsernameLogin(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                if (isEmail){
                    params.put("email", etEmailUsername.getText().toString());
                    params.put("username", "");
                }
                else
                {
                    params.put("username", etEmailUsername.getText().toString());
                    params.put("email", "");
                }
                params.put("accountType", "Email");
                params.put("accountID", etPassword.getText().toString());
                params.put("nameSurname", "");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
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
    //                if (isGuest)
    //                    CreateAndLoginGuestUser();
    //            }
    //            loadingDialog.dismiss();
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
    //                if (isGuest)
    //                    CreateAndLoginGuestUser();
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

    private void KeyHash() {
        Log.d("tarja","girdi");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.bcmobileappdevelopment.votidea",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("tarja", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        }
        catch (PackageManager.NameNotFoundException e) {
            Log.d("tarja",e.getMessage());

        }
        catch (NoSuchAlgorithmException e) {
            Log.d("tarja",e.getMessage());
        }
    }
}
