package com.bcmobileappdevelopment.votidea.Fragments;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bcmobileappdevelopment.votidea.Activities.BottomMenuActivity;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetCountryAndFlagCodeResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetCountryInfoResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.roughike.bottombar.BottomBar;
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import com.yarolegovich.lovelydialog.ViewConfigurator;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    public SettingsFragment() {
        // Required empty public constructor
    }
    LovelyProgressDialog progressDialog;
    LovelyInfoDialog successInfoDialog, noChangesInfoDialog, providerDialog, suggestionInfoDialog;
    LovelyStandardDialog askDialog;
    MaterialEditText etUsername, etNameSurname;
    FancyButton btSave, btLogout, btVerificateDialog, btResendCode, btVerificate, btDetectLocation, btSuggestion, btReviewApp;
    View rootview;
    ImageView ivProfilePic, ivAddedProfilePic;
    LoginResponse loggedUser;
    RequestOptions requestOptionsProfile;
    ConstraintLayout constraintLayout;
    ConstraintSet setDefault, setChangePhoto;
    Boolean isChangingPhoto, isPhotoChanged, isUsernameChanged, isNameSurnameChanged;
    ArrayList<String> returnValue;
    List<String> croppedImagePathList;
    String destinationPath, fileName, uploadedPhotoURL, croppedImagePath, dialogFields;
    UCrop.Options options;
    BasicResponse basicResponse, addSuggestionResponse;
    StorageReference mStorageRef;
    Gson gson;
    Dialog verificationDialog;
    EditText etVerificationCode;
    TextView tvPleaseEnterCode, btOpenSource;
    LocationManager locationManager;
    Location currentLocation;
    LovelyStandardDialog updateLocationDialog;
    String city, country, alpha2code, alpha3Code;
    GetCountryInfoResponse getCountryInfoResponse;
    LovelyTextInputDialog suggestionDialog;
    GetCountryAndFlagCodeResponse getCountryAndFlagCodeResponse;
    BasicResponse getCountryNameResponse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_settings, container, false);
        Initialize();
        InitializeListeners();
        InitializeUserData();
        return rootview;
    }


    private void InitializeUserData() {
        etUsername.setText(loggedUser.getLoginView().getUsername());
        etNameSurname.setText(loggedUser.getLoginView().getNameSurname());
        Glide.with(getContext()).load(loggedUser.getLoginView().getProfilePicURL()).apply(requestOptionsProfile).into(ivProfilePic);
        Glide.with(getContext()).load(R.drawable.ic_add_a_photo).apply(requestOptionsProfile).into(ivAddedProfilePic);

    }

    public boolean isAlphaUsername(String name) {
        return name.matches("[a-zA-Z0-9_.\\-]+");
    }

    public boolean isAlphaUsernameOnlyLetters(String name) {
        return name.matches("[\\p{L} ]+");
    }

    String regex = "[^\\p{L}]";

    private boolean InputVerification(){
        boolean checkUsername = false, checkNameSurname = false, isAlpha = false;
        if (etUsername.length() < 1 || etUsername.length() >30){
            etUsername.setError(getResources().getString(R.string.inputSizeError));
        }
        else
            checkUsername = true;
        if (etNameSurname.length() < 1 || etNameSurname.length()> 40){
            etNameSurname.setError(getResources().getString(R.string.inputSizeError));
        }
        else
            checkNameSurname = true;
        if (isAlphaUsernameOnlyLetters(etNameSurname.getText().toString())){
            isAlpha = true;
        }
        else
            etNameSurname.setError(getResources().getString(R.string.enter_only_letters));

        if (checkNameSurname && checkUsername && isAlpha)
            return true;
        else
            return false;
    }

    private void DetectChanges(){
        if (!etNameSurname.getText().toString().equals(loggedUser.getLoginView().getNameSurname())){
            isNameSurnameChanged  = true;
            dialogFields+= "-"+getResources().getString(R.string.nameSurname);
        }
        else{
            isNameSurnameChanged = false;
        }
        if (!etUsername.getText().toString().equals(loggedUser.getLoginView().getUsername())){
            isUsernameChanged = true;
            dialogFields+= "\n-"+ getResources().getString(R.string.username);
        }
        else {
            isUsernameChanged = false;
        }
        if (isPhotoChanged)
            dialogFields+= "\n-"+getResources().getString(R.string.profilePicture);
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            locationManager.removeUpdates(this);
            currentLocation = location;
            Log.d("tarja","onLocationChanged");
            try {
                CityLocation(location.getLatitude(), location.getLongitude());
            }catch (Exception ex){
                Log.d("tarja",ex.getMessage());
            }
        }
        public void onProviderDisabled(String provider){
            Log.d("tarja","onProviderDisabled");

        }
        public void onProviderEnabled(String provider){ }
        public void onStatusChanged(String provider, int status,
                                    Bundle extras){ }
    };

    private void GetLocation(){
        if (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
            if (currentLocation == null){
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (currentLocation == null){
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (currentLocation != null){
                Log.d("tarja","iyi");
                try {
                    CityLocation(currentLocation.getLatitude(), currentLocation.getLongitude());
                }catch (Exception ex){
                    Log.d("tarja",ex.getMessage());
                }
            }
            else {
                Log.d("tarja","requestLocationUpdates");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 1, locationListener);
            }
        }
    }

    private void DetectLocation(){
        locationManager = (LocationManager) getContext().getSystemService(getContext().LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
        }
        else if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            providerDialog.show();
        }
        else{
            GetLocation();
            Toasty.info(getContext(), getResources().getString(R.string.getting_location_info), Toast.LENGTH_LONG, true).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            Toasty.info(getContext(), getResources().getString(R.string.getting_location_info), Toast.LENGTH_LONG, true).show();
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                providerDialog.show();
            }
            else{
                GetLocation();
                Toasty.info(getContext(), getResources().getString(R.string.getting_location_info), Toast.LENGTH_LONG, true).show();
            }
        }
    }

    private String CityLocation(double lat, double lon){

        Geocoder geocoder = new Geocoder(getContext(), Locale.ENGLISH);
        List<Address> addresses;
        try {
             addresses = geocoder.getFromLocation(lat,lon,10);
             if (addresses.size()>0){
                 for(Address adr: addresses){
                     if (adr.getLocality() != null && adr.getLocality().length() > 0){
                         BottomMenuActivity activity = (BottomMenuActivity) getActivity();
                         city = adr.getLocality();
                         country = adr.getCountryName();
                         alpha2code = adr.getCountryCode();
                         Log.d("tarja","city = " + city);
                         Log.d("tarja","country = " + country);
                         Log.d("tarja","alpha2code = " + alpha2code);
                         updateLocationDialog.setMessage(getResources().getString(R.string.update_location_message )+ "\n\n"+city+", "+country);
                         updateLocationDialog.show();
                         break;
                     }
                 }
             }
        }
        catch (Exception ex){
            Log.d("tarja",ex.getMessage());
        }
        return city;
    }

    private void InitializeListeners() {
        etNameSurname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!etNameSurname.getText().toString().equals(loggedUser.getLoginView().getNameSurname()) && btSave.getVisibility() == View.GONE){
                    btSave.setVisibility(View.VISIBLE);
                    Log.d("tarja","değiş amk");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btOpenSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    startActivity(new Intent(getContext(), com.google.android.gms.oss.licenses.OssLicensesMenuActivity.class));
                }
                catch (Exception ex){
                }
            }
        });

        btSuggestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                suggestionDialog.setInitialInput("");
                suggestionDialog.show();
            }
        });

        btReviewApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.bcmobileappdevelopment.votidea")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.bcmobileappdevelopment.votidea")));
                }
            }
        });

        btDetectLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DetectLocation();
            }
        });

        btLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuActivity activity = (BottomMenuActivity) getActivity();
                activity.Logout();
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetectChanges();
                if (isPhotoChanged || isUsernameChanged || isNameSurnameChanged){
                    if (InputVerification()){
                        dialogFields = "";
                        DetectChanges();
                        askDialog.setMessage(getResources().getString(R.string.fieldsAreUpdated)+ "\n"+dialogFields);
                        askDialog.show();
                    }
                }
                else
                {
                    noChangesInfoDialog.show();
                }
            }
        });
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPhotoChanged){
                    TransitionManager.beginDelayedTransition(constraintLayout);
                    setDefault.applyTo(constraintLayout);
                    isChangingPhoto = false;
                    isPhotoChanged = false;
                    Glide.with(getContext()).load(R.drawable.ic_add_a_photo).apply(requestOptionsProfile).into(ivAddedProfilePic);
                }
            }
        });
        ivAddedProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPhotoChanged)
                {
                    Pix.start(SettingsFragment.this,1);
                }
            }
        });
        askDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                if(isPhotoChanged){
                    UploadImage();
                }
                else if(isNameSurnameChanged || isUsernameChanged)
                    UpdateProfileData(false);
                else{
                    // değişiklik algılanmadı infoDialog
                }
            }
        });

        if (!loggedUser.getLoginView().isIsEmailApproved()){
            btVerificateDialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verificationDialog.show();
                    //etVerificationCode.requestFocus();
                    //InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    //imm.toggleSoftInput(0,InputMethodManager.SHOW_IMPLICIT);
                    //verificationDialog.getCurrentFocus();

                }
            });
            btResendCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SendVerificationCode(false);
                }
            });
            btVerificate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (etVerificationCode.getText().length() == 6)
                        ApproveVerificationCode(false);
                    else {
                        Toasty.warning(getContext(), getResources().getString(R.string.enter6digits), Toast.LENGTH_LONG, true).show();
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK ){
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Uri source = Uri.fromFile(new File(returnValue.get(0)));
            destinationPath = getContext().getFilesDir().toString()+CreateFileName();
            Uri destination = Uri.fromFile(new File(destinationPath));
            UCrop.of(source, destination)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(800,800)
                    .withOptions(options)
                    .start(getActivity(),SettingsFragment.this,2);

        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            Uri resultUri = UCrop.getOutput(data);
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptionsProfile).into(ivAddedProfilePic);
            isPhotoChanged = true;
            Uri resultCropUri = UCrop.getOutput(data);
            croppedImagePath = resultCropUri.getPath();
            TransitionManager.beginDelayedTransition(constraintLayout);
            setChangePhoto.applyTo(constraintLayout);
            btSave.setVisibility(View.VISIBLE);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void Initialize() {
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        btSave = rootview.findViewById(R.id.btSave);
        btSuggestion = rootview.findViewById(R.id.btSuggestion);
        btReviewApp = rootview.findViewById(R.id.btReviewApp);
        btVerificateDialog = rootview.findViewById(R.id.btVerificateDialog);
        ivProfilePic = rootview.findViewById(R.id.ivProfilePic);
        ivAddedProfilePic = rootview.findViewById(R.id.ivAddedProfilePic);
        btLogout = rootview.findViewById(R.id.btLogout);
        etNameSurname = rootview.findViewById(R.id.etNameSurname);
        etUsername = rootview.findViewById(R.id.etUsername);
        requestOptionsProfile = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (!loggedUser.getLoginView().isIsEmailApproved()){
            btVerificateDialog.setVisibility(View.VISIBLE);
            Log.d("tarja","evet açılması lazım bncd");

            verificationDialog = new Dialog(getContext());
            verificationDialog.setContentView(R.layout.dialog_verification);
            etVerificationCode = verificationDialog.findViewById(R.id.etVerificationCode);
            btResendCode = verificationDialog.findViewById(R.id.btResendCode);
            btVerificate = verificationDialog.findViewById(R.id.btVerificate);
            tvPleaseEnterCode = verificationDialog.findViewById(R.id.tvPleaseEnterCode);
            tvPleaseEnterCode.setText(getResources().getString(R.string.please_enter_code)+ "\n\n"+getResources().getString(R.string.verification_sent_to)+"\n"+loggedUser.getLoginView().getEmail());
        }

        ViewConfigurator<EditText> viewConfigurator = new ViewConfigurator<EditText>() {
            @Override
            public void configureView(EditText v) {
                v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(500)});
                v.setInputType(InputType.TYPE_CLASS_TEXT);
                v.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            }
        };

        suggestionDialog = new LovelyTextInputDialog(getContext());
        suggestionDialog.configureEditText(viewConfigurator);
        suggestionDialog.setTopColor(getResources().getColor(R.color.colorPrimary));
        suggestionDialog.setIcon(R.drawable.ic_send_message);
        suggestionDialog.setTopTitleColor(getResources().getColor(R.color.white));
        //suggestionDialog.setHint(getResources().getString(R.string.optional));
        suggestionDialog.setTitle(getResources().getString(R.string.suggestion_dialog_title));
        suggestionDialog.setNegativeButton(getResources().getString(R.string.cancel),null);
        suggestionDialog.setConfirmButton(getResources().getString(R.string.ok), new LovelyTextInputDialog.OnTextInputConfirmListener() {
            @Override
            public void onTextInputConfirmed(String text) {
                AddSuggestion(text,false);
            }
        });

        updateLocationDialog = new LovelyStandardDialog(getContext());
        updateLocationDialog.setTopColorRes(R.color.colorPrimary);
        updateLocationDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        updateLocationDialog.setIcon(R.drawable.ic_location_white);
        updateLocationDialog.setNegativeButton(getResources().getString(R.string.no),null);
        updateLocationDialog.setTitle(getResources().getString(R.string.update_location_title));
        updateLocationDialog.setMessage(getResources().getString(R.string.update_location_message));
        updateLocationDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetCountryName(false);
                //GetAlpha3Code();
            }
        });

        btDetectLocation = rootview.findViewById(R.id.btDetectLocation);
        setDefault = new ConstraintSet();
        setChangePhoto = new ConstraintSet();
        constraintLayout = rootview.findViewById(R.id.FragmentSettingsChildConstraintLayout);
        setDefault.clone(constraintLayout);
        setChangePhoto.clone(getContext(),R.layout.fragment_settings_change_photo_2);
        isChangingPhoto = false;

        options = new UCrop.Options();
        options.setCircleDimmedLayer(true);
        options.setCompressionQuality(50);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setToolbarTitle(getResources().getString(R.string.editPhoto));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(R.color.blue));
        croppedImagePathList = new ArrayList<>();
        isPhotoChanged = false;
        isNameSurnameChanged = false;
        isUsernameChanged = false;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        progressDialog = new LovelyProgressDialog(getContext());
        progressDialog
                .setIcon(R.drawable.ic_hourglass_white)
                .setTitle(getResources().getString(R.string.pleaseWait))
                .setCancelable(false)
                .setTopColorRes(R.color.colorPrimary);

        askDialog = new LovelyStandardDialog(getContext());
        askDialog.setTopColorRes(R.color.colorPrimary);
        askDialog.setIcon(R.drawable.ic_error_outline);
        askDialog.setTitle(getResources().getString(R.string.updateUserData));
        askDialog.setNegativeButton(getResources().getString(R.string.no),null);

        successInfoDialog = new LovelyInfoDialog(getContext());
        successInfoDialog.setTopColorRes(R.color.colorPrimary);
        successInfoDialog.setIcon(R.drawable.ic_success_white);
        successInfoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        successInfoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        successInfoDialog.setTitle(getResources().getString(R.string.processSuccess));
        successInfoDialog.setMessage(getResources().getString(R.string.profileUpdated));

        noChangesInfoDialog = new LovelyInfoDialog(getContext());
        noChangesInfoDialog.setTopColorRes(R.color.colorPrimary);
        noChangesInfoDialog.setIcon(R.drawable.ic_error_outline);
        noChangesInfoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        noChangesInfoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        noChangesInfoDialog.setTitle(getResources().getString(R.string.notice));
        noChangesInfoDialog.setMessage(getResources().getString(R.string.thereIsNoChange));

        providerDialog = new LovelyInfoDialog(getContext());
        providerDialog.setTopColorRes(R.color.colorPrimary);
        providerDialog.setIcon(R.drawable.ic_location_white);
        providerDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        providerDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        providerDialog.setTitle(getResources().getString(R.string.provider_dialog_title));
        providerDialog.setMessage(getResources().getString(R.string.provider_dialog_message));

        suggestionInfoDialog = new LovelyInfoDialog(getContext());
        suggestionInfoDialog.setTopColorRes(R.color.colorPrimary);
        suggestionInfoDialog.setIcon(R.drawable.ic_success_white);
        suggestionInfoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        suggestionInfoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        suggestionInfoDialog.setTitle(getResources().getString(R.string.processSuccess));
        suggestionInfoDialog.setMessage(getResources().getString(R.string.suggestion_info_dialog_message));

        btOpenSource = rootview.findViewById(R.id.btOpenSource);
    }

    private void GetCountryName(final boolean useBackup){
        country = "";
        city = "";
        alpha3Code = "";
        alpha3Code = Locale.getDefault().getISO3Country();
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getBaseContext());
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
                    UpdateLocation(false);
                }
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



    private void UpdateLocation(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_UpdateLocation), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                basicResponse = gson.fromJson(response, myType);

                if (basicResponse.isIsSuccess()) {
                    loggedUser.getLoginView().setCity(city);
                    loggedUser.getLoginView().setCountry(country);
                    loggedUser.getLoginView().setFlagCode(alpha3Code);
                    Hawk.put("loggedUser",loggedUser);
                    BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                    host.selectTabAtPosition(0,true);
                }
                else{
                    Toasty.error(getContext(), getResources().getString(R.string.basic_error), Toast.LENGTH_LONG, true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                UpdateLocation(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("city", city);
                params.put("country", country);
                params.put("flagCode", alpha3Code.toLowerCase());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void AddSuggestion(final String message, final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_AddSuggestion), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                addSuggestionResponse = gson.fromJson(response, myType);
                progressDialog.dismiss();
                if (addSuggestionResponse.isIsSuccess()) {
                    suggestionInfoDialog.show();
                }
                else if(addSuggestionResponse.getMessage().equals("wait")){
                    Toasty.error(getContext(), getResources().getString(R.string.wait_1_minute), Toast.LENGTH_LONG, true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                AddSuggestion(message,true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("message", message);
                params.put("language", Locale.getDefault().getLanguage() +" - "+ Locale.getDefault().getDisplayLanguage());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private String CreateFileName(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
        fileName = "/u"+String.valueOf(loggedUser.getLoginView().getID())+"_"+df.format(calendar.getTime())+".jpg";
        return  fileName;
    }

    private void UploadImage(){
        Uri file = Uri.fromFile(new File(croppedImagePath));
        final StorageReference riversRef = mStorageRef.child("images"+CreateFileName());
        riversRef.putFile(file).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                //double progress = 100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                //progressDialog.setMessage(uploadedImageCount+"/"+loadedImageCount+" Yüklendi");
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                uploadedPhotoURL = downloadUrl.toString();
                                UpdateProfileData(false);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                    }
                });
    }

    private void UpdateProfileData(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_UpdateProfileData), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                basicResponse = gson.fromJson(response, myType);

                if (basicResponse.isIsSuccess()) {
                    progressDialog.dismiss();
                    successInfoDialog.show();
                    if (isPhotoChanged)
                        loggedUser.getLoginView().setProfilePicURL(uploadedPhotoURL);
                    if (isNameSurnameChanged)
                        loggedUser.getLoginView().setNameSurname(etNameSurname.getText().toString());
                    if (isUsernameChanged)
                        loggedUser.getLoginView().setUsername(etUsername.getText().toString());
                    Hawk.put("loggedUser",loggedUser);
                    BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                    host.selectTabAtPosition(4,true);
                }
                else{
                    Log.d("tarja",basicResponse.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                UpdateProfileData(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                if (isPhotoChanged)
                    params.put("profilePicURL", uploadedPhotoURL);
                else
                    params.put("profilePicURL", "");
                if (isUsernameChanged)
                    params.put("username", etUsername.getText().toString());
                else
                    params.put("username", "");
                if (isNameSurnameChanged)
                    params.put("nameSurname", etNameSurname.getText().toString());
                else
                    params.put("nameSurname", "");
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void SendVerificationCode(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_SendVerificationCode), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                basicResponse = gson.fromJson(response, myType);

                if (basicResponse.isIsSuccess()) {
                    Toasty.info(getContext(), getResources().getString(R.string.verification_code_sent ) + loggedUser.getLoginView().getEmail(), Toast.LENGTH_LONG, true).show();
                }
                else if(basicResponse.getMessage().equals("wait")){
                    Toasty.error(getContext(), getResources().getString(R.string.wait_verification_code), Toast.LENGTH_LONG, true).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                SendVerificationCode(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("language", Locale.getDefault().getLanguage());
                params.put("mailTo", String.valueOf(loggedUser.getLoginView().getEmail()));
                params.put("sendJson", String.valueOf(true));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void ApproveVerificationCode(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_ApproveVerificationCode), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                basicResponse = gson.fromJson(response, myType);

                if (basicResponse.isIsSuccess()) {
                    Toasty.success(getContext(), getResources().getString(R.string.account_verificated), Toast.LENGTH_LONG, true).show();
                    if (verificationDialog.isShowing()){
                        verificationDialog.dismiss();
                    }
                    btVerificateDialog.setVisibility(View.GONE);
                    loggedUser.getLoginView().setIsEmailApproved(true);
                    Hawk.put("loggedUser",loggedUser);

                    HomeFragment hf =(HomeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("home_fragment");
                    if (hf != null)
                        hf.RefreshLoggedInUser();
                    ExploreFragment ef =(ExploreFragment) getActivity().getSupportFragmentManager().findFragmentByTag("explore_fragment");
                    if (ef != null)
                        ef.RefreshLoggedInUser();
                    ProfileFragment pf =(ProfileFragment) getActivity().getSupportFragmentManager().findFragmentByTag("profile_fragment");
                    if (pf != null)
                        pf.RefreshLoggedInUser();
                    OtherProfileFragment opf =(OtherProfileFragment) getActivity().getSupportFragmentManager().findFragmentByTag("other_profile_fragment");
                    if (opf != null)
                        opf.RefreshLoggedInUser();
                    AddVoteFragment avf = (AddVoteFragment) getActivity().getSupportFragmentManager().findFragmentByTag("add_vote_fragment");
                    if (avf != null)
                        avf.RefreshLoggedInUser();
                    CommentsFragment cf = (CommentsFragment) getActivity().getSupportFragmentManager().findFragmentByTag("comments_fragment");
                    if (cf!= null){
                        cf.RefreshLoggedInUser();
                    }
                }
                else if(basicResponse.getMessage().equals("wait")){
                    Toasty.error(getContext(), getResources().getString(R.string.wait_verification_code_enter), Toast.LENGTH_LONG, true).show();
                }
                else if(!basicResponse.isIsSuccess()){
                    Toasty.error(getContext(), getResources().getString(R.string.wrong_verification_code), Toast.LENGTH_LONG, true).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                ApproveVerificationCode(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("code", String.valueOf(etVerificationCode.getText().toString()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }
}
