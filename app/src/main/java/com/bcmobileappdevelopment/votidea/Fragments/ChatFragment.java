package com.bcmobileappdevelopment.votidea.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bcmobileappdevelopment.votidea.HelperClass.Author;
import com.bcmobileappdevelopment.votidea.HelperClass.DialogItem;
import com.bcmobileappdevelopment.votidea.HelperClass.MasterChatObject;
import com.bcmobileappdevelopment.votidea.HelperClass.Message;
import com.bcmobileappdevelopment.votidea.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.orhanobut.hawk.Hawk;
import com.roughike.bottombar.BottomBar;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }

    View rootview;
    Button btButton;
    DialogsList dialogsList;
    DialogsListAdapter dialogsListAdapter;
    BottomBar host;
    DateFormatter dateFormatter;
    BroadcastReceiver chatMessageReceiver;
    RequestOptions requestOptions;
    TextView tvEmptyInbox;
    AdView mAdView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootview = inflater.inflate(R.layout.fragment_chat, container, false);
        mAdView = rootview.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        Initialize();
        InitializeListeners();
        InitializeBroadcast();
        //LetsCreateDialog();
        PopulateDialogs();
        return rootview;
    }

    private void InitializeBroadcast() {
        chatMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RefreshList();
            }
        };
        getActivity().registerReceiver(chatMessageReceiver,new IntentFilter("chat"));
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(chatMessageReceiver);
        BottomBar bottomBar = getActivity().findViewById(R.id.bottom_bar);
        if (bottomBar.getCurrentTabPosition() == 0){
            FancyButton btChat = getActivity().findViewById(R.id.btChat);
            btChat.setVisibility(View.VISIBLE);
        }
        bottomBar.setVisibility(View.VISIBLE);
        super.onDestroy();
    }

    private void PopulateDialogs() {
        List<MasterChatObject> masterChatObjects;
        masterChatObjects = Hawk.get("MasterChatObjects");
        if (masterChatObjects != null){
            Collections.sort(masterChatObjects, new Comparator<MasterChatObject>() {
                @Override
                public int compare(MasterChatObject o1, MasterChatObject o2) {
                    return o2.getDialogItem().getLastMessage().getCreatedAt().compareTo(o1.getDialogItem().getLastMessage().getCreatedAt());
                }
            });
            for (MasterChatObject masterItem:masterChatObjects){
                CreateDialog(masterItem);
            }
        }
        if (masterChatObjects.size() == 0){
            tvEmptyInbox.setVisibility(View.VISIBLE);
        }
        else
            tvEmptyInbox.setVisibility(View.GONE);
    }

    private void CreateDialog(MasterChatObject masterItem){
        DialogItem dialogItem = new DialogItem();
        dialogItem.setID(masterItem.getDialogItem().getId());
        dialogItem.setUserList(masterItem.getDialogItem().getUserList());
        dialogItem.setFromUsername(masterItem.getDialogItem().getFromUsername());
        dialogItem.setPhoto(masterItem.getDialogItem().getDialogPhoto());
        dialogItem.setLastMessage(masterItem.getDialogItem().getLastMessage());
        dialogItem.setUnreadCount(masterItem.getDialogItem().getUnreadCount());
        dialogItem.setFromUserID(masterItem.getDialogItem().getFromUserID());

        dialogsListAdapter.addItem(dialogItem);
    }

    private void LetsCreateDialog() {
        DialogItem dialogItem = new DialogItem();
        dialogItem.setID("1");
        dialogItem.setFromUsername("Elon");
        dialogItem.setPhoto("https://pbs.twimg.com/profile_images/378800000544769000/5d88a19ec1f5955c6a53b17dd6988687_400x400.jpeg");
        dialogItem.setUnreadCount(1);
        List<Author> userList = new ArrayList<>();
        Author author = new Author();
        author.setId("1");
        author.setName("Elon");
        author.setAvatar("https://i.ytimg.com/vi/TuL4bTIGXpc/hqdefault.jpg");
        userList.add(author);
        dialogItem.setUserList(userList);
        Message message = new Message();
        message.setId("5");
        message.setAuthor(author);
        message.setDate(new Date());
        message.setText("Selam");
        dialogItem.setLastMessage(message);

        DialogItem dialogItem2 = new DialogItem();
        dialogItem2.setID("2");
        dialogItem2.setFromUsername("Baran");
        dialogItem2.setPhoto("https://pbs.twimg.com/profile_images/378800000544769000/5d88a19ec1f5955c6a53b17dd6988687_400x400.jpeg");
        dialogItem2.setUnreadCount(0);
        userList = new ArrayList<>();
        author = new Author();
        author.setId("2");
        author.setName("Baran");
        author.setAvatar("https://i.ytimg.com/vi/TuL4bTIGXpc/hqdefault.jpg");
        userList.add(author);
        dialogItem2.setUserList(userList);
        message = new Message();
        message.setId("6");
        message.setAuthor(author);
        message.setDate(new Date(2018,05,5,5,47));
        message.setText("Hey");
        dialogItem2.setLastMessage(message);

        //dialogsListAdapter = new DialogsListAdapter(dialogsList, new ImageLoader() {
        //    @Override
        //    public void loadImage(ImageView imageView, @Nullable String url) {
        //        Glide.with(ChatFragment.this).load(url).into(imageView);
        //    }
        //});

        //dialogsListAdapter = new DialogsListAdapter<>(R.layout.fragment_chat, new ImageLoader() {
        //    @Override
        //    public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
        //        Glide.with(ChatFragment.this).load(url).into(imageView);
        //    }
        //});

        dialogsListAdapter = new DialogsListAdapter(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Glide.with(ChatFragment.this).load(url).into(imageView);
            }
        });

        dialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<DialogItem>() {
            @Override
            public void onDialogClick(DialogItem dialog) {
                btButton.callOnClick();
            }
        });

        dialogsList.setAdapter(dialogsListAdapter);
        dialogsListAdapter.addItem(dialogItem);
        dialogsListAdapter.addItem(dialogItem2);
    }

    private void InitializeListeners() {

    }

    private void Initialize() {
        Hawk.init(getContext()).build();
        dialogsList = rootview.findViewById(R.id.dialogsList);
        host = getActivity().findViewById(R.id.bottom_bar);

        requestOptions = new RequestOptions()
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        dialogsListAdapter = new DialogsListAdapter(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Glide.with(ChatFragment.this).load(url).apply(requestOptions).into(imageView);
            }
        });

        dialogsListAdapter.setOnDialogClickListener(new DialogsListAdapter.OnDialogClickListener<DialogItem>() {
            @Override
            public void onDialogClick(DialogItem dialog) {

                DialogFragment dialogFragment = new DialogFragment();
                Bundle args = new Bundle();
                args.putString("DialogID", dialog.getId());
                args.putString("FromUsername", dialog.getFromUsername());
                args.putString("ProfilePic", dialog.getDialogPhoto());
                args.putString("SenderID", dialog.getFromUserID());
                dialogFragment.setArguments(args);

                //FancyButton btBack = getActivity().findViewById(R.id.btBack);
                //btBack.setVisibility(View.VISIBLE);
                if (host.getCurrentTabPosition() == 0) {
                    getFragmentManager().beginTransaction()
                            .add(R.id.home_container, dialogFragment, "dialog_fragment")
                            .addToBackStack(null)
                            .commit();
                }
            }
        });

        dialogsListAdapter.setDatesFormatter(new DateFormatter.Formatter() {
            @Override
            public String format(Date date) {
                if (DateFormatter.isToday(date)) {
                    return DateFormatter.format(date, DateFormatter.Template.TIME);
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
        dialogsList.setAdapter(dialogsListAdapter);

        tvEmptyInbox = rootview.findViewById(R.id.tvEmptyInbox);

        FancyButton btChat = getActivity().findViewById(R.id.btChat);
        btChat.setIconColor(getResources().getColor(R.color.white));
    }

    public void RefreshList(){
        dialogsListAdapter.clear();
        PopulateDialogs();
    }
}
