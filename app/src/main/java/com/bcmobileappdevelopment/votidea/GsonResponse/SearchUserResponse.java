package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class SearchUserResponse {


    private boolean isSuccess;
    private List<UserListBean> userList;

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public List<UserListBean> getUserList() {
        return userList;
    }

    public void setUserList(List<UserListBean> userList) {
        this.userList = userList;
    }

    public static class UserListBean {

        private int ID;
        private boolean IsActive;
        private String NameSurname;
        private String ProfilePicURL;
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

        public String getNameSurname() {
            return NameSurname;
        }

        public void setNameSurname(String NameSurname) {
            this.NameSurname = NameSurname;
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
