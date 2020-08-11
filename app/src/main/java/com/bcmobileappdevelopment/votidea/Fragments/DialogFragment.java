package com.bcmobileappdevelopment.votidea.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bcmobileappdevelopment.votidea.Activities.BottomMenuActivity;
import com.bcmobileappdevelopment.votidea.HelperClass.Author;
import com.bcmobileappdevelopment.votidea.HelperClass.DialogItem;
import com.bcmobileappdevelopment.votidea.HelperClass.MasterChatObject;
import com.bcmobileappdevelopment.votidea.HelperClass.Message;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoadMoreMessagesResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.LoginResponse;
import com.bcmobileappdevelopment.votidea.GsonResponse.SendMessageResponse;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.roughike.bottombar.BottomBar;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragment extends Fragment implements MessageInput.InputListener {

    public DialogFragment() {
        // Required empty public constructor
    }

    View rootview;
    MessagesList messagesList;
    ImageLoader imageLoader;
    MessagesListAdapter<com.bcmobileappdevelopment.votidea.HelperClass.Message> adapter;
    Author me;
    BroadcastReceiver myReceiver;
    String dialogID, fromUsername, senderAvatar, senderID;
    ArrayList<MasterChatObject> masterChatObjects;
    MessageInput input;
    TextView tvFromUsername;
    RequestOptions requestOptionsProfile;
    Gson gson;
    LoadMoreMessagesResponse loadMoreMessagesResponse;
    LoginResponse loggedUser;
    int minID;
    ImageView ivProfilePic;
    Boolean dialogExists;
    SendMessageResponse sendMessageResponse;
    LovelyStandardDialog bannedDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_dialog, container, false);
        Initialize();
        InitializeAdapter();
        PopulateMessages();
        ResetUnreadCount();
        InitializeListeners();

        //com.bcmobileapps.omubumu.HelperClass.Message iMessage = new com.bcmobileapps.omubumu.HelperClass.Message();
        //iMessage.text = input.toString();
        //iMessage.date = new Date();
        //iMessage.id = String.valueOf(1);
        //iMessage.author = her;
        //adapter.addToStart(iMessage,true);
        //adapter.upsert(iMessage);
        //adapter.addToStart(iMessage,true);

        return rootview;
    }

    private void InitializeListeners() {
        ivProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt("UserID", Integer.valueOf(senderID));
                OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                otherProfileFragment.setArguments(args);

                BottomBar host = getActivity().findViewById(R.id.bottom_bar);
                if (host.getCurrentTabPosition() == 0){
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.home_container,otherProfileFragment,"other_profile_fragment")
                            .addToBackStack(null)
                            .commit();
                }
                else if(host.getCurrentTabPosition() == 1) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.explore_container,otherProfileFragment,"other_profile_fragment")
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
        });

        tvFromUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putInt("UserID", Integer.valueOf(senderID));
                OtherProfileFragment otherProfileFragment = new OtherProfileFragment();
                otherProfileFragment.setArguments(args);

                getActivity().getSupportFragmentManager()
                        .beginTransaction().add(R.id.home_container,otherProfileFragment,"other_profile_fragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    private void ResetUnreadCount() {
        //hawk ile lastIncomingMessageID güncellenmeli veya o tarz bişyler
        masterChatObjects = Hawk.get("MasterChatObjects");
        for (MasterChatObject item:masterChatObjects){
            if (item.getDialogItem().getId().equals(dialogID)){
                item.getDialogItem().setUnreadCount(0);
            }
        }
        Hawk.put("MasterChatObjects",masterChatObjects);
    }

    private void PopulateMessages() {
        masterChatObjects = Hawk.get("MasterChatObjects");
        for (MasterChatObject item:masterChatObjects){
            if (item.getDialogItem().getFromUserID().equals(senderID)){
                //senderAvatar = item.getDialogItem().getDialogPhoto();
                dialogID = item.getDialogItem().getId();
                for (com.bcmobileappdevelopment.votidea.HelperClass.Message message:item.getMessages()){
                    com.bcmobileappdevelopment.votidea.HelperClass.Message messageToAdd = new com.bcmobileappdevelopment.votidea.HelperClass.Message();
                    messageToAdd.setText(message.getText());
                    messageToAdd.setId(message.getId());
                    messageToAdd.setAuthor(message.getUser());
                    messageToAdd.setDate(message.getCreatedAt());
                    adapter.addToStart(messageToAdd,true);
                    if (Integer.parseInt(messageToAdd.getId()) != 0 && (minID == -1 || minID > Integer.parseInt(messageToAdd.getId()))){
                        minID = Integer.parseInt(messageToAdd.getId());
                    }
                }
            }
        }
    }

    private void Initialize() {
        Hawk.init(getContext()).build();
        loggedUser = Hawk.get("loggedUser");
        dialogID = getArguments().getString("DialogID","");
        fromUsername = getArguments().getString("FromUsername","");
        senderAvatar = getArguments().getString("ProfilePic","");
        senderID = getArguments().getString("SenderID","");
        minID = -1;
        tvFromUsername = rootview.findViewById(R.id.tvFromUsername);
        tvFromUsername.setText(fromUsername);
        ivProfilePic = rootview.findViewById(R.id.ivProfilePic);
        requestOptionsProfile = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(getContext()).load(senderAvatar).apply(requestOptionsProfile).into(ivProfilePic);

        me = new Author();
        me.setAvatar("");
        me.setId("0");
        me.setName("me");
        dialogExists = false;
        input = rootview.findViewById(R.id.input);
        input.setInputListener(this);
        messagesList = rootview.findViewById(R.id.messagesList);

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

        myReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (senderID.equals(intent.getStringExtra("senderUserID"))){
                    com.bcmobileappdevelopment.votidea.HelperClass.Message msg = new com.bcmobileappdevelopment.votidea.HelperClass.Message();
                    msg.setAuthor(new Author());
                    msg.setId(intent.getStringExtra("messageID"));
                    msg.getUser().setAvatar(intent.getStringExtra("senderUserProfilePic"));
                    msg.getUser().setId( intent.getStringExtra("senderUserID"));

                    byte[] data = Base64.decode(intent.getStringExtra("content"), Base64.DEFAULT);
                    try {
                        String emojiMessage = new String(data, "UTF-8");
                        msg.setText(emojiMessage);
                    }catch (Exception e){

                    }
                    //msg.setText(intent.getStringExtra("content"));
                    msg.getUser().setName(intent.getStringExtra("senderUserName"));
                    msg.setDate(new Date());
                    adapter.addToStart(msg,true);
                }
            }
        };
        getActivity().registerReceiver(myReceiver,new IntentFilter("chat"));
    }

    @Override
    public void onDestroy() {
        Log.d("tarja","destroyer");
        getActivity().unregisterReceiver(myReceiver);
        super.onDestroy();
    }

    private void InitializeAdapter() {
        //adapter = new MessagesListAdapter<>("0", imageLoader);
        adapter = new MessagesListAdapter<>("0", null);
        messagesList.setAdapter(adapter);
        adapter.setLoadMoreListener(new MessagesListAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                LoadMoreMessages(false);
                Log.d("tarja",page+" "+ totalItemsCount);
            }
        });
        adapter.setDateHeadersFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    return getResources().getString(R.string.today);
                }
                else if (DateFormatter.isYesterday(date)) {
                    return getResources().getString(R.string.yesterday);
                }
                else if (DateFormatter.isCurrentYear(date)) {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
                }
                else {
                    return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
                }
            }
        });
    }

    private void LoadMoreMessages(final boolean useBackup){
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_LoadMoreMessages), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<LoadMoreMessagesResponse>() {
                }.getType();
                loadMoreMessagesResponse = gson.fromJson(response, myType);
                if (loadMoreMessagesResponse.isIsSuccess()) {
                    String comment;
                    byte[] data;

                    List<com.bcmobileappdevelopment.votidea.HelperClass.Message> messages = new ArrayList<>();
                    for (LoadMoreMessagesResponse.MessagesBean messageItem: loadMoreMessagesResponse.getMessages()){
                        com.bcmobileappdevelopment.votidea.HelperClass.Message messageToAdd = new com.bcmobileappdevelopment.votidea.HelperClass.Message();
                        Author messageAuthor = new Author();
                        messageAuthor.setId(String.valueOf(messageItem.getFromUserID()));
                        messageAuthor.setAvatar(senderAvatar);
                        messageAuthor.setName("");
                        messageToAdd.setAuthor(messageAuthor);
                        try{
                            messageToAdd.setDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(messageItem.getDate()));
                        }catch (Exception e){
                            messageToAdd.setDate(new Date());
                        }
                        messageToAdd.setId(messageItem.getID());
                        //messageToAdd.setText(messageItem.getText());
                        comment = messageItem.getText();
                        data = Base64.decode(comment, Base64.DEFAULT);
                        try {
                            String emojiMessage = new String(data, "UTF-8");
                            messageToAdd.setText(emojiMessage);
                        }catch (Exception e){

                        }
                        messages.add(messageToAdd);
                        if (Integer.parseInt(messageToAdd.getId()) != 0 && (minID == -1 || minID > Integer.parseInt(messageToAdd.getId()))){
                            minID = Integer.parseInt(messageToAdd.getId());
                        }
                    }
                    adapter.addToEnd(messages,false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                    LoadMoreMessages(true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("userID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("token", loggedUser.getToken());
                params.put("chatID", dialogID);
                params.put("minID", String.valueOf(minID));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    @Override
    public boolean onSubmit(CharSequence input) {
        try {
            String contentWithEmoji,content;
            Log.d("tarja","girilen input -> "+ input.toString());
            //content = input.toString().replaceAll("\\s","").replaceAll("\\n","");
            content = input.toString().replaceAll(" +", " ").replaceAll("\\n","");
            Log.d("tarja","content -> "+ content);

            if (!content.equals("") && content.length() <= 250){
                byte[] data = input.toString().replaceAll("\\s\\s","").replaceAll("\\n","").getBytes("UTF-8");
                contentWithEmoji = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);

                Message newMessage = new Message();
                newMessage.setText(content);
                newMessage.setDate(new Date());
                newMessage.setId("0");
                newMessage.setAuthor(me);
                adapter.addToStart(newMessage,true);
                SendMessage(content,contentWithEmoji,false);
            }else
                return false; //TODO tost göster
        }
        catch (Exception e){
            return false; // TODO tost göster
        }
        return true;
    }

    private void SubmitMessageOld(Message newMessage) {
        //com.bcmobileapps.omubumu.HelperClass.Message newMessage = new com.bcmobileapps.omubumu.HelperClass.Message();
        //newMessage.setText(input.toString());
        //newMessage.setDate(new Date());
        //newMessage.setId("0");
        //newMessage.setAuthor(me);
        dialogExists = false;
        masterChatObjects = Hawk.get("MasterChatObjects");
        for (MasterChatObject item:masterChatObjects){
            if (item.getDialogItem().getFromUserID().equals(senderID)){
                item.getMessages().add(newMessage);
                item.getDialogItem().setLastMessage(newMessage);
                dialogExists = true;
            }
        }
        if (!dialogExists){
            MasterChatObject masterChatObjectAdd = new MasterChatObject();
            DialogItem dialogItemAdd = new DialogItem();
            dialogItemAdd.setUnreadCount(0);
            dialogItemAdd.setPhoto(senderAvatar);
            dialogItemAdd.setID("-1");
            dialogItemAdd.setFromUsername(fromUsername);
            dialogItemAdd.setFromUserID(senderID);

            List<Author> authorListAdd = new ArrayList<>();
            Author authorAdd = new Author();
            authorAdd.setName(fromUsername);
            authorAdd.setAvatar(senderAvatar);
            authorAdd.setId(senderID);
            authorListAdd.add(authorAdd);
            dialogItemAdd.setUserList(authorListAdd);

            Message messageToAdd = new Message();
            ArrayList<Message> messageListAdd = new ArrayList<>();

            messageToAdd.setAuthor(me);
            messageToAdd.setId("0");
            try{
                messageToAdd.setDate(new Date());
            }catch (Exception e){
                messageToAdd.setDate(new Date());
            }
            messageToAdd.setText(newMessage.getText());
            messageListAdd.add(messageToAdd);

            dialogItemAdd.setLastMessage(messageToAdd);
            masterChatObjectAdd.setDialogItem(dialogItemAdd);
            masterChatObjectAdd.setMessages(messageListAdd);
            masterChatObjects.add(masterChatObjectAdd);
        }
        Hawk.put("MasterChatObjects",masterChatObjects);
        //SendMessage(newMessage.getText());
    }

    private void SubmitMessage(String messageID, String chatID, String content) {
        Log.d("tarja","SubmitMessage");
        //com.bcmobileapps.omubumu.HelperClass.Message newMessage = new com.bcmobileapps.omubumu.HelperClass.Message();
        //newMessage.setText(input.toString());
        //newMessage.setDate(new Date());
        //newMessage.setId("0");
        //newMessage.setAuthor(me);
        dialogExists = false;
        masterChatObjects = Hawk.get("MasterChatObjects");
        for (MasterChatObject item:masterChatObjects){
            if (item.getDialogItem().getFromUserID().equals(senderID)){
                Log.d("tarja","1 girdim");
                Message newMessage = new Message();
                newMessage.setText(content);
                newMessage.setDate(new Date());
                newMessage.setId(messageID);
                newMessage.setAuthor(me);

                item.getMessages().add(newMessage);
                item.getDialogItem().setLastMessage(newMessage);
                dialogExists = true;
            }
        }
        if (!dialogExists){
            Log.d("tarja","2 girdim");
            MasterChatObject masterChatObjectAdd = new MasterChatObject();
            DialogItem dialogItemAdd = new DialogItem();
            dialogItemAdd.setUnreadCount(0);
            dialogItemAdd.setPhoto(senderAvatar);
            dialogItemAdd.setID(chatID);
            dialogItemAdd.setFromUsername(fromUsername);
            dialogItemAdd.setFromUserID(senderID);

            List<Author> authorListAdd = new ArrayList<>();
            Author authorAdd = new Author();
            authorAdd.setName(fromUsername);
            authorAdd.setAvatar(senderAvatar);
            authorAdd.setId(senderID);
            authorListAdd.add(authorAdd);
            dialogItemAdd.setUserList(authorListAdd);

            Message messageToAdd = new Message();
            ArrayList<Message> messageListAdd = new ArrayList<>();

            messageToAdd.setAuthor(me);
            messageToAdd.setId(messageID);
            try{
                messageToAdd.setDate(new Date());
            }catch (Exception e){
                messageToAdd.setDate(new Date());
            }
            messageToAdd.setText(content);
            messageListAdd.add(messageToAdd);

            dialogItemAdd.setLastMessage(messageToAdd);
            masterChatObjectAdd.setDialogItem(dialogItemAdd);
            masterChatObjectAdd.setMessages(messageListAdd);
            masterChatObjects.add(masterChatObjectAdd);
        }
        Hawk.put("MasterChatObjects",masterChatObjects);
        //SendMessage(newMessage.getText());
    }

    private void SendMessage(final String content, final String contentWithEmoji, final boolean useBackup) {
        RequestQueue mRequestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        String webserviceURL = getResources().getString(R.string.webservice_main);
        if (useBackup)
            webserviceURL = getResources().getString(R.string.webservice_backup);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, webserviceURL+getResources().getString(R.string.ws_SendMessage), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                Type myType = new TypeToken<SendMessageResponse>() {
                }.getType();
                sendMessageResponse = gson.fromJson(response, myType);
                Log.d("tarja","SendMessage");
                if (sendMessageResponse.isIsSuccess()){
                    Log.d("tarja","SendMessage Success");
                    //masterChatObjects = Hawk.get("MasterChatObjects");
                    //for (MasterChatObject item:masterChatObjects){
                    //    if (item.getDialogItem().getFromUserID().equals(senderID)){
                    //        if (item.getDialogItem().getId().equals("-1")){
                    //            item.getDialogItem().setID(sendMessageResponse.getChatID());
                    //        }
                    //        for (com.bcmobileapps.omubumu.HelperClass.Message messageItem: item.getMessages()){
                    //            if (messageItem.getId().equals("0"))
                    //                messageItem.setId(sendMessageResponse.getMessageID());
                    //        }
                    //    }
                    //}
                    //Hawk.put("MasterChatObjects",masterChatObjects);
                    SubmitMessage(sendMessageResponse.getMessageID(),sendMessageResponse.getChatID(),content);
                    if (minID == -1)
                        minID = Integer.parseInt(sendMessageResponse.getMessageID());
                    Log.d("tarja","minID -> "+minID);
                }
                else{
                    bannedDialog.show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (!useBackup)
                SendMessage(content,contentWithEmoji,true);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("masterPass", getResources().getString(R.string.masterpass));
                params.put("token", loggedUser.getToken());
                params.put("fromUserID", String.valueOf(loggedUser.getLoginView().getID()));
                params.put("toUserID", senderID);
                params.put("messageContent", contentWithEmoji);
                params.put("messageContentMeaning", content);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000,10,1.0f));
        mRequestQueue.add(stringRequest);
    }

    public String GetActiveDialogID(){
        if (dialogID != null)
            return dialogID;
        else
            return "";
    }

    public String GetActiveFromUserID(){
        if (senderID != null)
            return senderID;
        else
            return "";
    }

    @Override
    public void onDetach() {
        Log.d("tarja","ondetachh");
        ChatFragment cf =(ChatFragment) getActivity().getSupportFragmentManager().findFragmentByTag("chat_fragment");
        if (cf != null)
            cf.RefreshList();
        super.onDetach();
    }
}