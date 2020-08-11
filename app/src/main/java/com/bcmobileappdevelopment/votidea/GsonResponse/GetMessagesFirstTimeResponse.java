package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetMessagesFirstTimeResponse {



    private boolean isSuccess;
    private String message;
    private int totalUnreadCount;
    private int maxID;
    private List<DialogsBean> dialogs;

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotalUnreadCount() {
        return totalUnreadCount;
    }

    public void setTotalUnreadCount(int totalUnreadCount) {
        this.totalUnreadCount = totalUnreadCount;
    }

    public int getMaxID() {
        return maxID;
    }

    public void setMaxID(int maxID) {
        this.maxID = maxID;
    }

    public List<DialogsBean> getDialogs() {
        return dialogs;
    }

    public void setDialogs(List<DialogsBean> dialogs) {
        this.dialogs = dialogs;
    }

    public static class DialogsBean {

        private int unreadCount;
        private String photo;
        private String fromUsername;
        private String ID;
        private String fromUserID;
        private List<MessagesBean> messages;

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }

        public String getPhoto() {
            return photo;
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

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public String getFromUserID() {
            return fromUserID;
        }

        public void setFromUserID(String fromUserID) {
            this.fromUserID = fromUserID;
        }

        public List<MessagesBean> getMessages() {
            return messages;
        }

        public void setMessages(List<MessagesBean> messages) {
            this.messages = messages;
        }

        public static class MessagesBean {
            private String ID;
            private String text;
            private String date;
            private int fromUserID;

            public String getID() {
                return ID;
            }

            public void setID(String ID) {
                this.ID = ID;
            }

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getDate() {
                return date;
            }

            public void setDate(String date) {
                this.date = date;
            }

            public int getFromUserID() {
                return fromUserID;
            }

            public void setFromUserID(int fromUserID) {
                this.fromUserID = fromUserID;
            }
        }
    }
}
