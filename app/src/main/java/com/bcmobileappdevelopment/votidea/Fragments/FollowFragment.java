package com.bcmobileappdevelopment.votidea.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.Adapters.FollowAdapter;
import com.bcmobileappdevelopment.votidea.GsonResponse.GetFollowDataResponse;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowFragment extends Fragment {


    public FollowFragment() {
        // Required empty public constructor
    }

    View rootView;
    int userID;
    LoginResponse loggedUser;
    String mode;
    Gson gson;
    GetFollowDataResponse getFollowDataResponse;
    int callCount, guideID;
    boolean firstTime,loadMoreRequested,refreshRequested;
    //RefreshLayout refreshLayout;
    //RefreshLayout refresh;
    RecyclerView recyclerView;
    BaseQuickAdapter mQuickAdapter;
    List<GetFollowDataResponse.FollowDataListBean> dataList, localFollowDataViewList;
    GetFollowDataResponse.FollowDataListBean clickedFollowData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_follow, container, false);

        Initialize();
        InitializeListeners();
        if (getContext() != null ){
            GetFollowData(false);
        }

        return rootView;
    }

    private void Initialize() {
        userID = getArguments().getInt("UserID",0);
        mode = getArguments().getString("Mode","");
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        callCount = 0;
        guideID = 0;
        firstTime = true;
        loadMoreRequested = false;
        refreshRequested = false;
        //refreshLayout = rootView.findViewById(R.id.refreshLayout);
        recyclerView = rootView.findViewById(R.id.rvList);
        localFollowDataViewList = new ArrayList<>();
        dataList = new ArrayList<>();
    }

    private void InitializeListeners() {
        //refreshLayout.setOnRefreshListener(new OnRefreshListener() {
        //    @Override
        //    public void onRefresh(RefreshLayout refreshLayout) {
        //        refresh = refreshLayout;
        //        refreshRequested = true;
        //        RefreshRecyclerView();
        //    }
        //});
        //refreshLayout.setOnRefreshListener(new OnRefreshListener() {
        //    @Override
        //    public void onRefresh(RefreshLayout refreshLayout) {
        //        refresh = refreshLayout;
        //        refreshRequested = true;
        //        RefreshRecyclerView();
        //    }
        //});
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColors(getResources().getColor(R.color.colorPrimary),getResources().getColor(R.color.colorPrimaryDark));
                return new BezierRadarHeader(context);
            }
        });
    }

    public void RefreshRecyclerView(){
        localFollowDataViewList = new ArrayList<>();
        clickedFollowData = new GetFollowDataResponse.FollowDataListBean();
        guideID = 0;
        loadMoreRequested = false;
        callCount = 0;
        firstTime = true;
        GetFollowData(false);
    }

    private void GetFollowData(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_GetFollowData), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<GetFollowDataResponse>() {
                }.getType();
                getFollowDataResponse = gson.fromJson(response, myType);

                if (getFollowDataResponse.isIsSuccess()) {
                    callCount++;
                    //if (refreshRequested){
                    //    refreshRequested = false;
                    //    refresh.finishRefresh();
                    //}
                    if (getFollowDataResponse.getFollowDataList().size() == 0){
                        if (loadMoreRequested){
                            loadMoreRequested = false;
                            mQuickAdapter.loadMoreEnd();
                        }
                    }
                    else {
                        dataList = getFollowDataResponse.getFollowDataList();
                        localFollowDataViewList.addAll(getFollowDataResponse.getFollowDataList());
                        if (firstTime){
                            guideID = getFollowDataResponse.getGuideID();
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
                else{
                    Log.d("tarja",getFollowDataResponse.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                GetFollowData(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("requestedUser", String.valueOf(userID));
                params.put("followingOrFollowed", mode);
                params.put("guideID", String.valueOf(guideID));
                params.put("callCount", String.valueOf(callCount));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    private void InitializeRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mQuickAdapter = new FollowAdapter(R.layout.list_item_follow, dataList);
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
                            GetFollowData(false);
                        }
                    }
                },0);
            }
        },recyclerView);

        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                clickedFollowData = localFollowDataViewList.get(position);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/followLayout")){
                    if (loggedUser.getLoginView().getID() == (clickedFollowData.getID()))
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
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .add(R.id.top_container,profileFragment, "profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                    else {
                        Bundle args = new Bundle();
                        args.putInt("UserID", clickedFollowData.getID());
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
                        else if(host.getCurrentTabPosition() == 3) {
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .add(R.id.top_container,otherProfileFragment,"other_profile_fragment")
                                    .addToBackStack(null)
                                    .commit();
                        }
                    }
                }
            }
        });
        recyclerView.setAdapter(mQuickAdapter);
    }
}
