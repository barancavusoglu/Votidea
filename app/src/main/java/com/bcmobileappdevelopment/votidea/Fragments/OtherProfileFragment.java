package com.bcmobileappdevelopment.votidea.Fragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import com.bcmobileappdevelopment.votidea.Adapters.OtherProfileVotingAdapter;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetProfileUserResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetVotingRecordsProfileResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.VoteResponse;
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
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;
import com.yarolegovich.lovelydialog.ViewConfigurator;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import mehdi.sakout.fancybuttons.FancyButton;
import ru.whalemare.sheetmenu.SheetMenu;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherProfileFragment extends Fragment {

    ImageView ivProfile, ivProfileBlur,ivBlackened, ivGreyCircleOptions,ivOptions, ivCountry;
    RequestOptions requestOptions, requestOptionsProfile;
    View rootView;
    MultiTransformation multi;
    Gson gson;
    GetProfileUserResponse getProfileUserResponse;
    int callCount, guideID, otherUserID, clickedPosition, clickedImage;
    Boolean loadMoreRequested, firstTime;
    TextView tvNameSurname, tvLocation, tvVoteCount, tvVotedCount, tvCommentCount,tvFollowerCount,tvFollowingCount, tvUsername;
    GetVotingRecordsProfileResponse getVotingRecordsProfileResponse;
    ConstraintLayout followingLayout,followerLayout;

    RecyclerView recyclerView;
    BaseQuickAdapter mQuickAdapter;
    List<GetVotingRecordsProfileResponse.ProfileVotingListBean> dataList, masterVotingList;
    GetVotingRecordsProfileResponse.ProfileVotingListBean clickedVoting;
    RequestQueue mRequestQueue;

    LovelyProgressDialog progressDialog;
    GetVotingRecordsProfileResponse.ProfileVotingListBean clickedProfileVoting;
    Dialog myDialog;
    ImageView dialogImage, dialogDummyImage, ivSendMessage;
    LovelyTextInputDialog textInputDialog;
    LovelyInfoDialog infoDialog;
    BasicResponse reportResponse, followResponse;
    LoginResponse loggedUser;
    FancyButton btFollow, btFollowing, btVote;
    LovelyStandardDialog confirmationDialog;
    VoteResponse voteResponse;
    LovelyStandardDialog approveDialog, guestDialog;



    public OtherProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_other_profile, container, false);
        Initialize();
        InitializeListeners();

        if(getContext() != null && isAdded()){
            GetUserInformation(false);
        }
        //if(getFragmentManager().findFragmentByTag("home_fragment") != null){
        //    getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag("home_fragment")).commit();
        //}

        //Glide.with(this).load("https://graph.facebook.com/10156126626689098/picture?type=large").apply(requestOptions).into(ivProfile);
        //Glide.with(this).load("https://graph.facebook.com/10156126626689098/picture?type=large")
        //        .apply(RequestOptions.bitmapTransform(multi))
        //        .into(ivProfileBlur);
        //Glide.with(this).load("https://cdn2.iconfinder.com/data/icons/world-flag-icons/128/Flag_of_Turkey.png").into(ivCountry);

        return rootView;
    }

    private void InitializeListeners() {
        ivSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return;
                }
                FancyButton btBack = getActivity().findViewById(R.id.btBack);
                btBack.setVisibility(View.VISIBLE);
                DialogFragment dialogFragment = new DialogFragment();
                Bundle args = new Bundle();
                args.putString("SenderID", String.valueOf(otherUserID));
                args.putString("FromUsername",getProfileUserResponse.getUser().getUsername());
                args.putString("ProfilePic",getProfileUserResponse.getUser().getProfilePicURL());
                dialogFragment.setArguments(args);

                BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                if (host.getCurrentTabPosition() == 0){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.home_container,dialogFragment,"dialog_fragment")
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 1) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.explore_container,dialogFragment,"dialog_fragment")
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 4) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.profile_container,dialogFragment,"dialog_fragment")
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 3) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.top_container,dialogFragment,"dialog_fragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        btFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return;
                }
                Follow(false);

            }
        });
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
                Vote(clickedPosition, clickedProfileVoting.getChoiceList().get(clickedImage-1).getID(), clickedProfileVoting.getProfileVoting().getID(),clickedImage,false);

            }
        });
        followingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return;
                }
                FancyButton btBack = getActivity().findViewById(R.id.btBack);
                btBack.setVisibility(View.VISIBLE);
                FollowFragment followFragment = new FollowFragment();
                Bundle args = new Bundle();
                args.putInt("UserID", otherUserID);
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
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.explore_container,followFragment)
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
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return;
                }
                FancyButton btBack = getActivity().findViewById(R.id.btBack);
                btBack.setVisibility(View.VISIBLE);
                FollowFragment followFragment = new FollowFragment();
                Bundle args = new Bundle();
                args.putInt("UserID", otherUserID);
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
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.explore_container,followFragment)
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

    @Override
    public void onDetach() {
        this.onDestroy();
        super.onDetach();
    }

    public void RefreshLoggedInUser(){
        loggedUser = Hawk.get("loggedUser");
    }

    public void InitializeRecyclerView(){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuickAdapter = new OtherProfileVotingAdapter(R.layout.list_item_other_profile,dataList);
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
                clickedProfileVoting = masterVotingList.get(position);
                Log.d("tarja","long click geldi" + position);
                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                    Vote(position,clickedProfileVoting.getChoiceList().get(0).getID(),clickedProfileVoting.getProfileVoting().getID(),1,false);
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                    Vote(position,clickedProfileVoting.getChoiceList().get(1).getID(),clickedProfileVoting.getProfileVoting().getID(),2,false);
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedProfileVoting.getProfileVoting().getChoiceCount() >= 3){
                    Vote(position,clickedProfileVoting.getChoiceList().get(2).getID(),clickedProfileVoting.getProfileVoting().getID(),3,false);
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedProfileVoting.getProfileVoting().getChoiceCount() == 4){
                    Log.d("tarja", "tamam 4 e girdi");
                    Vote(position,clickedProfileVoting.getChoiceList().get(3).getID(),clickedProfileVoting.getProfileVoting().getID(),4,false);
                }

                return true;
            }
        });

        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                //clickedItemChoiceList = masterVotingList.get(position).getChoiceList();

                clickedProfileVoting = masterVotingList.get(position);
                btVote.setVisibility(View.GONE);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(0).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedProfileVoting.getSelectedChoice() == 0){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 1;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(1).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedProfileVoting.getSelectedChoice() == 0){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 2;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedProfileVoting.getProfileVoting().getChoiceCount() >= 3){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(2).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedProfileVoting.getSelectedChoice() == 0){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 3;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedProfileVoting.getProfileVoting().getChoiceCount() == 4){
                    Glide.with(getContext()).load(clickedProfileVoting.getChoiceList().get(3).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedProfileVoting.getSelectedChoice() == 0){
                        btVote.setVisibility(View.VISIBLE);
                        clickedPosition = position;
                        clickedImage = 4;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btVoteOptions")){
                    ShowSheetMenu(false);
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

                    BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                    if (host.getCurrentTabPosition() == 0){
                        //getFragmentManager().beginTransaction()
                        //        .add(R.id.home_container, commentsFragment)
                        //        .addToBackStack(null)
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
                        //getFragmentManager().beginTransaction()
                        //        .add(R.id.profile_container, commentsFragment)
                        //        .addToBackStack(null)
                        //        .commit();
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

    private void ShowSheetMenu(final Boolean isProfileReportedOnly){
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
        if (btFollow.getVisibility() == View.GONE && isProfileReportedOnly){
            sheetMenu.setMenu(R.menu.other_profile_2); // report
        }
        else {
            sheetMenu.setMenu(R.menu.other_profile_1); // report & unfollow
        }
        sheetMenu.setClick(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals(getResources().getString(R.string.report))){
                    textInputDialog.setInitialInput("");
                    textInputDialog.setNegativeButton(getResources().getString(R.string.cancel),null);
                    textInputDialog.setConfirmButton(getResources().getString(R.string.ok), new LovelyTextInputDialog.OnTextInputConfirmListener() {
                        @Override
                        public void onTextInputConfirmed(String text) {
                            progressDialog.show();
                            if (isProfileReportedOnly){
                                Report(text,otherUserID,1,false);
                            }
                            else
                                Report(text,otherUserID,2,false);
                        }
                    });
                    textInputDialog.show();
                }

                else if (item.getTitle().equals(getResources().getString(R.string.unfollow))){
                    confirmationDialog.setIcon(R.drawable.ic_user_times_solid_white);
                    confirmationDialog.setTitle(getResources().getString(R.string.unfollow));
                    confirmationDialog.setMessage(getResources().getString(R.string.sureToUnfollow));
                    confirmationDialog.setTopColorRes(R.color.colorPrimary);
                    confirmationDialog.setButtonsColorRes(R.color.colorPrimary);
                    confirmationDialog.setNegativeButton(getResources().getString(R.string.no),null);
                    confirmationDialog.setPositiveButton(getResources().getString(R.string.yes), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Unfollow(false);
                        }
                    });
                    confirmationDialog.show();

                }
                return false;
            }
        });
        sheetMenu.show(getContext());
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
                    //if (fg != null)
                    //    fg.RefreshItem(voteResponse,selectedChoice);
                    if (fg != null)
                        fg.RefreshItem(selectedChoice, votingID, voteResponse);

                    ExploreFragment ef =(ExploreFragment) getActivity().getSupportFragmentManager().findFragmentByTag("explore_fragment");
                    if (ef != null)
                        ef.RefreshItem(selectedChoice, votingID, voteResponse);
                    //for (int i = 0;i< fg.localMasterVotingList.size();i++){
                    //    fg.localMasterVotingList.get(i).getMasterVotingView().setDescription("asadasdasd");
                    //    if (fg.localMasterVotingList.get(i).getMasterVotingView().getVotingID() == votingID){
                    //        fg.localMasterVotingList.get(i).getChoiceList().get(0).setPercentage(voteResponse.getChoiceList().get(0).getPercentage());
                    //        fg.localMasterVotingList.get(i).getChoiceList().get(1).setPercentage(voteResponse.getChoiceList().get(1).getPercentage());
                    //        fg.localMasterVotingList.get(i).getChoiceList().get(0).setClickedCount(voteResponse.getChoiceList().get(0).getClickedCount());
                    //        fg.localMasterVotingList.get(i).getChoiceList().get(1).setClickedCount(voteResponse.getChoiceList().get(1).getClickedCount());
                    //        if (voteResponse.getChoiceList().size() > 2){
                    //            fg.localMasterVotingList.get(i).getChoiceList().get(3).setPercentage(voteResponse.getChoiceList().get(3).getPercentage());
                    //            fg.localMasterVotingList.get(i).getChoiceList().get(3).setClickedCount(voteResponse.getChoiceList().get(3).getClickedCount());
                    //            if (voteResponse.getChoiceList().size() == 4){
                    //                fg.localMasterVotingList.get(i).getChoiceList().get(4).setPercentage(voteResponse.getChoiceList().get(4).getPercentage());
                    //                fg.localMasterVotingList.get(i).getChoiceList().get(4).setClickedCount(voteResponse.getChoiceList().get(4).getClickedCount());
                    //            }
                    //        }
                    //    }
                    //}
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

        if (masterVotingList.get(position).getChoiceList().size() == 2){
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
        else if(masterVotingList.get(position).getChoiceList().size() == 3){
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
        else if(masterVotingList.get(position).getChoiceList().size() == 4){
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

        masterVotingList.get(position).setSelectedChoice(selectedChoice);
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

    private void Follow(final boolean useBackup){
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_Follow), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                followResponse = gson.fromJson(response, myType);
                if (followResponse.isIsSuccess()) {
                    btFollow.setVisibility(View.GONE);
                    btFollowing.setVisibility(View.VISIBLE);
                    getProfileUserResponse.addFollower(loggedUser.getLoginView().getID());
                    tvFollowerCount.setText(String.valueOf(getProfileUserResponse.getUser().getFollowerCount()));
                    //List<Integer> followingListEdit = loggedUser.getFollowingList();
                    //followingListEdit.add(otherUserID);
                    //loggedUser.setFollowingList(followingListEdit);
                    //loggedUser.getFollowingList().add(otherUserID);
                    loggedUser.addFollowing(otherUserID);
                    Hawk.put("loggedUser",loggedUser);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                Follow(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("followingID", String.valueOf(otherUserID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public void RefreshItemCommentCount(int votingID, int commentCount){
        for (int i = 0; i<masterVotingList.size();i++){
            if (masterVotingList.get(i).getProfileVoting().getID() == votingID){
                masterVotingList.get(i).getProfileVoting().setCommentCount(commentCount);
            }
            mQuickAdapter.notifyDataSetChanged();
        }
    }

    private void Unfollow(final boolean useBackup){
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_Unfollow), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<BasicResponse>() {
                }.getType();
                followResponse = gson.fromJson(response, myType);
                if (followResponse.isIsSuccess()) {
                    btFollow.setVisibility(View.VISIBLE);
                    btFollowing.setVisibility(View.GONE);
                    getProfileUserResponse.removeFollower(loggedUser.getLoginView().getID());
                    tvFollowerCount.setText(String.valueOf(getProfileUserResponse.getUser().getFollowerCount()));

                    //List<Integer> followingListEdit = loggedUser.getFollowingList();
                    //followingListEdit.add(otherUserID);
                    //loggedUser.setFollowingList(followingListEdit);
                    //loggedUser.getFollowingList().add(otherUserID);
                    loggedUser.removeFollowing(otherUserID);
                    Hawk.put("loggedUser",loggedUser);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                Unfollow(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("unfollowingID", String.valueOf(otherUserID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void Report(final String reason, final int itemID, final int reportTypeID, final boolean useBackup){
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
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
                params.put("reportTypeID", String.valueOf(reportTypeID));
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
                    try{
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
                params.put("isMyProfile", "false");
                params.put("otherUserID", String.valueOf(otherUserID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void GetUserInformation(final boolean useBackup) {
        mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetProfileUser), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetProfileUserResponse>() {
                }.getType();
                getProfileUserResponse = gson.fromJson(response, myType);
                if (getProfileUserResponse.isIsSuccess()) {
                    try {
                        Glide.with(getContext()).load(getProfileUserResponse.getUser().getProfilePicURL()).apply(requestOptionsProfile).into(ivProfile);
                        Glide.with(getContext()).load(getProfileUserResponse.getUser().getProfilePicURL())
                                .apply(RequestOptions.bitmapTransform(multi))
                                .into(ivProfileBlur);
                        Glide.with(getContext()).load(getContext().getResources().getIdentifier(getProfileUserResponse.getUser().getFlagCode(), "drawable", getContext().getPackageName())).into(ivCountry);
                        tvNameSurname.setText(getProfileUserResponse.getUser().getNameSurname());
                        String location = getProfileUserResponse.getUser().getCity()+", "+getProfileUserResponse.getUser().getCountry();
                        tvLocation.setText(location);
                        tvVoteCount.setText(String.valueOf(getProfileUserResponse.getUser().getHasVoteCount()));
                        tvVotedCount.setText(String.valueOf(getProfileUserResponse.getUser().getVotedCount()));
                        tvCommentCount.setText(String.valueOf(getProfileUserResponse.getUser().getCommentCount()));
                        tvFollowerCount.setText(String.valueOf(getProfileUserResponse.getUser().getFollowerCount()));
                        tvFollowingCount.setText(String.valueOf(getProfileUserResponse.getUser().getFollowingCount()));
                        tvUsername.setText("@"+getProfileUserResponse.getUser().getUsername());
                        GetProfileRecords(false);
                    }catch (Exception ex){

                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetUserInformation(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("userID", String.valueOf(otherUserID));
                params.put("token", loggedUser.getToken());
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,110,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void Initialize(){
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        otherUserID = getArguments().getInt("UserID");
        infoDialog = new LovelyInfoDialog(getContext());
        infoDialog.setTopColorRes(R.color.colorPrimary);
        infoDialog.setIcon(R.drawable.ic_success_white);
        infoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        infoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        infoDialog.setTitle(getResources().getString(R.string.processSuccess));
        infoDialog.setMessage(getResources().getString(R.string.recordReported));
        progressDialog = new LovelyProgressDialog(getContext());
        progressDialog
                .setIcon(R.drawable.ic_hourglass_white)
                .setTitle(getResources().getString(R.string.pleaseWait))
                .setCancelable(false)
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
        requestOptionsProfile = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        multi = new MultiTransformation(
                new BlurTransformation(50),
                //new BrightnessFilterTransformation(-0.25f),
                new CenterCrop()//,
                //new RoundedCornersTransformation(50,0, RoundedCornersTransformation.CornerType.BOTTOM)
        );
        ivProfile = rootView.findViewById(R.id.ivProfile);
        ivProfileBlur = rootView.findViewById(R.id.ivProfileBlur);
        ivBlackened = rootView.findViewById(R.id.ivBlackened);
        ivGreyCircleOptions = rootView.findViewById(R.id.ivGreyCircleOptions);
        //ivGreyCircleBack = rootView.findViewById(R.id.ivGreyCircleBack);
        //ivBack = rootView.findViewById(R.id.ivBack);
        ivOptions = rootView.findViewById(R.id.ivOptions);
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

        ivGreyCircleOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsClicked();
            }
        });
        ivOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsClicked();
            }
        });
        //ivGreyCircleBack.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        BackClicked();
        //    }
        //});
        //ivBack.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        BackClicked();
        //    }
        //});

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
        btVote = myDialog.findViewById(R.id.btVote);
        dialogDummyImage = myDialog.findViewById(R.id.ivDummyImage);

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
        btFollow = rootView.findViewById(R.id.btFollow);
        btFollowing = rootView.findViewById(R.id.btFollowing);

        //for(int i =0;i< loggedUser.getFollowingList().size();i++){
        //    Log.d("tarja","al->"+loggedUser.getFollowingList().get(i).toString());
        //}

        if (loggedUser.getFollowingList().contains(otherUserID)){
            btFollowing.setVisibility(View.VISIBLE);
        }
        else {
            btFollow.setVisibility(View.VISIBLE);
        }
        confirmationDialog = new LovelyStandardDialog(getContext());
        tvFollowerCount = rootView.findViewById(R.id.tvFollowerCount);
        tvFollowingCount = rootView.findViewById(R.id.tvFollowingCount);
        followingLayout = rootView.findViewById(R.id.followingLayout);
        followerLayout = rootView.findViewById(R.id.followerLayout);

        ivSendMessage = rootView.findViewById(R.id.ivSendMessage);
    }

    //public void BackClicked(){
    //    //if (Hawk.get("NavigateProfile").equals("Home")){
    //       //HomeFragment frag = new HomeFragment();
    //       //getActivity().getSupportFragmentManager().beginTransaction()
    //       //        .replace(R.id.content_frame,frag)
    //       //        .addToBackStack(null)
    //       //        .commit();
    //    //}
    //    //getActivity().getSupportFragmentManager().popBackStack();
    //    getFragmentManager().beginTransaction().hide(getFragmentManager().findFragmentByTag("other_profile_fragment")).commit();
    //    Hawk.put("IsOtherProfileFragmentActive",false);
    //    if (getFragmentManager().findFragmentByTag("home_fragment") != null){
    //        getFragmentManager().beginTransaction().show(getFragmentManager().findFragmentByTag("home_fragment")).commit();
    //    }
    //    else
    //        getFragmentManager().beginTransaction().add(R.id.content_frame,new HomeFragment(),"home_fragment").commit();
    //}
    public void OptionsClicked(){
        ShowSheetMenu(true);
        //final AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        //dialog.setTitle("Baran");
        //dialog.setMessage("Şikayet et");
        //dialog.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
        //    @Override
        //    public void onClick(DialogInterface dialog, int which) {
//
        //    }
        //});
        //dialog.show();
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

        if (masterVotingList.get(position).getChoiceList().size() == 2){
            int iv1Percentage = (int)masterVotingList.get(position).getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)masterVotingList.get(position).getChoiceList().get(1).getPercentage();
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
        else if(masterVotingList.get(position).getChoiceList().size() == 3){
            int iv1Percentage = (int)masterVotingList.get(position).getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)masterVotingList.get(position).getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)masterVotingList.get(position).getChoiceList().get(2).getPercentage();
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
        else if(masterVotingList.get(position).getChoiceList().size() == 4){
            int iv1Percentage = (int)masterVotingList.get(position).getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)masterVotingList.get(position).getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)masterVotingList.get(position).getChoiceList().get(2).getPercentage();
            int iv4Percentage = (int)masterVotingList.get(position).getChoiceList().get(3).getPercentage();
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


}
