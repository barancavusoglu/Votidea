package com.bcmobileappdevelopment.votidea.HelperClass;

import java.util.ArrayList;

public class MasterChatObject {
    private DialogItem dialogItem = new DialogItem();
    private ArrayList<Message> messages = new ArrayList<>();

    public DialogItem getDialogItem() {
        return dialogItem;
    }

    public void setDialogItem(DialogItem dialogItem) {
        this.dialogItem = dialogItem;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }
}
