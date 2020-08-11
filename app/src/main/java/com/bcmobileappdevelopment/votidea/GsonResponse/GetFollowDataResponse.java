package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetFollowDataResponse {



    private boolean isSuccess;
    private String message;
    private int guideID;
    private List<FollowDataListBean> followDataList;

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

    public int getGuideID() {
        return guideID;
    }

    public void setGuideID(int guideID) {
        this.guideID = guideID;
    }

    public List<FollowDataListBean> getFollowDataList() {
        return followDataList;
    }

    public void setFollowDataList(List<FollowDataListBean> followDataList) {
        this.followDataList = followDataList;
    }

    public static class FollowDataListBean {


        private int ID;
        private boolean IsActive;
        private String ProfilePicURL;
        private String NameSurname;
        private String Username;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
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

        public String getNameSurname() {
            return NameSurname;
        }

        public void setNameSurname(String NameSurname) {
            this.NameSurname = NameSurname;
        }

        public String getUsername() {
            return Username;
        }

        public void setUsername(String Username) {
            this.Username = Username;
        }
    }
}
