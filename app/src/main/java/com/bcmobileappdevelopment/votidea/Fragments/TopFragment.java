package com.bcmobileappdevelopment.votidea.Fragments;


import android.app.Dialog;
import android.os.Bundle;
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
import com.bcmobileappdevelopment.votidea.Adapters.TopVotedCommentedAdapter;
import com.bcmobileappdevelopment.votidea.Adapters.TopVotedVotingAdapter;
import com.bcmobileappdevelopment.votidea.Adapters.TopVotersAdapter;
import com.bcmobileappdevelopment.votidea.GsonResponse.BasicResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetTopCommentedResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetTopUserResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetTopVotingResponse;
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
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
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
public class TopFragment extends Fragment {

    public TopFragment() {
        // Required empty public constructor
    }

    View rootview;
    LoginResponse loggedUser;
    ImageView ivVoter, ivVoted, ivCommented, ivVoterFrame, ivVotedFrame, ivCommentedFrame, dialogImage, dialogDummyImage;
    Boolean voterClicked, votedClicked, commentedClicked;
    RecyclerView rvVoters, rvVoted, rvCommented;
    BaseQuickAdapter votersAdapter, votedAdapter, commentedAdapter;
    List<GetTopUserResponse.TopResultsBean> votersDatalist;
    List<GetTopVotingResponse.TopResultsBean> votedDatalist;
    List<GetTopCommentedResponse.TopResultsBean> commentedDatalist;
    Gson gson;
    GetTopUserResponse getTopVotersResponse;
    GetTopVotingResponse getTopVotedResponse;
    GetTopCommentedResponse getTopCommentedResponse;
    AppBarLayout appBarLayout;
    GetTopUserResponse.TopResultsBean clickedVotersItem;
    GetTopVotingResponse.TopResultsBean clickedVotedItem;
    GetTopCommentedResponse.TopResultsBean clickedCommentedItem;
    List<GetTopVotingResponse.TopResultsBean.VotingBean.ChoiceListBean> clickedVotedChoiceList;
    List<GetTopCommentedResponse.TopResultsBean.VotingBean.ChoiceListBean> clickedCommentedChoiceList;
    int clickedVotedPosition,clickedCommentedPosition,clickedImage;
    Dialog myDialog;
    RequestOptions requestOptions;
    FancyButton btVote;
    LovelyStandardDialog approveDialog, guestDialog;
    VoteResponse voteResponse;
    LovelyTextInputDialog textInputDialog;
    BasicResponse reportResponse;
    LovelyInfoDialog infoDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_top, container, false);
        Initialize();
        InitializeListeners();
        if (getContext() != null ){
            InitializeRecyclerViewVoters();
            InitializeRecyclerViewVoted();
            InitializeRecyclerViewCommented();
            GetTopVoters(false);
        }
        return rootview;
    }

    private void Initialize() {
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        ivVoter = rootview.findViewById(R.id.ivVoter);
        ivVoterFrame = rootview.findViewById(R.id.ivVoterFrame);
        ivVoted = rootview.findViewById(R.id.ivVoted);
        ivVotedFrame = rootview.findViewById(R.id.ivVotedFrame);
        ivCommented = rootview.findViewById(R.id.ivCommented);
        ivCommentedFrame = rootview.findViewById(R.id.ivCommentedFrame);
        voterClicked = true;
        votedClicked = false;
        commentedClicked = false;
        rvVoters = rootview.findViewById(R.id.rvListUser);
        rvVoted = rootview.findViewById(R.id.rvListVoting);
        rvCommented = rootview.findViewById(R.id.rvListCommented);

        votersDatalist = new ArrayList<>();
        votedDatalist = new ArrayList<>();
        commentedDatalist = new ArrayList<>();

        myDialog = new Dialog(getContext());
        myDialog.setContentView(R.layout.image_dialog);
        myDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialogImage = myDialog.findViewById(R.id.ivDialogImage);
        dialogDummyImage = myDialog.findViewById(R.id.ivDummyImage);
        btVote = myDialog.findViewById(R.id.btVote);

        ViewConfigurator<EditText> viewConfigurator = new ViewConfigurator<EditText>() {
            @Override
            public void configureView(EditText v) {
                v.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
                v.setInputType(InputType.TYPE_CLASS_TEXT);
                v.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            }
        };

        infoDialog = new LovelyInfoDialog(getContext());
        infoDialog.setTopColorRes(R.color.colorPrimary);
        infoDialog.setIcon(R.drawable.ic_success_white);
        infoDialog.setConfirmButtonText(getResources().getString(R.string.ok));
        infoDialog.setConfirmButtonColor(getResources().getColor(R.color.colorPrimary));
        infoDialog.setTitle(getResources().getString(R.string.processSuccess));
        infoDialog.setMessage(getResources().getString(R.string.recordReported));

        textInputDialog = new LovelyTextInputDialog(getContext());
        textInputDialog.configureEditText(viewConfigurator);
        textInputDialog.setTopColor(getResources().getColor(R.color.colorPrimary));
        textInputDialog.setTitle(getResources().getString(R.string.reportReason));
        textInputDialog.setIcon(R.drawable.ic_report_white);
        textInputDialog.setTopTitleColor(getResources().getColor(R.color.white));
        textInputDialog.setHint(getResources().getString(R.string.optional));

        Window window = myDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        requestOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new RoundedCorners(50));

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

    }

    private void InitializeListeners() {
        ivVoter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!voterClicked){
                    voterClicked = true;
                    votedClicked = false;
                    commentedClicked = false;
                    ivVoterFrame.setVisibility(View.VISIBLE);
                    ivVotedFrame.setVisibility(View.GONE);
                    ivCommentedFrame.setVisibility(View.GONE);
                    ivVoter.setAlpha(1f);
                    ivVoted.setAlpha(.5f);
                    ivCommented.setAlpha(.5f);
                    rvVoters.setVisibility(View.VISIBLE);
                    rvVoted.setVisibility(View.GONE);
                    rvCommented.setVisibility(View.GONE);
                    if (votersDatalist.size() == 0){
                        GetTopVoters(false);
                    }
                }
            }
        });

        ivVoted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!votedClicked){
                    voterClicked = false;
                    votedClicked = true;
                    commentedClicked = false;
                    ivVoterFrame.setVisibility(View.GONE);
                    ivVotedFrame.setVisibility(View.VISIBLE);
                    ivCommentedFrame.setVisibility(View.GONE);
                    ivVoter.setAlpha(.5f);
                    ivVoted.setAlpha(1f);
                    ivCommented.setAlpha(.5f);
                    rvVoters.setVisibility(View.GONE);
                    rvVoted.setVisibility(View.VISIBLE);
                    rvCommented.setVisibility(View.GONE);
                    if (votedDatalist.size() == 0){
                        GetTopVoted(false);
                    }
                }
            }
        });

        ivCommented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!commentedClicked){
                    voterClicked = false;
                    votedClicked = false;
                    commentedClicked = true;
                    ivVoterFrame.setVisibility(View.GONE);
                    ivVotedFrame.setVisibility(View.GONE);
                    ivCommentedFrame.setVisibility(View.VISIBLE);
                    ivVoter.setAlpha(.5f);
                    ivVoted.setAlpha(.5f);
                    ivCommented.setAlpha(1f);
                    rvVoters.setVisibility(View.GONE);
                    rvVoted.setVisibility(View.GONE);}
                rvCommented.setVisibility(View.VISIBLE);
                if (commentedDatalist.size() == 0){
                    GetTopCommented(false);
                }
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
                if (votedClicked)
                    Vote(clickedVotedPosition, clickedVotedChoiceList.get(clickedImage-1).getID(), clickedVotedItem.getVoting().getMasterVotingView().getVotingID(),clickedImage,false);
                else if (commentedClicked)
                    Vote(clickedCommentedPosition, clickedCommentedChoiceList.get(clickedImage-1).getID(), clickedCommentedItem.getVoting().getMasterVotingView().getVotingID(),clickedImage,false);
            }
        });

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
    }

    private void InitializeRecyclerViewVoters() {
        rvVoters.setLayoutManager(new LinearLayoutManager(getContext()));
        votersAdapter = new TopVotersAdapter(R.layout.list_item_top_voters, votersDatalist);
        votersAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        votersAdapter.setEnableLoadMore(false);

        votersAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                clickedVotersItem = votersDatalist.get(position);
                FancyButton btBack = getActivity().findViewById(R.id.btBack);
                btBack.setVisibility(View.VISIBLE);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/topVotersLayout")){
                    if (loggedUser.getLoginView().getID() == (clickedVotersItem.getID()))
                    {
                        ProfileFragment profileFragment = new ProfileFragment();
                        BottomBar host = getActivity().findViewById(R.id.bottom_bar);
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
                        else if(host.getCurrentTabPosition() == 4) {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.profile_container,profileFragment, "profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if(host.getCurrentTabPosition() == 3) {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.top_container,profileFragment, "profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                    else {
                        Bundle args = new Bundle();
                        args.putInt("UserID", clickedVotersItem.getID());
                        OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                        otherProfileFragment.setArguments(args);

                        BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                        if (host.getCurrentTabPosition() == 0){
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.home_container,otherProfileFragment,"other_profile_fragment")
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
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.profile_container,otherProfileFragment,"other_profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                        else if(host.getCurrentTabPosition() == 3) {
                            getActivity().getSupportFragmentManager()
                                    .beginTransaction().add(R.id.top_container,otherProfileFragment,"other_profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                }
            }
        });

        rvVoters.setAdapter(votersAdapter);
    }

    private void InitializeRecyclerViewVoted() {
        rvVoted.setLayoutManager(new LinearLayoutManager(getContext()));
        votedAdapter = new TopVotedVotingAdapter(R.layout.list_item_home_desc_up, votedDatalist);
        votedAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        votedAdapter.setEnableLoadMore(false);

        votedAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {

                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return false;
                }
                clickedVotedItem = votedDatalist.get(position);
                clickedVotedChoiceList = clickedVotedItem.getVoting().getChoiceList();
                clickedVotedPosition = position;
                Log.d("tarja","position=>" +String.valueOf(position));

                if (clickedVotedItem.getVoting().getSelectedChoice() == 0){
                    if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                        Vote(position,clickedVotedChoiceList.get(0).getID(),clickedVotedItem.getVoting().getMasterVotingView().getVotingID(),1,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                        Vote(position,clickedVotedChoiceList.get(1).getID(),clickedVotedItem.getVoting().getMasterVotingView().getVotingID(),2,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedVotedItem.getVoting().getMasterVotingView().getChoiceCount() >= 3){
                        Vote(position,clickedVotedChoiceList.get(2).getID(),clickedVotedItem.getVoting().getMasterVotingView().getVotingID(),3,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedVotedItem.getVoting().getMasterVotingView().getChoiceCount() == 4){
                        Vote(position,clickedVotedChoiceList.get(3).getID(),clickedVotedItem.getVoting().getMasterVotingView().getVotingID(),4,false);
                    }
                }
                return true;
            }
        });

        votedAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                clickedVotedItem = votedDatalist.get(position);
                clickedVotedChoiceList = clickedVotedItem.getVoting().getChoiceList();
                clickedVotedPosition = position;
                btVote.setVisibility(View.GONE);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                    Glide.with(getContext()).load(clickedVotedChoiceList.get(0).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedVotedItem.getVoting().getSelectedChoice() == 0 && clickedVotedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedImage = 1;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                    Glide.with(getContext()).load(clickedVotedChoiceList.get(1).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedVotedItem.getVoting().getSelectedChoice() == 0 && clickedVotedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedImage = 2;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedVotedItem.getVoting().getMasterVotingView().getChoiceCount() >= 3){
                    Glide.with(getContext()).load(clickedVotedChoiceList.get(2).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedVotedItem.getVoting().getSelectedChoice() == 0 && clickedVotedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedImage = 3;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedVotedItem.getVoting().getMasterVotingView().getChoiceCount() == 4){
                    Glide.with(getContext()).load(clickedVotedChoiceList.get(3).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedVotedItem.getVoting().getSelectedChoice() == 0 && clickedVotedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedImage = 4;
                    }
                    myDialog.show();
                }
                else if ((view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/ivProfilePic") ||view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/tvNameSurname")) && !clickedVotedItem.getVoting().getMasterVotingView().isIsAnonymous()){
                    if (loggedUser.getLoginView().getID() == (clickedVotedItem.getVoting().getMasterVotingView().getOwnerUserID()))
                    {
                        ProfileFragment profileFragment = new ProfileFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().add(R.id.top_container,profileFragment, "profile_fragment")
                                .addToBackStack(null)
                                .commit();
                    }
                    else{
                        FancyButton btBack = getActivity().findViewById(R.id.btBack);
                        btBack.setVisibility(View.VISIBLE);
                        Bundle args = new Bundle();
                        args.putInt("UserID", clickedVotedItem.getVoting().getMasterVotingView().getOwnerUserID());
                        OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                        otherProfileFragment.setArguments(args);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction().add(R.id.top_container,otherProfileFragment, "other_profile_fragment")
                                .addToBackStack(null)
                                .commit();
                    }
                }
                else if(view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btVoteOptions")){
                    ShowSheetMenuVoted();
                }
                else if(view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btComments") && clickedVotedItem.getVoting().getMasterVotingView().isIsCommentAllowed()){
                    CommentsFragment commentsFragment = new CommentsFragment();
                    FancyButton btBack = getActivity().findViewById(R.id.btBack);
                    btBack.setVisibility(View.VISIBLE);
                    Bundle args = new Bundle();
                    args.putInt("VotingID", clickedVotedItem.getVoting().getMasterVotingView().getVotingID());
                    args.putInt("VotingOwnerID", clickedVotedItem.getVoting().getMasterVotingView().getOwnerUserID());
                    args.putInt("CommentCount", clickedVotedItem.getVoting().getMasterVotingView().getCommentCount());
                    commentsFragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.top_container,commentsFragment,"comments_fragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });
        rvVoted.setAdapter(votedAdapter);
    }

    private void InitializeRecyclerViewCommented(){
        rvCommented.setLayoutManager(new LinearLayoutManager(getContext()));
        commentedAdapter = new TopVotedCommentedAdapter(R.layout.list_item_home_desc_up, commentedDatalist);
        commentedAdapter.openLoadAnimation(BaseQuickAdapter.ALPHAIN);
        commentedAdapter.setEnableLoadMore(false);

        commentedAdapter.setOnItemChildLongClickListener(new BaseQuickAdapter.OnItemChildLongClickListener() {
            @Override
            public boolean onItemChildLongClick(BaseQuickAdapter adapter, View view, int position) {
                if (!loggedUser.getLoginView().isIsEmailApproved()){
                    if (loggedUser.getLoginView().getID() == 1)
                        guestDialog.show();
                    else
                        approveDialog.show();
                    return false;
                }
                clickedCommentedItem = commentedDatalist.get(position);
                clickedCommentedChoiceList = clickedCommentedItem.getVoting().getChoiceList();
                clickedCommentedPosition = position;

                if (clickedCommentedItem.getVoting().getSelectedChoice() == 0){
                    if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                        Vote(position,clickedCommentedChoiceList.get(0).getID(),clickedCommentedItem.getVoting().getMasterVotingView().getVotingID(),1,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                        Vote(position,clickedCommentedChoiceList.get(1).getID(),clickedCommentedItem.getVoting().getMasterVotingView().getVotingID(),2,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedCommentedItem.getVoting().getMasterVotingView().getChoiceCount() >= 3){
                        Vote(position,clickedCommentedChoiceList.get(2).getID(),clickedCommentedItem.getVoting().getMasterVotingView().getVotingID(),3,false);
                    }
                    else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedCommentedItem.getVoting().getMasterVotingView().getChoiceCount() == 4){
                        Vote(position,clickedCommentedChoiceList.get(3).getID(),clickedCommentedItem.getVoting().getMasterVotingView().getVotingID(),4,false);
                    }
                }
                return true;
            }
        });

        commentedAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                clickedCommentedItem = commentedDatalist.get(position);
                clickedCommentedChoiceList = clickedCommentedItem.getVoting().getChoiceList();
                clickedCommentedPosition = position;
                btVote.setVisibility(View.GONE);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv1")){
                    Glide.with(getContext()).load(clickedCommentedChoiceList.get(0).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedCommentedItem.getVoting().getSelectedChoice() == 0 && clickedCommentedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedCommentedPosition = position;
                        clickedImage = 1;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv2")){
                    Glide.with(getContext()).load(clickedCommentedChoiceList.get(1).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedCommentedItem.getVoting().getSelectedChoice() == 0 && clickedCommentedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedCommentedPosition = position;
                        clickedImage = 2;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv3") && clickedCommentedItem.getVoting().getMasterVotingView().getChoiceCount() >= 3){
                    Glide.with(getContext()).load(clickedCommentedChoiceList.get(2).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedCommentedItem.getVoting().getSelectedChoice() == 0 && clickedCommentedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedCommentedPosition = position;
                        clickedImage = 3;
                    }
                    myDialog.show();
                }
                else if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/iv4") && clickedCommentedItem.getVoting().getMasterVotingView().getChoiceCount() == 4){
                    Glide.with(getContext()).load(clickedCommentedChoiceList.get(3).getPictureURL()).apply(requestOptions).into(dialogImage);
                    if (clickedCommentedItem.getVoting().getSelectedChoice() == 0 && clickedCommentedItem.getVoting().getMasterVotingView().getOwnerUserID() != loggedUser.getLoginView().getID()){
                        btVote.setVisibility(View.VISIBLE);
                        clickedCommentedPosition = position;
                        clickedImage = 4;
                    }
                    myDialog.show();
                }
                else if ((view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/ivProfilePic") ||view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/tvNameSurname")) && !clickedCommentedItem.getVoting().getMasterVotingView().isIsAnonymous()){
                    if (loggedUser.getLoginView().getID() == (clickedCommentedItem.getVoting().getMasterVotingView().getOwnerUserID()))
                    {
                        ProfileFragment profileFragment = new ProfileFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().add(R.id.top_container,profileFragment, "profile_fragment")
                                .addToBackStack(null)
                                .commit();
                    }
                    else{
                        FancyButton btBack = getActivity().findViewById(R.id.btBack);
                        btBack.setVisibility(View.VISIBLE);
                        Bundle args = new Bundle();
                        args.putInt("UserID", clickedCommentedItem.getVoting().getMasterVotingView().getOwnerUserID());
                        OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                        otherProfileFragment.setArguments(args);

                        getActivity().getSupportFragmentManager()
                                .beginTransaction().add(R.id.top_container,otherProfileFragment, "other_profile_fragment")
                                .addToBackStack(null)
                                .commit();
                    }
                }
                else if(view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btVoteOptions")){
                    ShowSheetMenuCommented();
                }
                else if(view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/btComments") && clickedCommentedItem.getVoting().getMasterVotingView().isIsCommentAllowed()){
                    CommentsFragment commentsFragment = new CommentsFragment();
                    FancyButton btBack = getActivity().findViewById(R.id.btBack);
                    btBack.setVisibility(View.VISIBLE);
                    Bundle args = new Bundle();
                    args.putInt("VotingID", clickedCommentedItem.getVoting().getMasterVotingView().getVotingID());
                    args.putInt("VotingOwnerID", clickedCommentedItem.getVoting().getMasterVotingView().getOwnerUserID());
                    args.putInt("CommentCount", clickedCommentedItem.getVoting().getMasterVotingView().getCommentCount());
                    commentsFragment.setArguments(args);

                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.top_container,commentsFragment,"comments_fragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        rvCommented.setAdapter(commentedAdapter);
    }


    private void GetTopVoters(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetTopVoters), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetTopUserResponse>() {
                }.getType();
                getTopVotersResponse = gson.fromJson(response, myType);

                if (getTopVotersResponse.isIsSuccess()) {
                    if (getTopVotersResponse.getTopResults().size() != 0){
                        votersDatalist = getTopVotersResponse.getTopResults();
                        votersAdapter.addData(votersDatalist);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetTopVoters(true);
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

    private void GetTopVoted(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetTopVoted), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetTopVotingResponse>() {
                }.getType();
                getTopVotedResponse = gson.fromJson(response, myType);

                if (getTopVotedResponse.isIsSuccess()) {
                    if (getTopVotedResponse.getTopResults().size() != 0){
                        votedDatalist = getTopVotedResponse.getTopResults();
                        votedAdapter.addData(votedDatalist);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetTopVoted(true);
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

    private void GetTopCommented(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetTopCommented), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetTopCommentedResponse>() {
                }.getType();
                getTopCommentedResponse = gson.fromJson(response, myType);

                if (getTopCommentedResponse.isIsSuccess()) {
                    if (getTopCommentedResponse.getTopResults().size() != 0){
                        commentedDatalist = getTopCommentedResponse.getTopResults();
                        commentedAdapter.addData(commentedDatalist);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetTopCommented(true);
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

    public void GoTop(){
        appBarLayout = rootview.findViewById(R.id.appbar);
        appBarLayout.setExpanded(true,true);
        if (voterClicked)
            rvVoters.smoothScrollToPosition(0);
        if (votedClicked)
            rvVoted.smoothScrollToPosition(0);
        if (commentedClicked)
            rvCommented.smoothScrollToPosition(0);

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
                    if (votedClicked)
                        VoteCompleteVoted(position, selectedChoice);
                    else if (commentedClicked)
                        VoteCompleteCommented(position, selectedChoice);
                    HomeFragment fg =(HomeFragment) getActivity().getSupportFragmentManager().findFragmentByTag("home_fragment");
                    if (fg != null)
                        fg.RefreshItem(selectedChoice, votingID, voteResponse);
                }
                else if(!voteResponse.isIsSuccess() && voteResponse.getMessage().equals("duplicate_vote")){
                    if (votedClicked)
                        AppearVoteVoted(position);
                    else if(commentedClicked)
                        AppearVoteCommented(position);
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

    private void VoteCompleteVoted(int position, int selectedChoice){
        //View viewToUpdate = votedAdapter.getViewByPosition(position,R.id.listItemHomeLayout);
        View viewToUpdate = votedAdapter.getViewByPosition(rvVoted,position,R.id.listItemHomeLayout);
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

        if (votedDatalist.get(position).getVoting().getChoiceList().size() == 2){
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
        else if(votedDatalist.get(position).getVoting().getChoiceList().size() == 3){
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
        else if(votedDatalist.get(position).getVoting().getChoiceList().size() == 4){
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

        votedDatalist.get(position).getVoting().setSelectedChoice(selectedChoice);
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

    private void VoteCompleteCommented(int position, int selectedChoice){
        View viewToUpdate = commentedAdapter.getViewByPosition(rvCommented,position,R.id.listItemHomeLayout);
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

        if (commentedDatalist.get(position).getVoting().getChoiceList().size() == 2){
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
        else if(commentedDatalist.get(position).getVoting().getChoiceList().size() == 3){
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
        else if(commentedDatalist.get(position).getVoting().getChoiceList().size() == 4){
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

        commentedDatalist.get(position).getVoting().setSelectedChoice(selectedChoice);
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

    private void AppearVoteVoted(int position){
        Toasty.info(getContext(), getResources().getString(R.string.duplicate_vote), Toast.LENGTH_LONG, true).show();
        View viewToUpdate = votedAdapter.getViewByPosition(rvVoted,position,R.id.listItemHomeLayout);
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

        if (votedDatalist.get(position).getVoting().getChoiceList().size() == 2){
            int iv1Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(1).getPercentage();
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
        else if(votedDatalist.get(position).getVoting().getChoiceList().size() == 3){
            int iv1Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(2).getPercentage();
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
        else if(votedDatalist.get(position).getVoting().getChoiceList().size() == 4){
            int iv1Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(2).getPercentage();
            int iv4Percentage = (int)votedDatalist.get(position).getVoting().getChoiceList().get(3).getPercentage();
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

        // Duplicate olduğuna dair toast ile uyar TODO

        if(myDialog.isShowing()){
            myDialog.dismiss();
        }
    }

    private void AppearVoteCommented(int position){
        Toasty.info(getContext(), getResources().getString(R.string.duplicate_vote), Toast.LENGTH_LONG, true).show();
        View viewToUpdate = commentedAdapter.getViewByPosition(rvCommented,position,R.id.listItemHomeLayout);
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

        if (commentedDatalist.get(position).getVoting().getChoiceList().size() == 2){
            int iv1Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(1).getPercentage();
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
        else if(commentedDatalist.get(position).getVoting().getChoiceList().size() == 3){
            int iv1Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(2).getPercentage();
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
        else if(commentedDatalist.get(position).getVoting().getChoiceList().size() == 4){
            int iv1Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(0).getPercentage();
            int iv2Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(1).getPercentage();
            int iv3Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(2).getPercentage();
            int iv4Percentage = (int)commentedDatalist.get(position).getVoting().getChoiceList().get(3).getPercentage();
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

        // Duplicate olduğuna dair toast ile uyar TODO

        if(myDialog.isShowing()){
            myDialog.dismiss();
        }
    }

    private void ShowSheetMenuVoted(){
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
                            Report(text, clickedVotedItem.getVoting().getMasterVotingView().getVotingID(),2,false);
                        }
                    });
                    textInputDialog.show();
                }
                return false;
            }
        });
        sheetMenu.show(getContext());
    }

    private void ShowSheetMenuCommented(){
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
                            Report(text, clickedCommentedItem.getVoting().getMasterVotingView().getVotingID(),2,false);
                        }
                    });
                    textInputDialog.show();
                }
                return false;
            }
        });
        sheetMenu.show(getContext());
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
