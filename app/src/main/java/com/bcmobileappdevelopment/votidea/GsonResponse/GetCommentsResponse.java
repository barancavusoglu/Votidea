package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetCommentsResponse {


    private boolean isSuccess;
    private String message;
    private List<CommentViewListBean> commentViewList;

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

    public List<CommentViewListBean> getCommentViewList() {
        return commentViewList;
    }

    public void setCommentViewList(List<CommentViewListBean> commentViewList) {
        this.commentViewList = commentViewList;
    }

    public static class CommentViewListBean {

        private int ID;
        private int VotingID;
        private int UserID;
        private String Date;
        private String Description;
        private boolean IsActive;
        private String ProfilePicURL;
        private String Username;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getVotingID() {
            return VotingID;
        }

        public void setVotingID(int VotingID) {
            this.VotingID = VotingID;
        }

        public int getUserID() {
            return UserID;
        }

        public void setUserID(int UserID) {
            this.UserID = UserID;
        }

        public String getDate() {
            return Date;
        }

        public void setDate(String Date) {
            this.Date = Date;
        }

        public String getDescription() {
            return Description;
        }

        public void setDescription(String Description) {
            this.Description = Description;
        }

        public boolean isIsActive() {
            return IsActive;
        }

        public void setIsActive(boolean IsActive) {
            this.IsActive = IsActive;
        }

        public String getProfilePicURL() {
            return ProfilePicURL;
        }

        public void setProfilePicURL(String ProfilePicURL) {
            this.ProfilePicURL = ProfilePicURL;
        }

        public String getUsername() {
            return Username;
        }

        public void setUsername(String Username) {
            this.Username = Username;
        }
    }
}
