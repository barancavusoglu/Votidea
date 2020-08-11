package com.bcmobileappdevelopment.votidea.Fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.Activities.BottomMenuActivity;
import com.bcmobileappdevelopment.votidea.Adapters.ExploreAdapter;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetMasterVotingRecordsExploreResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.VoteResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.roughike.bottombar.BottomBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.BezierRadarHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import com.yarolegovich.lovelydialog.ViewConfigurator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import mehdi.sakout.fancybuttons.FancyButton;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExploreFragment extends Fragment {

    public ExploreFragment() {
    }

    RecyclerView recyclerView;
    Gson gson;
    GetMasterVotingRecordsExploreResponse getMasterVotingRecordsExploreResponse;
    BaseQuickAdapter mQuickAdapter;
    View rootview;
    Boolean firstTime,recordsFound, loadMoreRequested, refreshRequested, worldClicked, myCountryClicked, myCityClicked;
    Dialog myDialog;
    ImageView dialogImage, dialogDummyImage, btDummyUser, ivWorld, ivMyCountry, ivWorldFrame, ivMyCountryFrame, ivMyCity, ivMyCityFrame;
    RequestOptions requestOptions;
    RefreshLayout refresh;
    AppBarLayout appBarLayout;

    List<GetMasterVotingRecordsExploreResponse.MasterVotingExploreListBean> localMasterVotingExploreList, dataList;
    GetMasterVotingRecordsExploreResponse.MasterVotingExploreListBean clickedMasterVotingExplore;
    List<GetMasterVotingRecordsExploreResponse.MasterVotingExploreListBean.ChoiceListBean> clickedItemChoiceList;
    int guideID, clickedPosition, clickedImage, callCount;
    LovelyProgressDialog progressDialog;
    LovelyTextInputDialog textInputDialog;
    BasicResponse reportResponse;
    LovelyInfoDialog infoDialog;
    LoginResponse loggedUser;
    VoteResponse voteResponse;
    FancyButton btVote;
    String myCountry, myCity, mode;
    TextView tvMyCountry, tvMyCity;
    LovelyStandardDialog approveDialog, guestDialog;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_explore, container, false);
        Initialize();
        InitializeListeners();
        if (getContext() != null ){
            InitializeRecyclerView();
            GetVotingRecords(false);
        }
        return  rootview;
    }

    private void Initialize() {
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        callCount = 0;
        getMasterVotingRecordsExploreResponse = new GetMasterVotingRecordsExploreResponse();
        firstTime = true;
        recordsFound = false;
        loadMoreRequested = false;
        refreshRequested= false;
        dataList = new ArrayList<>();
        localMasterVotingExploreList = new ArrayList<>();
        progressDialog = new LovelyProgressDialog(getContext());
        progressDialog
                .setIcon(R.drawable.ic_hourglass_white)
                .setTitle(getResources().getString(R.string.pleaseWait))
                .setTopColorRes(R.color.colorPrimary);
        ViewConfigurator<EditText> viewConfigurator = new ViewConfigurator<EditText>() {
            @Override
            public void configureView(EditText v) {
                v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                v.setInputType(InputType.TYPE_CLASS_TEXT);
                v.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            }
        };
        textInputDialog = new LovelyTextInputDialog(getContext());
        textInputDialog.configureEditText(viewConfigurator);
        textInputDialog.setTopColor(getResources().getColor(R.color.colorPrimary));
        textInputDialog.setTitle(getResources().getString(R.string.reportReason));
        textInputDialog.setIcon(R.drawable.ic_report_white);
        textInputDialog.setTopTitleColor(getResources().getColor(R.color.white));
        textInputDialog.setHint(getResources().getString(R.string.optional));

        infoDialog = new LovelyInfoDialog(getContext());
        infoDialog.setTopColorRes(R.color.colorPrimary);
        infoDialog.setIcon(R.drawable.ic_success_white);
        infoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        infoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        infoDialog.setTitle(getResources().getString(R.string.processSuccess));
        infoDialog.setMessage(getResources().getString(R.string.recordReported));

        if (!loggedUser.getLoginView().isIsEmailApproved()){
            approveDialog = new LovelyStandardDialog(getContext());
            approveDialog.setTopColorRes(R.color.colorPrimary);
            approveDialog.setButtonsColor(getResources().getColor(R.color.colorPrimary));
            approveDialog.setIcon(R.drawable.ic_user_times_solid_white);
            approveDialog.setPositiveButton(getResources().getString(R.string.goToSettings), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myDialog.isShowing())
                        myDialog.dismiss();
                    SettingsFragment settingsFragment = new SettingsFragment();
                    FancyButton btBack = getActivity().findViewById(R.id.btBack);
                    btBack.setVisibility(View.VISIBLE);

                    BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                    if (host.getCurrentTabPosition() == 0){
                        getFragmentManager().beginTransaction()
                                .add(R.id.home_container, settingsFragment,"home_container")
                                .addToBackStack(null)
                                .commit();
                    }
                    else if(host.getCurrentTabPosition() == 1) {
                        getFragmentManager().beginTransaction()
                                .add(R.id.explore_container, settingsFragment,"home_container")
                                .addToBackStack(null)
                                .commit();
                    }
                    else if(host.getCurrentTabPosition() == 4) {
                        getFragmentManager().beginTransaction()
                                .add(R.id.profile_container, settingsFragment,"home_container")
                                .addToBackStack(null)
                                .commit();
                    }
                    else if(host.getCurrentTabPosition() == 3) {
                        getFragmentManager().beginTransaction()
                                .add(R.id.top_container, settingsFragment,"home_container")
                                .addToBackStack(null)
                                .commit();
                    }
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
                        if (myDialog.isShowing())
                            myDialog.dismiss();
                        BottomMenuActivity activity = (BottomMenuActivity) getActivity();
                        activity.Logout();
                    }
                });
                guestDialog.setNegativeButton(getResources().getString(R.string.later),null);
                guestDialog.setTitle(getResources().getString(R.string.loginned_as_guest));
                guestDialog.setMessage(getResources().getString(R.string.go_login_or_register));
            }
        }

        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.image_dialog);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogImage = myDialog.findViewById(R.id.ivDialogImage);
        dialogDummyImage = myDialog.findViewById(R.id.ivDummyImage);
        btVote = myDialog.findViewById(R.id.btVote);


        Window window = myDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RoundedCorners(50));
        localMasterVotingExploreList = new ArrayList<>();
        clickedItemChoiceList = new ArrayList<>();
        clickedMasterVotingExplore = new GetMasterVotingRecordsExploreResponse.MasterVotingExploreListBean();

        dialogDummyImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    myDialog.dismiss();
                return false;
            }
        });

        //dialogDummyImage.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        myDialog.dismiss();
        //    }
        //});
        guideID = 0;
        loadMoreRequested = false;
        recyclerView = rootview.findViewById(R.id.rvList);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        RefreshLayout refreshLayout = rootview.findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                refresh = refreshLayout;
                refreshRequested = true;
                RefreshRecyclerView();
            }
        });

        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColors(getResources().getColor(R.color.colorPrimaryDark),getResources().getColor(R.color.colorPrimary));
                return new BezierRadarHeader(context);
            }
        });
        btDummyUser = rootview.findViewById(R.id.btDummyUser);
        ivMyCountry = rootview.findViewById(R.id.ivMyCountry);
        ivMyCountryFrame = rootview.findViewById(R.id.ivMyCountryFrame);
        ivWorld = rootview.findViewById(R.id.ivWorld);
        ivWorldFrame = rootview.findViewById(R.id.ivWorldFrame);
        ivMyCity = rootview.findViewById(R.id.ivMyCity);
        ivMyCityFrame = rootview.findViewById(R.id.ivMyCityFrame);

        tvMyCity = rootview.findViewById(R.id.tvMyCity);
        tvMyCountry = rootview.findViewById(R.id.tvMyCountry);
        tvMyCountry.setText(loggedUser.getLoginView().getCountry());
        tvMyCity.setText(loggedUser.getLoginView().getCity());

        worldClicked = false;
        myCountryClicked = true;
        myCityClicked = false;
        myCountry = loggedUser.getLoginView().getCountry();
        myCity = loggedUser.getLoginView().getCity();
        mode = "country";

        Glide.with(getContext()).load(R.drawable.world_image).into(ivWorld);
        Glide.with(getContext()).load(getResources().getIdentifier(loggedUser.getLoginView().getFlagCode(), "drawable", getContext().getPackageName())).into(ivMyCountry);

    }

    public void RefreshLoggedInUser(){
        loggedUser = Hawk.get("loggedUser");
    }

    private void InitializeListeners() {
        btVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return;
                }
                Vote(clickedPosition, clickedMasterVotingExplore.getChoiceList().get(clickedImage-1).getID(), clickedMasterVotingExplore.getMasterVotingView().getVotingID(),clickedImage,false);
            }
        });
        btDummyUser.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    FancyButton btBack = getActivity().findViewById(R.id.btBack);
                    btBack.setVisibility(View.VISIBLE);
                    SearchFragment searchFragment = new SearchFragment();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.explore_container,searchFragment)
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            }

        });
        ivWorld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!worldClicked){
                    worldClicked = true;
                    myCountryClicked = false;
                    myCityClicked = false;
                    ivMyCountryFrame.setVisibility(View.GONE);
                    ivMyCityFrame.setVisibility(View.GONE);
                    ivWorldFrame.setVisibility(View.VISIBLE);
                    ivWorld.setAlpha(1f);
                    ivMyCity.setAlpha(.5f);
                    ivMyCountry.setAlpha(.5f);
                    mode = "world";
                    RefreshRecyclerView();
                }
            }
        });
        ivMyCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!myCountryClicked){
                    myCountryClicked = true;
                    worldClicked = false;
                    myCityClicked = false;
                    ivMyCountryFrame.setVisibility(View.VISIBLE);
                    ivWorldFrame.setVisibility(View.GONE);
                    ivMyCityFrame.setVisibility(View.GONE);
                    ivWorld.setAlpha(.5f);
                    ivMyCity.setAlpha(.5f);
                    ivMyCountry.setAlpha(1f);
                    myCountry = loggedUser.getLoginView().getCountry();
                    mode = "country";
                    RefreshRecyclerView();
                }
            }
        });
        ivMyCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!myCityClicked)
                {
                    myCountryClicked = false;
                    worldClicked = false;
                    myCityClicked = true;
                    ivMyCountryFrame.setVisibility(View.GONE);
                    ivWorldFrame.setVisibility(View.GONE);
                    ivMyCityFrame.setVisibility(View.VISIBLE);
                    ivWorld.setAlpha(.5f);
                    ivMyCity.setAlpha(1f);
                    ivMyCountry.setAlpha(.5f);
                    myCountry = loggedUser.getLoginView().getCountry();
                    myCity = loggedUser.getLoginView().getCity();
                    mode = "city";
                    RefreshRecyclerView();
                }
            }
        });
    }

    public void GoTop(){
        recyclerView.smoothScrollToPosition(0);
        appBarLayout = rootview.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true,true);
    }

    private void GetVotingRecords(final boolean useBackup) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetMasterVotingRecordsExplore), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetMasterVotingRecordsExploreResponse>() {
                }.getType();
                getMasterVotingRecordsExploreResponse = gson.fromJson(response, myType);
                if (getMasterVotingRecordsExploreResponse.isIsSuccess()) {
                    callCount++;
                    if (refreshRequested){
                        refreshRequested = false;
                        refresh.finishRefresh();
                    }
                    if (getMasterVotingRecordsExploreResponse.getMasterVotingExploreList().size() == 0){
                        if (loadMoreRequested){
                            loadMoreRequested = false;
                            mQuickAdapter.loadMoreEnd();
                        }
                    }
                    else {
                        dataList = getMasterVotingRecordsExploreResponse.getMasterVotingExploreList();
                        localMasterVotingExploreList.addAll(getMasterVotingRecordsExploreResponse.getMasterVotingExploreList());
                        if (firstTime){
                            guideID = getMasterVotingRecordsExploreResponse.getMasterVotingExploreList().get(0).getMasterVotingView().getVotingID();
                            InitializeRecyclerView();
                            //mQuickAdapter.addData(dataList);
                            firstTime = false;
                        }
                        if(loadMoreRequested){
                            loadMoreRequested = false;
                            mQuickAdapter.addData(dataList);
                            mQuickAdapter.loadMoreComplete();
                        }
                    }
                }
                if (localMasterVotingExploreList.size() == 0){
                    //ivNoData.setVisibility(View.VISIBLE);
                    //tvNoData.setVisibility(View.VISIBLE);
                    dataList = new ArrayList<>();
                    mQuickAdapter = new ExploreAdapter(R.layout.list_item_home_desc_up, dataList);
                    recyclerView.setAdapter(mQuickAdapter);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetVotingRecords(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("callCount", String.valueOf(callCount));
                params.put("guideID", String.valueOf(guideID));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("country", myCountry);
                params.put("city", myCity);
                params.put("mode", mode);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public void RefreshRecyclerView(){
        localMasterVotingExploreList = new ArrayList<>();
        clickedItemChoiceList = new ArrayList<>();
        clickedMasterVotingExplore = new GetMasterVotingRecordsExploreResponse.MasterVotingExploreListBean();
        guideID = 0;
        loadMoreRequested = false;
        callCount = 0;
        firstTime = true;
        GetVotingRecords(false);
    }

    private void InitializeRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuickAdapter = new ExploreAdapter(R.layout.list_item_home_desc_up, dataList);
        mQuickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mQuickAdapter.setEnableLoadMore(true);
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("tarja","OnLoadMoreBRVAH");

                        if (getContext() != null){
                            loadMoreRequested = true;
                            GetVotingRecords(false);
                        }
                    }
                },0 );
            }
        },recyclerView);



        mQuickAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return false;
                }

                clickedMasterVotingExplore = localMasterVotingExploreList.get(position);

                if (clickedMasterVotingExplore.getSelectedChoice() == 0){
                    if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                        Vote(position,clickedMasterVotingExplore.getChoiceList().get(0).getID(),clickedMasterVotingExplore.getMasterVotingView().getVotingID(),1,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                        Vote(position,clickedMasterVotingExplore.getChoiceList().get(1).getID(),clickedMasterVotingExplore.getMasterVotingView().getVotingID(),2,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedMasterVotingExplore.getMasterVotingView().getChoiceCount() >= 3){
                        Vote(position,clickedMasterVotingExplore.getChoiceList().get(2).getID(),clickedMasterVotingExplore.getMasterVotingView().getVotingID(),3,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedMasterVotingExplore.getMasterVotingView().getChoiceCount() == 4){
                        Vote(position,clickedMasterVotingExplore.getChoiceList().get(3).getID(),clickedMasterVotingExplore.getMasterVotingView().getVotingID(),4,false);
                    }
                }
                return true;
            }
        });

        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                clickedItemChoiceList = localMasterVotingExploreList.get(position).getChoiceList();

                clickedMasterVotingExplore = localMasterVotingExploreList.get(position);
                clickedPosition = position;
                btVote.setVisibility(View.GONE);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                    Glide.with(getContext()).load(clickedMasterVotingExplore.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedMasterVotingExplore.getSelectedChoice() == 0 && clickedMasterVotingExplore.getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 1;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                    Glide.with(getContext()).load(clickedMasterVotingExplore.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedMasterVotingExplore.getSelectedChoice() == 0 && clickedMasterVotingExplore.getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 2;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedMasterVotingExplore.getMasterVotingView().getChoiceCount() >= 3){
                    Glide.with(getContext()).load(clickedMasterVotingExplore.getChoiceList().get(2).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedMasterVotingExplore.getSelectedChoice() == 0 && clickedMasterVotingExplore.getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 3;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedMasterVotingExplore.getMasterVotingView().getChoiceCount() == 4){
                    Glide.with(getContext()).load(clickedMasterVotingExplore.getChoiceList().get(3).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedMasterVotingExplore.getSelectedChoice() == 0 && clickedMasterVotingExplore.getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 4;
                    }
                    myDialog.show();
                }
                else if ((view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/ivProfilePic") ||view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/tvNameSurname")) && !clickedMasterVotingExplore.getMasterVotingView().isIsAnonymous()){

                    if (loggedUser.getLoginView().getID() == (clickedMasterVotingExplore.getMasterVotingView().getOwnerUserID()))
                    {
                        BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                        host.selectTabAtPosition(4,true);
                    }
                    else{
                        //Hawk.init(getActivity()).build();
                        //Hawk.put("IsOtherProfileFragmentActive",true);
                        //getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag("home_fragment")).commit();
                        //Bundle args = new Bundle();
                        //args.putInt("UserID", clickedMasterVoting.getMasterVotingView().getOwnerUserID());
                        //OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                        //otherProfileFragment.setArguments(args);
                        //getActivity().getSupportFragmentManager().beginTransaction()
                        //        .add(R.id.content_frame,otherProfileFragment,"other_profile_fragment")
                        //        .addToBackStack(null)
                        //        .commit();
                        FancyButton btBack = getActivity().findViewById(R.id.btBack);
                        btBack.setVisibility(View.VISIBLE);
                        Bundle args = new Bundle();
                        args.putInt("UserID", clickedMasterVotingExplore.getMasterVotingView().getOwnerUserID());
                        OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                        otherProfileFragment.setArguments(args);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction().add(R.id.explore_container,otherProfileFragment, "other_profile_fragment")
                                .addToBackStack(null)
                                .commit();
                        //getFragmentManager().beginTransaction()
                        //        .add(R.id.home_container, otherProfileFragment)
                        //        .addToBackStack(null)
                        //        .commit();
                    }
                }
                else if(view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btVoteOptions")){
                    ShowSheetMenu();
                }
                else if(view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btComments") && clickedMasterVotingExplore.getMasterVotingView().isIsCommentAllowed()){
                    CommentsFragment commentsFragment = new CommentsFragment();
                    FancyButton btBack = getActivity().findViewById(R.id.btBack);
                    btBack.setVisibility(View.VISIBLE);
                    Bundle args = new Bundle();
                    args.putInt("VotingID", clickedMasterVotingExplore.getMasterVotingView().getVotingID());
                    args.putInt("VotingOwnerID", clickedMasterVotingExplore.getMasterVotingView().getOwnerUserID());
                    args.putInt("CommentCount", clickedMasterVotingExplore.getMasterVotingView().getCommentCount());
                    commentsFragment.setArguments(args);


                    //getFragmentManager().beginTransaction()
                    //        .add(R.id.home_container, commentsFragment)
                    //        .addToBackStack(null)
                    //        .commit();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.explore_container,commentsFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        recyclerView.setAdapter(mQuickAdapter);
    }

    private void ShowSheetMenu(){
        if (!loggedUser.getLoginView().isIsEmailApproved()){
            if (loggedUser.getLoginView().getID() == 1)
                guestDialog.show();
            else
                approveDialog.show();
            return;
        }
        SheetMenu sheetMenu = new SheetMenu();
        sheetMenu.setTitle(getResources().getString(R.string.options));
        sheetMenu.setAutoCancel(true);
        sheetMenu.setMenu(R.menu.other_profile_1);
        sheetMenu.setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getResources().getString(R.string.report))){
                    textInputDialog.setInitialInput("");
                    textInputDialog.setNegativeButton(getResources().getString(R.string.cancel),null);
                    textInputDialog.setConfirmButton(getResources().getString(R.string.ok), new LovelyTextInputDialog.OnTextInputConfirmListener() {
                        @Override
                        public void onTextInputConfirmed(String text) {
                            Report(text, clickedMasterVotingExplore.getMasterVotingView().getVotingID(),2,false);
                        }
                    });
                    textInputDialog.show();
                }
                return false;
            }
        });
        sheetMenu.show(getContext());
    }

    public void RefreshItem(int selectedChoiceOtherProfile, int votingID, VoteResponse voteResponse){
        for (int i = 0; i<localMasterVotingExploreList.size();i++){
            if (localMasterVotingExploreList.get(i).getMasterVotingView().getVotingID() == votingID){
                localMasterVotingExploreList.get(i).setSelectedChoice(selectedChoiceOtherProfile);

                switch (localMasterVotingExploreList.get(i).getChoiceList().size()){
                    case 2:
                        localMasterVotingExploreList.get(i).getChoiceList().get(0).setClickedCount(voteResponse.getChoiceList().get(0).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(0).setPercentage(voteResponse.getChoiceList().get(0).getPercentage());

                        localMasterVotingExploreList.get(i).getChoiceList().get(1).setClickedCount(voteResponse.getChoiceList().get(1).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(1).setPercentage(voteResponse.getChoiceList().get(1).getPercentage());
                        break;
                    case 3:
                        localMasterVotingExploreList.get(i).getChoiceList().get(0).setClickedCount(voteResponse.getChoiceList().get(0).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(0).setPercentage(voteResponse.getChoiceList().get(0).getPercentage());

                        localMasterVotingExploreList.get(i).getChoiceList().get(1).setClickedCount(voteResponse.getChoiceList().get(1).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(1).setPercentage(voteResponse.getChoiceList().get(1).getPercentage());

                        localMasterVotingExploreList.get(i).getChoiceList().get(2).setClickedCount(voteResponse.getChoiceList().get(2).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(2).setPercentage(voteResponse.getChoiceList().get(2).getPercentage());
                        break;
                    case 4:
                        localMasterVotingExploreList.get(i).getChoiceList().get(0).setClickedCount(voteResponse.getChoiceList().get(0).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(0).setPercentage(voteResponse.getChoiceList().get(0).getPercentage());

                        localMasterVotingExploreList.get(i).getChoiceList().get(1).setClickedCount(voteResponse.getChoiceList().get(1).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(1).setPercentage(voteResponse.getChoiceList().get(1).getPercentage());

                        localMasterVotingExploreList.get(i).getChoiceList().get(2).setClickedCount(voteResponse.getChoiceList().get(2).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(2).setPercentage(voteResponse.getChoiceList().get(2).getPercentage());

                        localMasterVotingExploreList.get(i).getChoiceList().get(3).setClickedCount(voteResponse.getChoiceList().get(3).getClickedCount());
                        localMasterVotingExploreList.get(i).getChoiceList().get(3).setPercentage(voteResponse.getChoiceList().get(3).getPercentage());
                        break;
                }
                mQuickAdapter.notifyDataSetChanged();
            }
        }
    }

    private void Vote(final int position, final int choiceID, final int votingID, final int selectedChoice, final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_Vote), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<VoteResponse>() {
                }.getType();
                voteResponse = gson.fromJson(response, myType);

                if (voteResponse.isIsSuccess()) {
                    VoteComplete(position, selectedChoice);
                    HomeFragment fg =(HomeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("home_fragment");
                    if (fg != null)
                        fg.RefreshItem(selectedChoice, votingID, voteResponse);
                }
                else if(!voteResponse.isIsSuccess() && voteResponse.getMessage().equals("duplicate_vote")){
                    AppearVote(position);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                Vote(position,choiceID,votingID,selectedChoice,true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("choiceID", String.valueOf(choiceID));
                params.put("votingID", String.valueOf(votingID));
                params.put("token", loggedUser.getToken());
                params.put("selectedChoice", String.valueOf(selectedChoice));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public void RefreshItemCommentCount(int votingID, int commentCount){
        for (int i = 0; i<localMasterVotingExploreList.size();i++){
            if (localMasterVotingExploreList.get(i).getMasterVotingView().getVotingID() == votingID){
                localMasterVotingExploreList.get(i).getMasterVotingView().setCommentCount(commentCount);
            }
            mQuickAdapter.notifyDataSetChanged();
        }
    }

    private void AppearVote(int position){
        Toasty.info(getContext(), getResources().getString(R.string.duplicate_vote), Toast.LENGTH_LONG, true).show();
        View viewToUpdate = mQuickAdapter.getViewByPosition(position,R.id.listItemHomeLayout);
        RoundCornerProgressBar pBar1,pBar2,pBar3,pBar4;
        TextView tv1Perc, tv2Perc,tv3Perc,tv4Perc;
        pBar1 = viewToUpdate.findViewById(R.id.pBar1);
        pBar2 = viewToUpdate.findViewById(R.id.pBar2);
        pBar3 = viewToUpdate.findViewById(R.id.pBar3);
        pBar4 = viewToUpdate.findViewById(R.id.pBar4);
        tv1Perc = viewToUpdate.findViewById(R.id.tv1perc);
        tv2Perc = viewToUpdate.findViewById(R.id.tv2perc);
        tv3Perc = viewToUpdate.findViewById(R.id.tv3perc);
        tv4Perc = viewToUpdate.findViewById(R.id.tv4perc);

        ImageView iv1Check, iv2Check, iv3Check, iv4Check;
        iv1Check = viewToUpdate.findViewById(R.id.iv1Check);
        iv2Check = viewToUpdate.findViewById(R.id.iv2Check);
        iv3Check = viewToUpdate.findViewById(R.id.iv3Check);
        iv4Check = viewToUpdate.findViewById(R.id.iv4Check);

        if (localMasterVotingExploreList.get(position).getChoiceList().size() == 2){
            int iv1Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(1).getPercentage();
            pBar1.setProgress((float)iv1Percentage);
            pBar2.setProgress((float)iv2Percentage);
            tv1Perc.setText("%"+String.valueOf(iv1Percentage));
            tv2Perc.setText("%"+String.valueOf(iv2Percentage));
            pBar1.setVisibility(View.VISIBLE);
            pBar2.setVisibility(View.VISIBLE);
            tv1Perc.setVisibility(View.VISIBLE);
            tv2Perc.setVisibility(View.VISIBLE);
            if (iv1Percentage > iv2Percentage){
                iv1Check.setVisibility(View.VISIBLE);
            }
            else if(iv2Percentage > iv1Percentage){
                iv2Check.setVisibility(View.VISIBLE);
            }
        }
        else if(localMasterVotingExploreList.get(position).getChoiceList().size() == 3){
            int iv1Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(2).getPercentage();
            pBar1.setProgress((float)iv1Percentage);
            pBar2.setProgress((float)iv2Percentage);
            pBar3.setProgress((float)iv3Percentage);
            tv1Perc.setText("%"+String.valueOf(iv1Percentage));
            tv2Perc.setText("%"+String.valueOf(iv2Percentage));
            tv3Perc.setText("%"+String.valueOf(iv3Percentage));
            pBar1.setVisibility(View.VISIBLE);
            pBar2.setVisibility(View.VISIBLE);
            pBar3.setVisibility(View.VISIBLE);
            tv1Perc.setVisibility(View.VISIBLE);
            tv2Perc.setVisibility(View.VISIBLE);
            tv3Perc.setVisibility(View.VISIBLE);
            if (iv1Percentage > iv2Percentage && iv1Percentage> iv3Percentage){
                iv1Check.setVisibility(View.VISIBLE);
            }
            else if (iv2Percentage > iv1Percentage && iv2Percentage> iv3Percentage){
                iv2Check.setVisibility(View.VISIBLE);
            }
            else if (iv3Percentage > iv1Percentage && iv3Percentage> iv2Percentage){
                iv3Check.setVisibility(View.VISIBLE);
            }
        }
        else if(localMasterVotingExploreList.get(position).getChoiceList().size() == 4){
            int iv1Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(2).getPercentage();
            int iv4Percentage = (int)localMasterVotingExploreList.get(position).getChoiceList().get(3).getPercentage();
            pBar1.setProgress((float)iv1Percentage);
            pBar2.setProgress((float)iv2Percentage);
            pBar3.setProgress((float)iv3Percentage);
            pBar4.setProgress((float)iv4Percentage);
            tv1Perc.setText("%"+String.valueOf(iv1Percentage));
            tv2Perc.setText("%"+String.valueOf(iv2Percentage));
            tv3Perc.setText("%"+String.valueOf(iv3Percentage));
            tv4Perc.setText("%"+String.valueOf(iv4Percentage));
            pBar1.setVisibility(View.VISIBLE);
            pBar2.setVisibility(View.VISIBLE);
            pBar3.setVisibility(View.VISIBLE);
            pBar4.setVisibility(View.VISIBLE);
            tv1Perc.setVisibility(View.VISIBLE);
            tv2Perc.setVisibility(View.VISIBLE);
            tv3Perc.setVisibility(View.VISIBLE);
            tv4Perc.setVisibility(View.VISIBLE);
            if (iv1Percentage > iv2Percentage && iv1Percentage > iv3Percentage && iv1Percentage > iv4Percentage){
                iv1Check.setVisibility(View.VISIBLE);
            }
            else if(iv2Percentage > iv1Percentage && iv2Percentage > iv3Percentage && iv2Percentage > iv4Percentage){
                iv2Check.setVisibility(View.VISIBLE);
            }
            else if(iv3Percentage > iv1Percentage && iv3Percentage > iv2Percentage && iv3Percentage > iv4Percentage){
                iv3Check.setVisibility(View.VISIBLE);
            }
            else if(iv4Percentage > iv1Percentage && iv4Percentage > iv2Percentage && iv4Percentage > iv3Percentage){
                iv4Check.setVisibility(View.VISIBLE);
            }
        }

        // Duplicate olduğuna dair toast ile uyar

        if(myDialog.isShowing()){
            myDialog.dismiss();
        }
    }

    private void VoteComplete(int position, int selectedChoice){
        View viewToUpdate = mQuickAdapter.getViewByPosition(position,R.id.listItemHomeLayout);
        RoundCornerProgressBar pBar1,pBar2,pBar3,pBar4;
        TextView tv1Perc, tv2Perc,tv3Perc,tv4Perc;
        pBar1 = viewToUpdate.findViewById(R.id.pBar1);
        pBar2 = viewToUpdate.findViewById(R.id.pBar2);
        pBar3 = viewToUpdate.findViewById(R.id.pBar3);
        pBar4 = viewToUpdate.findViewById(R.id.pBar4);
        tv1Perc = viewToUpdate.findViewById(R.id.tv1perc);
        tv2Perc = viewToUpdate.findViewById(R.id.tv2perc);
        tv3Perc = viewToUpdate.findViewById(R.id.tv3perc);
        tv4Perc = viewToUpdate.findViewById(R.id.tv4perc);

        FancyButton btVoteCount;
        btVoteCount = viewToUpdate.findViewById(R.id.btVoteCount);
        int voteCount = 0;

        ImageView ivLike1, ivLike2, ivLike3, ivLike4;
        ivLike1 = viewToUpdate.findViewById(R.id.ivLike1);
        ivLike2 = viewToUpdate.findViewById(R.id.ivLike2);
        ivLike3 = viewToUpdate.findViewById(R.id.ivLike3);
        ivLike4 = viewToUpdate.findViewById(R.id.ivLike4);

        ImageView iv1Check, iv2Check, iv3Check, iv4Check;
        iv1Check = viewToUpdate.findViewById(R.id.iv1Check);
        iv2Check = viewToUpdate.findViewById(R.id.iv2Check);
        iv3Check = viewToUpdate.findViewById(R.id.iv3Check);
        iv4Check = viewToUpdate.findViewById(R.id.iv4Check);

        if (localMasterVotingExploreList.get(position).getChoiceList().size() == 2){
            int iv1Percentage = (int)voteResponse.getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)voteResponse.getChoiceList().get(1).getPercentage();
            voteCount = voteResponse.getChoiceList().get(0).getClickedCount() + voteResponse.getChoiceList().get(1).getClickedCount();
            pBar1.setProgress((float)iv1Percentage);
            pBar2.setProgress((float)iv2Percentage);
            tv1Perc.setText("%"+String.valueOf(iv1Percentage));
            tv2Perc.setText("%"+String.valueOf(iv2Percentage));
            pBar1.setVisibility(View.VISIBLE);
            pBar2.setVisibility(View.VISIBLE);
            tv1Perc.setVisibility(View.VISIBLE);
            tv2Perc.setVisibility(View.VISIBLE);
            if (iv1Percentage > iv2Percentage){
                iv1Check.setVisibility(View.VISIBLE);
            }
            else if(iv2Percentage > iv1Percentage){
                iv2Check.setVisibility(View.VISIBLE);
            }
        }
        else if(localMasterVotingExploreList.get(position).getChoiceList().size() == 3){
            int iv1Percentage = (int)voteResponse.getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)voteResponse.getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)voteResponse.getChoiceList().get(2).getPercentage();
            voteCount = voteResponse.getChoiceList().get(0).getClickedCount() + voteResponse.getChoiceList().get(1).getClickedCount() + voteResponse.getChoiceList().get(2).getClickedCount();
            pBar1.setProgress((float)iv1Percentage);
            pBar2.setProgress((float)iv2Percentage);
            pBar3.setProgress((float)iv3Percentage);
            tv1Perc.setText("%"+String.valueOf(iv1Percentage));
            tv2Perc.setText("%"+String.valueOf(iv2Percentage));
            tv3Perc.setText("%"+String.valueOf(iv3Percentage));
            pBar1.setVisibility(View.VISIBLE);
            pBar2.setVisibility(View.VISIBLE);
            pBar3.setVisibility(View.VISIBLE);
            tv1Perc.setVisibility(View.VISIBLE);
            tv2Perc.setVisibility(View.VISIBLE);
            tv3Perc.setVisibility(View.VISIBLE);
            if (iv1Percentage > iv2Percentage && iv1Percentage> iv3Percentage){
                iv1Check.setVisibility(View.VISIBLE);
            }
            else if (iv2Percentage > iv1Percentage && iv2Percentage> iv3Percentage){
                iv2Check.setVisibility(View.VISIBLE);
            }
            else if (iv3Percentage > iv1Percentage && iv3Percentage> iv2Percentage){
                iv3Check.setVisibility(View.VISIBLE);
            }
        }
        else if(localMasterVotingExploreList.get(position).getChoiceList().size() == 4){
            int iv1Percentage = (int)voteResponse.getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)voteResponse.getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)voteResponse.getChoiceList().get(2).getPercentage();
            int iv4Percentage = (int)voteResponse.getChoiceList().get(3).getPercentage();
            voteCount = voteResponse.getChoiceList().get(0).getClickedCount() + voteResponse.getChoiceList().get(1).getClickedCount() + voteResponse.getChoiceList().get(2).getClickedCount() + voteResponse.getChoiceList().get(3).getClickedCount();
            pBar1.setProgress((float)iv1Percentage);
            pBar2.setProgress((float)iv2Percentage);
            pBar3.setProgress((float)iv3Percentage);
            pBar4.setProgress((float)iv4Percentage);
            tv1Perc.setText("%"+String.valueOf(iv1Percentage));
            tv2Perc.setText("%"+String.valueOf(iv2Percentage));
            tv3Perc.setText("%"+String.valueOf(iv3Percentage));
            tv4Perc.setText("%"+String.valueOf(iv4Percentage));
            pBar1.setVisibility(View.VISIBLE);
            pBar2.setVisibility(View.VISIBLE);
            pBar3.setVisibility(View.VISIBLE);
            pBar4.setVisibility(View.VISIBLE);
            tv1Perc.setVisibility(View.VISIBLE);
            tv2Perc.setVisibility(View.VISIBLE);
            tv3Perc.setVisibility(View.VISIBLE);
            tv4Perc.setVisibility(View.VISIBLE);
            if (iv1Percentage > iv2Percentage && iv1Percentage > iv3Percentage && iv1Percentage > iv4Percentage){
                iv1Check.setVisibility(View.VISIBLE);
            }
            else if(iv2Percentage > iv1Percentage && iv2Percentage > iv3Percentage && iv2Percentage > iv4Percentage){
                iv2Check.setVisibility(View.VISIBLE);
            }
            else if(iv3Percentage > iv1Percentage && iv3Percentage > iv2Percentage && iv3Percentage > iv4Percentage){
                iv3Check.setVisibility(View.VISIBLE);
            }
            else if(iv4Percentage > iv1Percentage && iv4Percentage > iv2Percentage && iv4Percentage > iv3Percentage){
                iv4Check.setVisibility(View.VISIBLE);
            }
        }

        localMasterVotingExploreList.get(position).setSelectedChoice(selectedChoice);
        btVoteCount.setText(String.valueOf(voteCount)+" "+getResources().getString(R.string.vote_count));

        switch (selectedChoice){
            case 1:
                ivLike1.setVisibility(View.VISIBLE);
                break;
            case 2:
                ivLike2.setVisibility(View.VISIBLE);
                break;
            case 3:
                ivLike3.setVisibility(View.VISIBLE);
                break;
            case 4:
                ivLike4.setVisibility(View.VISIBLE);
                break;
        }

        if(myDialog.isShowing()){
            myDialog.dismiss();
        }
    }

    private void Report(final String reason, final int itemID, final int reportTypeID, final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_Report), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                reportResponse = gson.fromJson(response, myType);
                progressDialog.dismiss();
                if (reportResponse.isIsSuccess()) {
                    infoDialog.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                Report(reason,itemID,reportTypeID,true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("reporterUserID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("itemID", String.valueOf(itemID));
                params.put("reason", reason);
                params.put("reportTypeID", String.valueOf(2)); // voting
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

}
