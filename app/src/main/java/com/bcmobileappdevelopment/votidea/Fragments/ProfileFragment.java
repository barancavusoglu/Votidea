package com.bcmobileappdevelopment.votidea.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.Adapters.ProfileVotingAdapter;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetProfileUserResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetVotingRecordsProfileResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.roughike.bottombar.BottomBar;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.BlurTransformation;
import mehdi.sakout.fancybuttons.FancyButton;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    ImageView ivProfile, ivProfileBlur,ivBlackened, ivSettings, ivCountry;
    RequestOptions requestOptions, requestOptionsProfile;
    View rootView;
    MultiTransformation multi;
    Gson gson;
    GetProfileUserResponse getProfileUserResponse;
    int callCount, guideID;
    Boolean loadMoreRequested, firstTime;
    TextView tvNameSurname, tvLocation, tvVoteCount, tvVotedCount, tvCommentCount, tvFollowingCount, tvFollowerCount, tvUsername;
    GetVotingRecordsProfileResponse getVotingRecordsProfileResponse;
    Dialog myDialog;
    ImageView dialogImage, dialogDummyImage;
    BasicResponse removeVotingResponse;
    LovelyProgressDialog progressDialog;
    LovelyInfoDialog infoDialog;
    Boolean isRepublishProcess;
    ConstraintLayout followingLayout,followerLayout;
    AppBarLayout appBarLayout;

    RecyclerView recyclerView;
    BaseQuickAdapter mQuickAdapter;
    List<GetVotingRecordsProfileResponse.ProfileVotingListBean> dataList, masterVotingList;
    GetVotingRecordsProfileResponse.ProfileVotingListBean clickedVoting;
    RequestQueue mRequestQueue;
    List<GetVotingRecordsProfileResponse.ProfileVotingListBean.ChoiceListBean> clickedItemChoiceList;
    GetVotingRecordsProfileResponse.ProfileVotingListBean clickedProfileVoting;

    FancyButton btVote;
    LoginResponse loggedUser;

    public ProfileFragment() {
    }

    @Override
    public void onDetach() {
        this.onDestroy();
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        Initialize();
        InitializeListeners();
        if(getContext() != null && isAdded()){
            LoadUserInformation();
        }

        //Glide.with(this).load("https://graph.facebook.com/10156126626689098/picture?type=large").apply(requestOptions).into(ivProfile);
        //Glide.with(this).load("https://graph.facebook.com/10156126626689098/picture?type=large")
        //        .apply(RequestOptions.bitmapTransform(multi))
        //        .into(ivProfileBlur);
        //Glide.with(this).load("https://cdn2.iconfinder.com/data/icons/world-flag-icons/128/Flag_of_Turkey.png").into(ivCountry);



        return rootView;
    }

    public void GoSettings(){
        ivSettings.callOnClick();
    }

    private void InitializeListeners() {
        ivSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedUser.getLoginView().getID()==1)
                    return;
                FancyButton btBack = getActivity().findViewById(R.id.btBack);
                btBack.setVisibility(View.VISIBLE);
                FollowFragment followFragment = new FollowFragment();
                Bundle args = new Bundle();
                args.putInt("UserID", loggedUser.getLoginView().getID());
                args.putString("Mode","following");
                followFragment.setArguments(args);
                BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                if (host.getCurrentTabPosition() == 0){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.home_container,followFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 1) {
                    getFragmentManager().beginTransaction()
                            .add(R.id.explore_container, followFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 4) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.profile_container,followFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 3) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.top_container,followFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        followerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loggedUser.getLoginView().getID()==1)
                    return;
                FancyButton btBack = getActivity().findViewById(R.id.btBack);
                btBack.setVisibility(View.VISIBLE);
                FollowFragment followFragment = new FollowFragment();
                Bundle args = new Bundle();
                args.putInt("UserID", loggedUser.getLoginView().getID());
                args.putString("Mode","follower");
                followFragment.setArguments(args);
                BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                if (host.getCurrentTabPosition() == 0){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.home_container,followFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 1) {
                    getFragmentManager().beginTransaction()
                            .add(R.id.explore_container, followFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 4) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.profile_container,followFragment)
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 3) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.top_container,followFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

    }

    public void GoTop(){
        recyclerView.smoothScrollToPosition(0);
        appBarLayout = rootView.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true,true);

    }

    public void InitializeRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuickAdapter = new ProfileVotingAdapter(R.layout.list_item_profile,dataList);
        mQuickAdapter.setEnableLoadMore(true);
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (getContext() != null){
                            loadMoreRequested = true;
                            GetProfileRecords(false);
                        }
                    }
                },0);
            }
        },recyclerView);

        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, final int position) {
                //clickedItemChoiceList = masterVotingList.get(position).getChoiceList();

                clickedProfileVoting = masterVotingList.get(position);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into(dialogImage);
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into(dialogImage);
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedProfileVoting.getProfileVoting().getChoiceCount() >= 3){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(2).getPictureURL()).apply(requestOptions).into(dialogImage);
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedProfileVoting.getProfileVoting().getChoiceCount() == 4){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(3).getPictureURL()).apply(requestOptions).into(dialogImage);
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btVoteOptions")){
                    ShowSheetMenu(position);
                }
                else if(view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btComments") && clickedProfileVoting.getProfileVoting().isIsCommentAllowed()){
                    CommentsFragment commentsFragment = new CommentsFragment();
                    FancyButton btBack = getActivity().findViewById(R.id.btBack);
                    btBack.setVisibility(View.VISIBLE);
                    Bundle args = new Bundle();
                    args.putInt("VotingID", clickedProfileVoting.getProfileVoting().getID());
                    args.putInt("VotingOwnerID", clickedProfileVoting.getProfileVoting().getOwnerUserID());
                    args.putInt("CommentCount", clickedProfileVoting.getProfileVoting().getCommentCount());
                    commentsFragment.setArguments(args);
                    //int count = Hawk.get("container_count");
                    //int count = Hawk.get("container_count");
                    //Hawk.put("container_count",++count);

                    BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                    if (host.getCurrentTabPosition() == 0){
                        //getFragmentManager().beginTransaction()
                        //        .add(R.id.home_container, commentsFragment,"home_container")
                        //        .commit();
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.home_container,commentsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    else if(host.getCurrentTabPosition() == 1) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.explore_container,commentsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    else if(host.getCurrentTabPosition() == 4) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.profile_container,commentsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                    else if(host.getCurrentTabPosition() == 3) {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.top_container,commentsFragment)
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });
        recyclerView.setAdapter(mQuickAdapter);
    }

    public void RefreshItemCommentCount(int votingID, int commentCount){
        for (int i = 0; i<masterVotingList.size();i++){
            if (masterVotingList.get(i).getProfileVoting().getID() == votingID){
                masterVotingList.get(i).getProfileVoting().setCommentCount(commentCount);
            }
            mQuickAdapter.notifyDataSetChanged();
        }
    }

    private void ShowSheetMenu(final int position){


        //SheetMenu.with(getContext())
        //        .setTitle(getResources().getString(R.string.options23))
        //        .setMenu(R.menu.options23)
        //        .setAutoCancel(true)
        //        .setClick(new MenuItem.OnMenuItemClickListener() {
        //            @Override
        //            public boolean onMenuItemClick(MenuItem item) {
        //                new LovelyStandardDialog(getContext(), LovelyStandardDialog.ButtonLayout.VERTICAL)
        //                        .setTopColorRes(R.color.blue)
        //                        .setButtonsColorRes(R.color.bb_darkBackgroundColor)
        //                        .setIcon(R.drawable.ic_delete_forever)
        //                        .setTitle("Title")
        //                        .setMessage("Message")
        //                        .setPositiveButton(android.R.string.ok, new View.OnClickListener() {
        //                            @Override
        //                            public void onClick(View v) {
        //                                progressDialog = new LovelyProgressDialog(getContext());
        //                                progressDialog
        //                                        .setIcon(R.drawable.ic_delete_forever)
        //                                        .setTitle("progress")
        //                                        .setTopColorRes(R.color.blue)
        //                                        .show();
        //                                RemoveVoting(clickedProfileVoting.getProfileVoting().getID(),position);
        //                            }
        //                        })
        //                        .setNegativeButton(android.R.string.no, null)
        //                        .show();
//
//
        //                return false;
        //            }
        //        }).show();

        SheetMenu sheetMenu = new SheetMenu();
        sheetMenu.setTitle(getResources().getString(R.string.options));
        sheetMenu.setAutoCancel(true);
        if (clickedProfileVoting.getProfileVoting().getStateID() == 1){
            sheetMenu.setMenu(R.menu.options1);
        }
        else if (clickedProfileVoting.getProfileVoting().getStateID() == 4){
            sheetMenu.setMenu(R.menu.options4);
        }
        else
            sheetMenu.setMenu(R.menu.options23);

        final LovelyStandardDialog dialog = new LovelyStandardDialog(getContext());
        dialog.setTopColorRes(R.color.colorPrimary);
        dialog.setButtonsColorRes(R.color.colorPrimary);
        sheetMenu.setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getResources().getString(R.string.delete))){

                    dialog.setIcon(R.drawable.ic_delete_forever_white);
                    dialog.setTitle(getResources().getString(R.string.sureToDelete));
                    dialog.setMessage(getResources().getString(R.string.cannotAccessDeleted));
                    dialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog = new LovelyProgressDialog(getContext());
                            progressDialog
                                    .setIcon(R.drawable.ic_hourglass_white)
                                    .setTitle(getResources().getString(R.string.pleaseWait))
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setCancelable(false)
                                    .show();
                            RemoveVoting(clickedProfileVoting.getProfileVoting().getID(),false);
                        }
                    });
                    dialog.setNegativeButton(getResources().getString(R.string.no),null);
                    dialog.show();
                }
                else if(item.getTitle().equals(getResources().getString(R.string.unpublish))){
                    dialog.setIcon(R.drawable.ic_unpublish_white);
                    dialog.setTitle(getResources().getString(R.string.sureToUnpublish));
                    dialog.setMessage(getResources().getString(R.string.canPublishLater));
                    dialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isRepublishProcess = false;
                            progressDialog = new LovelyProgressDialog(getContext());
                            progressDialog
                                    .setIcon(R.drawable.ic_republish_white)
                                    .setTitle(getResources().getString(R.string.pleaseWait))
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setCancelable(false)
                                    .show();
                            ChangePublishedState(clickedProfileVoting.getProfileVoting().getID(),false);
                        }
                    });
                    dialog.setNegativeButton(getResources().getString(R.string.no),null);
                    dialog.show();
                }
                else if(item.getTitle().equals(getResources().getString(R.string.republish))){
                    dialog.setIcon(R.drawable.ic_republish_white);
                    dialog.setTitle(getResources().getString(R.string.sureToRepublish));
                    dialog.setMessage(getResources().getString(R.string.canUnpublishLater));
                    dialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isRepublishProcess = true;
                            progressDialog = new LovelyProgressDialog(getContext());
                            progressDialog
                                    .setIcon(R.drawable.ic_republish_white)
                                    .setTitle(getResources().getString(R.string.pleaseWait))
                                    .setTopColorRes(R.color.colorPrimary)
                                    .setCancelable(false)

                                    .show();
                            ChangePublishedState(clickedProfileVoting.getProfileVoting().getID(),false);
                        }
                    });
                    dialog.setNegativeButton(getResources().getString(R.string.no),null);
                    dialog.show();
                }
                return false;
            }
        });
        sheetMenu.show(getContext());
    }

    private void RemoveVoting(final int votingID, final boolean useBackup){
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_RemoveVoting), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                removeVotingResponse = gson.fromJson(response, myType);
                progressDialog.dismiss();
                if (removeVotingResponse.isIsSuccess()) {
                    infoDialog.setIcon(R.drawable.ic_success_white);
                    infoDialog.setTitle(getResources().getString(R.string.processSuccess));
                    infoDialog.setMessage(getResources().getString(R.string.deleteSuccess));
                    infoDialog.show();
                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("profile_fragment")).commit();
                    getFragmentManager().beginTransaction().add(R.id.content_frame,new ProfileFragment(),"profile_fragment").commit();
                    //firstTime = true;
                    //guideID = 0;
                    //GetProfileRecords();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                RemoveVoting(votingID,true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("votingID", String.valueOf(votingID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void ChangePublishedState(final int votingID, final boolean useBackup){
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_ChangePublishedState), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                removeVotingResponse = gson.fromJson(response, myType);
                progressDialog.dismiss();

                if (removeVotingResponse.isIsSuccess()) {
                    infoDialog.setIcon(R.drawable.ic_success_white);
                    infoDialog.setTitle(getResources().getString(R.string.processSuccess));
                    if (isRepublishProcess)
                        infoDialog.setMessage(getResources().getString(R.string.republishSuccess));
                    else
                        infoDialog.setMessage(getResources().getString(R.string.unpublishSuccess));
                    infoDialog.show();

                    getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentByTag("profile_fragment")).commit();
                    getFragmentManager().beginTransaction().add(R.id.content_frame,new ProfileFragment(),"profile_fragment").commit();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                ChangePublishedState(votingID,true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("votingID", String.valueOf(votingID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void GetProfileRecords(final boolean useBackup) {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetVotingRecordsProfile), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetVotingRecordsProfileResponse>() {
                }.getType();
                getVotingRecordsProfileResponse = gson.fromJson(response, myType);
                if (getVotingRecordsProfileResponse.isIsSuccess()) {
                    try {
                        callCount++;
                        if (getVotingRecordsProfileResponse.getProfileVotingList().size() == 0) {
                            if (loadMoreRequested){
                                loadMoreRequested = false;
                                mQuickAdapter.loadMoreEnd();
                            }
                        }
                        else{
                            dataList = getVotingRecordsProfileResponse.getProfileVotingList();
                            masterVotingList.addAll(getVotingRecordsProfileResponse.getProfileVotingList());
                            if (firstTime){
                                guideID = getVotingRecordsProfileResponse.getProfileVotingList().get(0).getProfileVoting().getID();
                                InitializeRecyclerView();
                                firstTime = false;
                            }
                            if (loadMoreRequested){
                                loadMoreRequested = false;
                                mQuickAdapter.addData(dataList);
                                mQuickAdapter.loadMoreComplete();
                            }
                        }
                    }catch (Exception ex){

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetProfileRecords(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("callCount", String.valueOf(callCount));
                params.put("guideID", String.valueOf(guideID));
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("token", loggedUser.getToken());
                params.put("isMyProfile", "true");
                params.put("otherUserID", String.valueOf(loggedUser.getLoginView().getID()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void LoadUserInformation(){
        if (loggedUser.getLoginView().getID() == 1){
            Glide.with(getContext()).load(R.drawable.com_facebook_profile_picture_blank_square).apply(requestOptionsProfile).into(ivProfile);
            //Glide.with(getContext()).load("https://www.freevector.com/uploads/vector/preview/6044/FreeVector-Colorful-Shapes-Background.jpg")
            Glide.with(getContext()).load("https://firebasestorage.googleapis.com/v0/b/votidea-ef6e2.appspot.com/o/images%2FlogoWord.png?alt=media&token=b27cacc6-8469-4bf8-9859-6c778864d3fc")
                    //.apply(RequestOptions.bitmapTransform(multi))
                    .into(ivProfileBlur);
        }
        else {
            Glide.with(getContext()).load(loggedUser.getLoginView().getProfilePicURL()).apply(requestOptionsProfile).into(ivProfile);
            Glide.with(getContext()).load(loggedUser.getLoginView().getProfilePicURL())
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(ivProfileBlur);
            GetProfileRecords(false);
        }


        Glide.with(getContext()).load(getContext().getResources().getIdentifier(loggedUser.getLoginView().getFlagCode(), "drawable", getContext().getPackageName())).into(ivCountry);
        tvNameSurname.setText(loggedUser.getLoginView().getNameSurname());
        String location = loggedUser.getLoginView().getCity()+", "+loggedUser.getLoginView().getCountry();
        tvLocation.setText(location);
        tvVoteCount.setText(String.valueOf(loggedUser.getLoginView().getHasVoteCount()));
        tvVotedCount.setText(String.valueOf(loggedUser.getLoginView().getVotedCount()));
        tvCommentCount.setText(String.valueOf(loggedUser.getLoginView().getCommentCount()));
        tvUsername.setText("@"+loggedUser.getLoginView().getUsername());

        tvFollowerCount.setText(String.valueOf(loggedUser.getLoginView().getFollowerCount()));
        tvFollowingCount.setText(String.valueOf(loggedUser.getLoginView().getFollowingCount()));
    }

    //private void GetUserInformation() {
    //    mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
    //
    //    StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetProfileUser), new Response.Listener<String>() {
    //        @Override
    //        public void onResponse(String response) {
    //            GsonBuilder gsonBuilder = new GsonBuilder();
    //            gson = gsonBuilder.create();
    //            Type myType = new TypeToken<GetProfileUserResponse>() {
    //            }.getType();
    //            getProfileUserResponse = gson.fromJson(response, myType);
    //            if (getProfileUserResponse.isIsSuccess() ) {
    //                try{
    //                    Glide.with(getContext()).load(getProfileUserResponse.getUser().getProfilePicURL()).apply(requestOptionsProfile).into(ivProfile);
    //                    Glide.with(getContext()).load(getProfileUserResponse.getUser().getProfilePicURL())
    //                            .apply(RequestOptions.bitmapTransform(multi))
    //                            .into(ivProfileBlur);
    //                    Glide.with(getContext()).load(getContext().getResources().getIdentifier(getProfileUserResponse.getUser().getFlagCode(), "drawable", getContext().getPackageName())).into(ivCountry);
    //                    tvNameSurname.setText(getProfileUserResponse.getUser().getNameSurname());
    //                    String location = getProfileUserResponse.getUser().getCity()+", "+getProfileUserResponse.getUser().getCountry();
    //                    tvLocation.setText(location);
    //                    tvVoteCount.setText(String.valueOf(getProfileUserResponse.getUser().getHasVoteCount()));
    //                    tvVotedCount.setText(String.valueOf(getProfileUserResponse.getUser().getVotedCount()));
    //                    tvCommentCount.setText(String.valueOf(getProfileUserResponse.getUser().getCommentCount()));
    //                    GetProfileRecords();
    //                }catch(Exception ex) {
//
    //                }
    //            }
    //        }
    //    }, new Response.ErrorListener() {
    //        @Override
    //        public void onErrorResponse(VolleyError error) {
    //        }
    //    }) {
    //        @Override
    //        protected Map<String, String> getParams() {
    //            Map<String, String> params = new HashMap<String, String>();
    //            params.put("masterPass", getResources().getString(R.string.masterpass));
    //            params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
    //            params.put("token", loggedUser.getToken());
    //            return params;
    //        }
    //    };
    //    stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
    //    mRequestQueue.add(stringRequest);
    //}

    public void RefreshLoggedInUser(){
        loggedUser = Hawk.get("loggedUser");
    }

    private void Initialize(){
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        infoDialog = new LovelyInfoDialog(getContext());
        infoDialog.setTopColorRes(R.color.colorPrimary);
        infoDialog.setIcon(R.drawable.ic_success_white);
        infoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        infoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        infoDialog.setTitle(getResources().getString(R.string.processSuccess));
        //btHiddenProfile = rootView.findViewById(R.id.btHiddenProfile);
        //btHiddenProfile.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        infoDialog.setTitle(getResources().getString(R.string.secretProfileTitle));
        //        infoDialog.setMessage(getResources().getString(R.string.secretProfileMessage));
        //        infoDialog.setIcon(R.drawable.ic_unpublish_white);
        //        infoDialog.show();
        //    }
        //});
        ivSettings = rootView.findViewById(R.id.ivSettings);
        if (loggedUser.getLoginView().getID()==1)
            ivSettings.setVisibility(View.GONE);

        requestOptionsProfile = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        multi = new MultiTransformation(
                new BlurTransformation(50),
                //new BrightnessFilterTransformation(-0.25f),
                new CenterCrop()
        );
        ivProfile = rootView.findViewById(R.id.ivProfile);
        ivProfileBlur = rootView.findViewById(R.id.ivProfileBlur);
        ivBlackened = rootView.findViewById(R.id.ivBlackened);
        ivCountry = rootView.findViewById(R.id.ivCountry);
        callCount = 0;
        guideID = 0;
        loadMoreRequested = false;
        tvNameSurname = rootView.findViewById(R.id.tvNameSurname);
        tvLocation = rootView.findViewById(R.id.tvLocation);
        tvVoteCount = rootView.findViewById(R.id.tvVoteCount);
        tvVotedCount = rootView.findViewById(R.id.tvVotedCount);
        tvCommentCount = rootView.findViewById(R.id.tvCommentCount);
        tvUsername = rootView.findViewById(R.id.tvUsername);

        recyclerView = rootView.findViewById(R.id.rvList);
        firstTime = true;
        loadMoreRequested = false;
        clickedVoting = new GetVotingRecordsProfileResponse.ProfileVotingListBean();
        masterVotingList = new ArrayList<>();


        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RoundedCorners(50));

        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.image_dialog);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = myDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);
        clickedProfileVoting = new GetVotingRecordsProfileResponse.ProfileVotingListBean();
        dialogImage = myDialog.findViewById(R.id.ivDialogImage);
        dialogDummyImage = myDialog.findViewById(R.id.ivDummyImage);
        btVote = myDialog.findViewById(R.id.btVote);
        btVote.setVisibility(View.GONE);

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
        followingLayout = rootView.findViewById(R.id.followingLayout);
        followerLayout = rootView.findViewById(R.id.followerLayout);
        tvFollowingCount = rootView.findViewById(R.id.tvFollowingCount);
        tvFollowerCount = rootView.findViewById(R.id.tvFollowerCount);
    }
}