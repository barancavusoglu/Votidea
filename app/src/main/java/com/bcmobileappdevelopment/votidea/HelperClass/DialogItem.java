package com.bcmobileappdevelopment.votidea.HelperClass;

import com.stfalcon.chatkit.commons.models.IDialog;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;

import java.util.ArrayList;
import java.util.List;

public class DialogItem implements IDialog {

    private int unreadCount;
    private String photo, fromUsername, ID, fromUserID;

    public List<Author> getUserList() {
        return userList;
    }

    private List<Author> userList = new ArrayList<>();
    private Message lastMessage = new Message();

    public String getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFromUsername() {
        return fromUsername;
    }

    public void setFromUsername(String fromUsername) {
        this.fromUsername = fromUsername;
    }

    public void setID(String ID) {
        this.ID = ID;
    }


    public void setUserList(List<Author> userList) {
        this.userList = userList;
    }



    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDialogPhoto() {
        return photo;
    }

    @Override
    public String getDialogName() {
        return fromUsername;
    }

    @Override
    public List<? extends IUser> getUsers() {
        return userList;
    }

    @Override
    public IMessage getLastMessage() {
        return lastMessage;
    }

    @Override
    public void setLastMessage(IMessage message) {
        this.lastMessage = (Message) message;
    }

    @Override
    public int getUnreadCount() {
        return unreadCount;
    }
}
