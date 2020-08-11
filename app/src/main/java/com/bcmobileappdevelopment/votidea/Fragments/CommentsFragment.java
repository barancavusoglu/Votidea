package com.bcmobileappdevelopment.votidea.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.Activities.BottomMenuActivity;
import com.bcmobileappdevelopment.votidea.Adapters.CommentAdapter;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetCommentsResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.R;
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
import com.yarolegovich.lovelydialog.LovelyStandardDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import com.yarolegovich.lovelydialog.ViewConfigurator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    public CommentsFragment() {
    }
    View rootview;
    int votingID, guideID, callCount, votingOwnerID, commentCount;
    Gson gson;
    GetCommentsResponse getCommentsResponse;
    boolean firstTime, loadMoreRequested,refreshRequested;
    RefreshLayout refresh;
    BaseQuickAdapter mQuickAdapter;
    List<GetCommentsResponse.CommentViewListBean> dataList, localCommentViewList;
    RefreshLayout refreshLayout;
    GetCommentsResponse.CommentViewListBean clickedCommentView;
    RecyclerView recyclerView;
    LoginResponse loggedUser;
    FancyButton btAddVote;
    BasicResponse commentResponse;
    EditText etComment;
    String commentWithEmoji;
    LovelyTextInputDialog reportDialog;
    BasicResponse reportResponse;
    ConstraintLayout list_item_comment_layout;
    GetCommentsResponse.CommentViewListBean clickedComment;
    LovelyInfoDialog infoDialog;
    BasicResponse removeCommentResponse;
    LovelyStandardDialog approveDialog,guestDialog,bannedDialog,deleteDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_comments, container, false);
        Initialize();
        InitializeListeners();
        if (getContext() != null ){
            GetComments(false);
            Log.d("tarja","getContext dolu");
        }
        return rootview;
    }

    private void InitializeListeners() {
        btAddVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return;
                }
                try {
                    if (!etComment.getText().toString().replaceAll("\\s","").replaceAll("\\n","").equals("")){
                        byte[] data = etComment.getText().toString().replaceAll("\\s\\s","").replaceAll("\\n","").getBytes("UTF-8");
                        commentWithEmoji = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
                        Comment(false);
                    }
                }
                catch (Exception e){
                }
            }
        });
    }

    private void Initialize() {
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        votingID = getArguments().getInt("VotingID");
        votingOwnerID = getArguments().getInt("VotingOwnerID");
        commentCount = getArguments().getInt("CommentCount");
        callCount = 0;
        guideID = 0;
        firstTime = true;
        loadMoreRequested = false;
        refreshRequested = false;
        recyclerView = rootview.findViewById(R.id.rvList);
        localCommentViewList = new ArrayList<>();
        dataList = new ArrayList<>();
        refreshLayout = rootview.findViewById(R.id.refreshLayout);
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
                layout.setPrimaryColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
                return new BezierRadarHeader(context);
            }
        });
        btAddVote = rootview.findViewById(R.id.btAddVote);
        etComment = rootview.findViewById(R.id.etComment);
        mQuickAdapter = new CommentAdapter(R.layout.list_item_comment, dataList);

        ViewConfigurator<EditText> viewConfigurator = new ViewConfigurator<EditText>() {
            @Override
            public void configureView(EditText v) {
                v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                v.setInputType(InputType.TYPE_CLASS_TEXT);
                v.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            }
        };
        reportDialog = new LovelyTextInputDialog(getContext());
        reportDialog.configureEditText(viewConfigurator);
        reportDialog.setTopColor(getResources().getColor(R.color.colorPrimary));
        reportDialog.setTitle(getResources().getString(R.string.reportReason));
        reportDialog.setIcon(R.drawable.ic_report_white);
        reportDialog.setTopTitleColor(getResources().getColor(R.color.white));
        reportDialog.setHint(getResources().getString(R.string.optional));

        infoDialog = new LovelyInfoDialog(getContext());
        infoDialog.setTopColorRes(R.color.colorPrimary);
        infoDialog.setIcon(R.drawable.ic_success_white);
        infoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        infoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        infoDialog.setTitle(getResources().getString(R.string.processSuccess));
        infoDialog.setMessage(getResources().getString(R.string.recordReported));

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
                        BottomMenuActivity activity = (BottomMenuActivity) getActivity();
                        activity.Logout();
                    }
                });
                guestDialog.setNegativeButton(getResources().getString(R.string.later),null);
                guestDialog.setTitle(getResources().getString(R.string.loginned_as_guest));
                guestDialog.setMessage(getResources().getString(R.string.go_login_or_register));
            }

        }

        deleteDialog = new LovelyStandardDialog(getContext());
        deleteDialog.setTopColorRes(R.color.colorPrimary);
        deleteDialog.setButtonsColorRes(R.color.colorPrimary);
        deleteDialog.setTopTitleColor(getResources().getColor(R.color.colorPrimary));
        deleteDialog.setIcon(R.drawable.ic_delete_forever_white);
        deleteDialog.setTitle(getResources().getString(R.string.sureToDelete));
        deleteDialog.setMessage(getResources().getString(R.string.cannotAccessDeleted));
    }
    public void RefreshRecyclerView(){
        localCommentViewList = new ArrayList<>();
        //clickedItemChoiceList = new ArrayList<>();
        clickedCommentView = new GetCommentsResponse.CommentViewListBean();
        guideID = 0;
        loadMoreRequested = false;
        callCount = 0;
        firstTime = true;
        //btGoTop.setVisibility(View.GONE);
        GetComments(false);
    }

    private void GetComments(final boolean useBackup)
    {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetComments), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetCommentsResponse>() {
                }.getType();
                getCommentsResponse = gson.fromJson(response, myType);
                if (getCommentsResponse.isIsSuccess()) {
                    callCount++;
                    if (refreshRequested){
                        refreshRequested = false;
                        refresh.finishRefresh();
                    }
                    if (getCommentsResponse.getCommentViewList().size() == 0){
                        if (loadMoreRequested){
                            loadMoreRequested = false;
                            mQuickAdapter.loadMoreEnd();
                            //btGoTop.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        dataList = getCommentsResponse.getCommentViewList();
                        localCommentViewList.addAll(getCommentsResponse.getCommentViewList());
                        if (firstTime){
                            guideID = getCommentsResponse.getCommentViewList().get(0).getID();
                            InitializeRecyclerView();
                            firstTime = false;
                        }
                        if(loadMoreRequested){
                            loadMoreRequested = false;
                            mQuickAdapter.addData(dataList);
                            mQuickAdapter.loadMoreComplete();
                        }
                    }
                }

                if (localCommentViewList.size() == 0){
                    //ivNoData.setVisibility(View.VISIBLE);
                    //tvNoData.setVisibility(View.VISIBLE);
                    dataList = new ArrayList<>();
                    mQuickAdapter = new CommentAdapter(R.layout.list_item_comment, dataList);
                    recyclerView.setAdapter(mQuickAdapter);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetComments(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("votingID", String.valueOf(votingID));
                params.put("guideID", String.valueOf(guideID));
                params.put("callCount", String.valueOf(callCount));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public void RefreshLoggedInUser(){
        loggedUser = Hawk.get("loggedUser");
    }

    private void Comment(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        btAddVote.setActivated(false);
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_Comment), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                commentResponse = gson.fromJson(response, myType);
                if (commentResponse.isIsSuccess()) {
                    commentCount++;
                    HomeFragment fg =(HomeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("home_fragment");
                    if (fg != null)
                        fg.RefreshItemCommentCount(votingID, commentCount);
                    ExploreFragment ef =(ExploreFragment) getActivity().getSupportFragmentManager().findFragmentByTag("explore_fragment");
                    if (ef != null)
                        ef.RefreshItemCommentCount(votingID, commentCount);
                    ProfileFragment pf =(ProfileFragment) getActivity().getSupportFragmentManager().findFragmentByTag("profile_fragment");
                    if (pf != null)
                        pf.RefreshItemCommentCount(votingID, commentCount);
                    OtherProfileFragment opf =(OtherProfileFragment) getActivity().getSupportFragmentManager().findFragmentByTag("other_profile_fragment");
                    if (opf != null)
                        opf.RefreshItemCommentCount(votingID, commentCount);

                    GetCommentsResponse.CommentViewListBean newComment = new GetCommentsResponse.CommentViewListBean();
                    newComment.setDescription(commentWithEmoji);
                    newComment.setProfilePicURL(loggedUser.getLoginView().getProfilePicURL());
                    newComment.setUsername(loggedUser.getLoginView().getUsername());
                    newComment.setDate(getResources().getString(R.string.now));
                    newComment.setUserID(loggedUser.getLoginView().getID());
                    List<GetCommentsResponse.CommentViewListBean> dummyList = new ArrayList<>();
                    dummyList.add(newComment);
                    localCommentViewList.add(newComment);
                    mQuickAdapter.addData(dummyList);
                    recyclerView.smoothScrollToPosition(0);
                    etComment.setText("");
                    btAddVote.setActivated(true);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootview.getWindowToken(), 0);
                    if (localCommentViewList.size() <=1){
                        refresh = refreshLayout;
                        refreshRequested = true;
                        RefreshRecyclerView();
                    }
                }
                else if(commentResponse.getMessage().equals("user_banned")){
                    bannedDialog.show();
                    Log.d("tarja","banned");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                Comment(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("votingID", String.valueOf(votingID));
                params.put("description", commentWithEmoji);
                params.put("descriptionMeaning", etComment.getText().toString().replaceAll("\\s\\s",""));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void InitializeRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuickAdapter = new CommentAdapter(R.layout.list_item_comment, dataList);
        mQuickAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        mQuickAdapter.setEnableLoadMore(true);
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getContext() != null){
                            loadMoreRequested = true;
                            GetComments(false);
                        }
                    }
                },0 );
            }
        },recyclerView);

        mQuickAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/list_item_comment_layout")){
                    list_item_comment_layout = view.findViewById(R.id.list_item_comment_layout);
                    list_item_comment_layout.setBackgroundColor(getResources().getColor(R.color.light_grey));
                    clickedComment = localCommentViewList.get(position);
                    if (votingOwnerID == loggedUser.getLoginView().getID() && clickedComment.getUserID() != loggedUser.getLoginView().getID()){ // oylama benim, yorum başkasının
                        ShowSheetMenu(R.menu.delete_report);
                    }
                    else if (clickedComment.getUserID() == loggedUser.getLoginView().getID()){ //oylama benim
                        ShowSheetMenu(R.menu.delete);
                    }
                    else{   // oylama da yorum da başkasının
                        ShowSheetMenu(R.menu.report);
                    }
                    Log.d("tarja","longclick");
                }
                return true;
            }
        });

        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                clickedCommentView = localCommentViewList.get(position);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/ivProfilePic") || view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/tvUsernameButton")){

                    if (loggedUser.getLoginView().getID() == (clickedCommentView.getUserID()))
                    {
                        BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                        ProfileFragment profileFragment = new ProfileFragment();
                        if (host.getCurrentTabPosition() == 0){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.home_container,profileFragment, "profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if (host.getCurrentTabPosition() == 1){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.explore_container,profileFragment, "profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if (host.getCurrentTabPosition() == 4){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.profile_container,profileFragment, "profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if (host.getCurrentTabPosition() == 3){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.top_container,profileFragment, "profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                    else {                                                           // geri tuşuna 1 kere basınca diğer fragmentler kaybolacak, home a dönecek
                        Bundle args = new Bundle();
                        args.putInt("UserID", clickedCommentView.getUserID());
                        OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                        otherProfileFragment.setArguments(args);

                        BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                        if (host.getCurrentTabPosition() == 0){
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .add(R.id.home_container,otherProfileFragment,"other_profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if (host.getCurrentTabPosition() == 1){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.explore_container,otherProfileFragment,"other_profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if(host.getCurrentTabPosition() == 4) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .add(R.id.profile_container,otherProfileFragment,"other_profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if (host.getCurrentTabPosition() == 3){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.top_container,otherProfileFragment,"other_profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(mQuickAdapter);
    }

    private void ShowSheetMenu(int menu){
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
        sheetMenu.setMenu(menu);

        sheetMenu.setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getResources().getString(R.string.report))){
                    reportDialog.setInitialInput("");
                    reportDialog.setNegativeButton(getResources().getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            list_item_comment_layout.setBackgroundColor(getResources().getColor(R.color.home_cardview));
                        }
                    });
                    reportDialog.setConfirmButton(getResources().getString(R.string.ok), new LovelyTextInputDialog.OnTextInputConfirmListener() {
                        @Override
                        public void onTextInputConfirmed(String text) {
                            Report(text,clickedComment.getID(),3,false);
                        }
                    });
                    reportDialog.show();
                }

                else if (item.getTitle().equals(getResources().getString(R.string.delete))){
                    deleteDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d("tarja","siliyorum inş");
                            RemoveComment(false);
                        }
                    });
                    deleteDialog.setNegativeButton(getResources().getString(R.string.no), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            list_item_comment_layout.setBackgroundColor(getResources().getColor(R.color.home_cardview));
                        }
                    });
                    deleteDialog.show();

                }
                return false;
            }
        });
        sheetMenu.show(getContext());
    }

    private void RemoveComment(final boolean useBackup) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_RemoveComment), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                removeCommentResponse = gson.fromJson(response, myType);
                if (removeCommentResponse.isIsSuccess()) {
                    commentCount--;

                    HomeFragment fg =(HomeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("home_fragment");
                    if (fg != null)
                        fg.RefreshItemCommentCount(votingID, commentCount);
                    ExploreFragment ef =(ExploreFragment) getActivity().getSupportFragmentManager().findFragmentByTag("explore_fragment");
                    if (ef != null)
                        ef.RefreshItemCommentCount(votingID, commentCount);
                    ProfileFragment pf =(ProfileFragment) getActivity().getSupportFragmentManager().findFragmentByTag("profile_fragment");
                    if (pf != null)
                        pf.RefreshItemCommentCount(votingID, commentCount);
                    OtherProfileFragment opf =(OtherProfileFragment) getActivity().getSupportFragmentManager().findFragmentByTag("other_profile_fragment");
                    if (opf != null)
                        opf.RefreshItemCommentCount(votingID, commentCount);

                    refresh = refreshLayout; ///TEMP
                    refreshRequested = true;  ///TEMP
                    RefreshRecyclerView();  ///TEMP
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                RemoveComment(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("commentID", String.valueOf(clickedComment.getID()));
                params.put("commentOwnerID", String.valueOf(clickedComment.getUserID()));
                params.put("votingID", String.valueOf(clickedComment.getVotingID()));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
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
                if (reportResponse.isIsSuccess()) {
                    infoDialog.show();
                    list_item_comment_layout.setBackgroundColor(getResources().getColor(R.color.home_cardview));
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
                params.put("reportTypeID", String.valueOf(reportTypeID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

}