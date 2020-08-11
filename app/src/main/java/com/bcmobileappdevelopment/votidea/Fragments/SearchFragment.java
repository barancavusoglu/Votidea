package com.bcmobileappdevelopment.votidea.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.bcmobileappdevelopment.votidea.Adapters.SearchAdapter;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.SearchUserResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }



    View rootview;
    FancyButton btSearch;
    SearchUserResponse searchUserResponse;
    Gson gson;
    int callCount, guideID, timeToWait = 2000;
    Boolean loadMoreRequested, firstTime, firstTimeInitializeRv, searchResultsAttached, isHandlerStarted;
    List<SearchUserResponse.UserListBean> dataList, localSearchUserList;
    LoginResponse loggedUser;
    RecyclerView recyclerView;
    BaseQuickAdapter mQuickAdapter;
    SearchUserResponse.UserListBean clickedUser;
    EditText etUser;
    TextView tvNoResult;
    Handler waitHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_search, container, false);
        Initialize();
        InitializeListeners();
        return  rootview;
    }

    @Override
    public void onPause() {
        waitHandler.removeCallbacks(searchRunnable);
        super.onPause();
    }

    private void Initialize() {
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        btSearch = rootview.findViewById(R.id.btSearch);
        callCount = 0;
        guideID =0;
        loadMoreRequested = false;
        firstTime = true;
        localSearchUserList = new ArrayList<>();
        clickedUser = new SearchUserResponse.UserListBean();
        firstTimeInitializeRv = true;
        etUser = rootview.findViewById(R.id.etUser);
        searchResultsAttached = false;
        recyclerView = rootview.findViewById(R.id.rvList);
        etUser.requestFocus();
        isHandlerStarted = false;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
        tvNoResult = rootview.findViewById(R.id.tvNoResult);
    }

    private boolean InputVerification(){
        if (etUser.length() < 3 || etUser.length() >30){
            etUser.setError(getResources().getString(R.string.inputSizeError));
            return  false;
        }
        return true;
    }

    private void InitializeListeners() {
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (InputVerification()){
                    Log.d("tarja",etUser.getText().toString().replaceAll("\\s\\s","").replaceAll("\n",""));
                    callCount = 0;
                    guideID =0;
                    loadMoreRequested = false;
                    firstTime = true;
                    dataList = new ArrayList<>();
                    localSearchUserList = new ArrayList<>();
                    mQuickAdapter = new SearchAdapter(R.layout.list_item_search, dataList);
                    mQuickAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(mQuickAdapter);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(rootview.getWindowToken(), 0);
                    SearchUser(false);
                }
            }
        });
        etUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (isHandlerStarted)
                    restartHandler();
                else {
                    startHandler();
                    isHandlerStarted = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    Runnable searchRunnable = new Runnable() {
        @Override
        public void run() {
            if (InputVerification()){
                Log.d("tarja",etUser.getText().toString().replaceAll("\\s\\s","").replaceAll("\n",""));
                callCount = 0;
                guideID =0;
                loadMoreRequested = false;
                firstTime = true;
                dataList = new ArrayList<>();
                localSearchUserList = new ArrayList<>();
                mQuickAdapter = new SearchAdapter(R.layout.list_item_search, dataList);
                mQuickAdapter.notifyDataSetChanged();
                recyclerView.setAdapter(mQuickAdapter);
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(rootview.getWindowToken(), 0);
                SearchUser(false);
            }
        }
    };



    public void startHandler() {
        waitHandler.postDelayed(searchRunnable, timeToWait);
    }

    public void restartHandler() {
        waitHandler.removeCallbacks(searchRunnable);
        waitHandler.postDelayed(searchRunnable, timeToWait);
    }

    private void SearchUser(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_SearchUser), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<SearchUserResponse>() {
                }.getType();
                searchUserResponse = gson.fromJson(response, myType);
                Log.d("tarja", "size= " + String.valueOf(searchUserResponse.getUserList().size()));
                Log.d("tarja", "isSUccess= " + String.valueOf(searchUserResponse.isIsSuccess()));

                if (searchUserResponse.isIsSuccess()) {
                    callCount++;
                    if (searchUserResponse.getUserList().size() == 0){
                        if (loadMoreRequested){
                            loadMoreRequested = false;
                            mQuickAdapter.loadMoreEnd();
                        }
                    }
                    else {
                        dataList = searchUserResponse.getUserList();
                        localSearchUserList.addAll(dataList);
                        if (firstTime){
                            guideID = searchUserResponse.getUserList().get(0).getID();
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
                if (localSearchUserList.size() == 0)
                    tvNoResult.setVisibility(View.VISIBLE);
                else if(tvNoResult.getVisibility()== View.VISIBLE)
                    tvNoResult.setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                SearchUser(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("requestedUser", etUser.getText().toString().replaceAll("\\s\\s","").replaceAll("\n",""));
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
        mQuickAdapter = new SearchAdapter(R.layout.list_item_search, dataList);
        mQuickAdapter.setEnableLoadMore(true);
        mQuickAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                recyclerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (getContext() != null && dataList.size() == 20){
                            loadMoreRequested = true;
                            SearchUser(false);
                        }
                        else if(dataList.size()<20){
                            Log.d("tarja","datalist size= "+String.valueOf(dataList.size()));
                            loadMoreRequested = false;
                            mQuickAdapter.loadMoreEnd();
                        }
                    }
                },0 );
            }
        },recyclerView);


        mQuickAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                clickedUser = localSearchUserList.get(position);
                FancyButton btBack = getActivity().findViewById(R.id.btBack);
                btBack.setVisibility(View.VISIBLE);

                if (view.getResources().getResourceName(view.getId()).equals("com.bcmobileappdevelopment.votidea:id/searchLayout")){
                    if (loggedUser.getLoginView().getID() == (clickedUser.getID()))
                    {
                        ProfileFragment profileFragment = new ProfileFragment();
                        getActivity().getSupportFragmentManager()
                                .beginTransaction().add(R.id.explore_container,profileFragment, "profile_fragment")
                                .addToBackStack(null)
                                .commit();

                    }
                    else {                                                           // geri tuşuna 1 kere basınca diğer fragmentler kaybolacak, home a dönecek
                        Bundle args = new Bundle();
                        args.putInt("UserID", clickedUser.getID());
                        OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                        otherProfileFragment.setArguments(args);

                        getActivity().getSupportFragmentManager().beginTransaction()
                                .add(R.id.explore_container,otherProfileFragment, "other_profile_fragment")
                                .addToBackStack(null)
                                .commit();
                    }
                }
            }
        });
        recyclerView.setAdapter(mQuickAdapter);
    }
}
