package com.bcmobileappdevelopment.votidea.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.fxn.pix.Pix;
import com.github.zagum.switchicon.SwitchIconView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
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
import com.roughike.bottombar.BottomBar;
import com.yalantis.ucrop.UCrop;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddVoteFragment extends Fragment {

    public AddVoteFragment() {
        // Required empty public constructor
    }

    View rootView;
    StorageReference mStorageRef;
    LovelyProgressDialog progressDialog;
    LovelyInfoDialog infoDialog;
    ImageView iv1,iv2,iv3,iv4;
    int loadedImageCount, uploadedImageCount;
    Camera camera;
    String destinationPath, iv1Path,iv2Path,iv3Path,iv4Path,fileName,urlListCombined;
    ArrayList<String> returnValue;
    Boolean iv1Filled,iv2Filled,iv3Filled,iv4Filled;
    FancyButton btAddPhoto, btSave, btDeleteiv1,btDeleteiv2,btDeleteiv3,btDeleteiv4;
    TextView asd;
    EditText etDescription;
    RequestOptions requestOptions;
    List<String> croppedImagePathList, uploadedImagePathList;
    UCrop.Options options;
    List<myImage> myImageList;
    Gson gson;
    BasicResponse basicResponse;
    SwitchIconView swIsAnonymous, swIsCommentAllowed;
    TextView tvIsAnonymous, tvIsCommentAllowed;
    LovelyStandardDialog approveDialog, guestDialog, bannedDialog;

    LoginResponse loggedUser;
    InterstitialAd mInterstitialAd;

    private class myImage{
        private String filePath ="";
        private String fileName="";
        private Boolean isFilled= false;

        private String getFilePath() {
            return filePath;
        }

        private void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        private String getFileName() {
            return fileName;
        }

        private void setFileName(String fileName) {
            this.fileName = fileName;
        }

        private Boolean getFilled() {
            return isFilled;
        }

        private void setFilled(Boolean filled) {
            isFilled = filled;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_add_vote, container, false);

        mInterstitialAd = new InterstitialAd(getContext());
        mInterstitialAd.setAdUnitId("ca-app-pub-6156316056990400/2485738321");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        Initialize();
        InitializeOnClickListeners();
        //TestTime();
        //Uri source = Uri.fromFile(new File("/storage/emulated/0/DCIM/Camera/IMG_20181004_234321.jpg"));


        //bunları initialize içine almalı sanırım
        myImageList = new ArrayList<>();
        myImageList.add(new myImage());
        myImageList.add(new myImage());
        myImageList.add(new myImage());
        myImageList.add(new myImage());

        return rootView;
    }

    private void TestTime() {

        Locale current = rootView.getResources().getConfiguration().locale;

        SimpleDateFormat oldFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        oldFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        String dueDateAsNormal ="";
        try {
            value = oldFormatter.parse("2018-01-01 15:00:00");
            SimpleDateFormat newFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");

            newFormatter.setTimeZone(TimeZone.getDefault());
            dueDateAsNormal = newFormatter.format(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d("tarja","date-> "+dueDateAsNormal);
    }

    public void RefreshLoggedInUser(){
        loggedUser = Hawk.get("loggedUser");
    }

    private void UploadPhoto(){
        progressDialog.show();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20181004-WA0017.jpg"));
        final StorageReference riversRef = mStorageRef.child("images/quokka"+System.currentTimeMillis()+".jpg");
        riversRef.putFile(file).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = 100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount();
                progressDialog.setMessage(String.valueOf(progress)+"% Yüklendi");
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Uri downloadUrl = uri;
                                Log.d("Tarja", downloadUrl.toString());
                            }
                        });
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDown();
                        progressDialog.dismiss();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void StartUpload(){
        urlListCombined = "";
        uploadedImagePathList = new ArrayList<>();
        uploadedImageCount = 0;
        progressDialog.show();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        for (int i =0;i<4;i++){
            if (myImageList.get(i).isFilled)
            {
                UploadImage(myImageList.get(i));
            }
        }
    }

    private void UploadImage(myImage image){
        Uri file = Uri.fromFile(new File(image.getFilePath()));
        final StorageReference riversRef = mStorageRef.child("images/"+loggedUser.getLoginView().getFlagCode()+image.getFileName());
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
                                uploadedImagePathList.add(downloadUrl.toString());
                                urlListCombined += downloadUrl.toString()+",";
                                //Log.d("Tarja","Download link:"+ downloadUrl.toString());
                                uploadedImageCount++;
                                progressDialog.setMessage(uploadedImageCount+"/"+loadedImageCount+" "+getResources().getString(R.string.uploaded));
                                if (uploadedImageCount == loadedImageCount){
                                    //Log.d("tarja","urlList="+urlListCombined);
                                    urlListCombined = urlListCombined.substring(0,urlListCombined.length()-1);
                                    //Log.d("tarja","urlListBeforeCreate= "+urlListCombined);
                                    CreateVoting(false);
                                }
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Log.d("Tarja","UPLOAD FAILURE");
                    }
                });
    }

    private void Initialize(){
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        tvIsAnonymous = rootView.findViewById(R.id.tvIsAnonymous);
        swIsAnonymous = rootView.findViewById(R.id.swIsAnonymous);
        tvIsCommentAllowed = rootView.findViewById(R.id.tvIsCommentAllowed);
        swIsCommentAllowed = rootView.findViewById(R.id.swIsCommentAllowed);

        swIsCommentAllowed.setIconEnabled(true);
        tvIsCommentAllowed.setText(getResources().getString(R.string.allowComments));
        swIsCommentAllowed.setImageDrawable(getResources().getDrawable(R.drawable.ic_comment_black_24dp));
        swIsCommentAllowed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swIsCommentAllowed.switchState();
                if (swIsCommentAllowed.isIconEnabled()){
                    tvIsCommentAllowed.setText(getResources().getString(R.string.allowComments));
                }
                else
                {
                    tvIsCommentAllowed.setText(getResources().getString(R.string.disallowComments));
                }
            }
        });

        tvIsAnonymous.setText(getResources().getString(R.string.shareAsAnonymous));
        tvIsAnonymous.setVisibility(View.INVISIBLE);
        swIsAnonymous.setIconEnabled(false);
        swIsAnonymous.setImageDrawable(getResources().getDrawable(R.drawable.ic_incognito));
        swIsAnonymous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swIsAnonymous.switchState();
                if (swIsAnonymous.isIconEnabled())
                {
                    tvIsAnonymous.setVisibility(View.VISIBLE);
                }
                else {
                    tvIsAnonymous.setVisibility(View.INVISIBLE);
                }
            }
        });
        infoDialog = new LovelyInfoDialog(getContext());
        infoDialog.setTopColorRes(R.color.colorPrimary);
        infoDialog.setIcon(R.drawable.ic_success_white);
        infoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        infoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        infoDialog.setTitle(getResources().getString(R.string.processSuccess));
        infoDialog.setMessage(getResources().getString(R.string.willPublishAfterApproval));

        bannedDialog = new LovelyStandardDialog(getContext());
        bannedDialog.setTopColorRes(R.color.real_Black);
        bannedDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
        bannedDialog.setIcon(R.drawable.ic_banned_white);
        bannedDialog.setTitle(getResources().getString(R.string.user_banned_title));
        bannedDialog.setMessage(getResources().getString(R.string.user_banned_message));
        bannedDialog.setCancelable(false);
        bannedDialog.setPositiveButton(getResources().getString(R.string.banned_ok), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomMenuActivity activity = (BottomMenuActivity) getActivity();
                activity.Logout();
            }
        });

        if (!loggedUser.getLoginView().isIsEmailApproved()){
            approveDialog = new LovelyStandardDialog(getContext());
            approveDialog.setTopColorRes(R.color.colorPrimary);
            approveDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
            approveDialog.setIcon(R.drawable.ic_user_times_solid_white);
            approveDialog.setPositiveButton(getResources().getString(R.string.goToSettings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingsFragment settingsFragment = new SettingsFragment();
                    FancyButton btBack = getActivity().findViewById(R.id.btBack);
                    btBack.setVisibility(View.VISIBLE);
                    getFragmentManager().beginTransaction()
                            .add(R.id.add_vote_container, settingsFragment,"home_container")
                            .addToBackStack(null)
                            .commit();
                }
            });
            approveDialog.setNegativeButton(getResources().getString(R.string.later),null);
            approveDialog.setTitle(getResources().getString(R.string.havent_verificated_email));
            approveDialog.setMessage(getResources().getString(R.string.go_settings_for_verification));

            if (loggedUser.getLoginView().getID() == 1){
                guestDialog = new LovelyStandardDialog(getContext());
                guestDialog.setTopColorRes(R.color.colorPrimary);
                guestDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
                guestDialog.setIcon(R.drawable.ic_user_times_solid_white);
                guestDialog.setPositiveButton(getResources().getString(R.string.login_or_register), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomMenuActivity activity = (BottomMenuActivity) getActivity();
                        activity.Logout();
                    }
                });
                guestDialog.setNegativeButton(getResources().getString(R.string.later),null);
                guestDialog.setTitle(getResources().getString(R.string.loginned_as_guest));
                guestDialog.setMessage(getResources().getString(R.string.go_login_or_register));
            }

        }

        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RoundedCorners(20));
        progressDialog = new LovelyProgressDialog(getContext());
        progressDialog
                .setIcon(R.drawable.ic_hourglass_white)
                .setTitle(getResources().getString(R.string.pleaseWait))
                .setCancelable(false)
                .setTopColorRes(R.color.colorPrimary);
        iv1 = rootView.findViewById(R.id.iv1);
        iv2 = rootView.findViewById(R.id.iv2);
        iv3 = rootView.findViewById(R.id.iv3);
        iv4 = rootView.findViewById(R.id.iv4);
        btAddPhoto = rootView.findViewById(R.id.btAddPhoto);
        btSave = rootView.findViewById(R.id.btSave);
        btSave.setEnabled(false);
        etDescription = rootView.findViewById(R.id.etDescription);
        loadedImageCount = 0;
        uploadedImageCount = 0;
        iv1Filled = false;
        iv2Filled = false;
        iv3Filled = false;
        iv4Filled = false;
        btDeleteiv1 = rootView.findViewById(R.id.btDeleteiv1);
        btDeleteiv2 = rootView.findViewById(R.id.btDeleteiv2);
        btDeleteiv3 = rootView.findViewById(R.id.btDeleteiv3);
        btDeleteiv4 = rootView.findViewById(R.id.btDeleteiv4);
        croppedImagePathList = new ArrayList<>();
        uploadedImagePathList = new ArrayList<>();
        options = new UCrop.Options();
        options.setCompressionQuality(50);
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setToolbarTitle(getResources().getString(R.string.editPhoto));
        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimary));
        options.setActiveWidgetColor(getResources().getColor(R.color.blue));
    }

    private boolean CheckPermissions(){

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ){
            return true;
        }
        String[] PERMISSIONS = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)  != PackageManager.PERMISSION_GRANTED){
            requestPermissions(PERMISSIONS,1000);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getContext().checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                getContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)  == PackageManager.PERMISSION_GRANTED){
            Pix.start(AddVoteFragment.this,1);
        }
    }

    private void InitializeOnClickListeners(){
        btAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (btAddPhoto.isClickable())
                if (CheckPermissions())
                    Pix.start(AddVoteFragment.this,1);
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if (loadedImageCount>=2)
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return;
                }
                StartUpload();
            }
        });
        btDeleteiv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getContext()).load(R.drawable.rounded_corner_multiline).into(iv1);
                loadedImageCount--;
                myImageList.get(0).setFileName("");
                myImageList.get(0).setFilePath("");
                myImageList.get(0).setFilled(false);
                btDeleteiv1.setVisibility(View.INVISIBLE);
                ImageDeleted();
            }
        });
        btDeleteiv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getContext()).load(R.drawable.rounded_corner_multiline).into(iv2);
                loadedImageCount--;
                myImageList.get(1).setFileName("");
                myImageList.get(1).setFilePath("");
                myImageList.get(1).setFilled(false);
                btDeleteiv2.setVisibility(View.INVISIBLE);
                ImageDeleted();
            }
        });
        btDeleteiv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getContext()).load(R.drawable.rounded_corner_multiline).into(iv3);
                loadedImageCount--;
                myImageList.get(2).setFileName("");
                myImageList.get(2).setFilePath("");
                myImageList.get(2).setFilled(false);
                btDeleteiv3.setVisibility(View.INVISIBLE);
                ImageDeleted();
            }
        });
        btDeleteiv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(getContext()).load(R.drawable.rounded_corner_multiline).into(iv4);
                loadedImageCount--;
                myImageList.get(3).setFileName("");
                myImageList.get(3).setFilePath("");
                myImageList.get(3).setFilled(false);
                btDeleteiv4.setVisibility(View.INVISIBLE);
                ImageDeleted();
            }
        });

        iv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myImageList.get(0).isFilled)
                    btAddPhoto.callOnClick();
            }
        });
        iv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myImageList.get(1).isFilled)
                    btAddPhoto.callOnClick();
            }
        });
        iv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myImageList.get(2).isFilled)
                    btAddPhoto.callOnClick();
            }
        });
        iv4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myImageList.get(3).isFilled)
                    btAddPhoto.callOnClick();
            }
        });
    }

    private void ImageViewClicked(){}

    private void CreateVoting(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_CreateVoting), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                basicResponse = gson.fromJson(response, myType);

                if (basicResponse.isIsSuccess()) {
                    if (mInterstitialAd.isLoaded())
                        mInterstitialAd.show();
                    progressDialog.dismiss();
                    //HomeFragment frag = new HomeFragment();
                    //getActivity().getSupportFragmentManager().beginTransaction()
                    //        .replace(R.id.content_frame,frag)
                    //        .addToBackStack(null)
                    //        .commit();

                    //Hawk.put("RefreshHome",true);                                     GAYET YARARLI
                    if (basicResponse.getMessage().equals("approval_is_active")){
                        infoDialog.show();
                    }
                    BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                    host.selectTabAtPosition(4,true);
                }
                else if(basicResponse.getMessage().equals("user_banned")){
                    bannedDialog.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                CreateVoting(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("isCommentAllowed", String.valueOf(swIsCommentAllowed.isIconEnabled()));
                params.put("URLListCombined", urlListCombined);
                params.put("Description", etDescription.getText().toString().replaceAll("\\s\\s","").replaceAll("\n",""));
                params.put("token", loggedUser.getToken());
                params.put("isAnonymous", String.valueOf(swIsAnonymous.isIconEnabled()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void ImageDeleted(){
        if(loadedImageCount < 4){
            btAddPhoto.setEnabled(true);
            if (loadedImageCount == 1){
                btSave.setEnabled(false);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK ){
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Uri source = Uri.fromFile(new File(returnValue.get(0)));
            destinationPath = getContext().getFilesDir().toString()+CreateFileName();
            //destinationPath = "/storage/emulated/0/DCIM/baran/IMG_6.jpg";
            Uri destination = Uri.fromFile(new File(destinationPath));
            Log.d("Tarja","dest= "+destinationPath);
            UCrop.of(source, destination)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(800,800)
                    .withOptions(options)
                    .start(getActivity(),AddVoteFragment.this,2);

        }
        else if(requestCode == 2 && resultCode == RESULT_OK){
            FillImageView(data);
            //if (fDelete.exists()){
            //    if(fDelete.delete()){
            //        Log.d("tarja","file Deleted : " +resultUri.getPath());
            //    }
            //    else{
            //        Log.d("tarja","file not Deleted : " +resultUri.getPath());
            //    }
            //}

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void FillImageView2(Intent data){
        Uri resultUri = UCrop.getOutput(data);
        if (!iv1Filled){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv1);
            Log.d("tarja","resultURI = "+resultUri.getPath());
            iv1Filled = true;
            iv1Path = resultUri.getPath();
            btDeleteiv1.setVisibility(View.VISIBLE);
        }
        else if(!iv2Filled){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv2);
            iv2Filled = true;
            iv2Path = resultUri.getPath();
            btDeleteiv2.setVisibility(View.VISIBLE);
        }
        else if(!iv3Filled){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv3);
            iv3Filled = true;
            iv3Path = resultUri.getPath();
            btDeleteiv3.setVisibility(View.VISIBLE);
        }
        else if(!iv4Filled){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv4);
            iv4Filled = true;
            iv4Path = resultUri.getPath();
            btDeleteiv4.setVisibility(View.VISIBLE);
        }
        loadedImageCount++;
        if (loadedImageCount >= 2)
        {
            btSave.setEnabled(true);
            if (loadedImageCount == 4)
            {
                btAddPhoto.setEnabled(false);
            }
        }

    }

    private void FillImageView(Intent data){
        Uri resultUri = UCrop.getOutput(data);
        Log.d("tarja","resultUri= "+resultUri.getPath());
        if (!myImageList.get(0).getFilled()){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv1);
            myImageList.get(0).setFilled(true);
            myImageList.get(0).setFilePath(resultUri.getPath());
            myImageList.get(0).setFileName(fileName);
            btDeleteiv1.setVisibility(View.VISIBLE);
        }
        else if(!myImageList.get(1).getFilled()){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv2);
            myImageList.get(1).setFilled(true);
            myImageList.get(1).setFilePath(resultUri.getPath());
            myImageList.get(1).setFileName(fileName);
            btDeleteiv2.setVisibility(View.VISIBLE);
        }
        else if(!myImageList.get(2).getFilled()){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv3);
            myImageList.get(2).setFilled(true);
            myImageList.get(2).setFilePath(resultUri.getPath());
            myImageList.get(2).setFileName(fileName);
            btDeleteiv3.setVisibility(View.VISIBLE);
        }
        else if(!myImageList.get(3).getFilled()){
            Glide.with(getContext()).load(resultUri.getPath()).apply(requestOptions).into(iv4);
            myImageList.get(3).setFilled(true);
            myImageList.get(3).setFilePath(resultUri.getPath());
            myImageList.get(3).setFileName(fileName);
            btDeleteiv4.setVisibility(View.VISIBLE);
        }
        loadedImageCount++;
        if (loadedImageCount >= 2)
        {
            btSave.setEnabled(true);
            if (loadedImageCount == 4)
            {
                btAddPhoto.setEnabled(false);
            }
        }

    }

    private String CreateFileName(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss:SSS");
        fileName = "/u"+String.valueOf(loggedUser.getLoginView().getID())+"_"+df.format(calendar.getTime())+".jpg";
        croppedImagePathList.add(getContext().getFilesDir().toString()+fileName);
        return  fileName;
    }

    private void DeleteImage(String path) {
        File fDelete = new File(path);
        if (fDelete.exists()) {
            if (fDelete.delete()) {
                Log.d("tarja", "file Deleted : " + path);
            } else {
                Log.d("tarja", "file not Deleted : " + path);
            }
        }
    }
}
