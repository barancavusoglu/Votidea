package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class LoginResponse {

    public void addFollowing(Integer following){
        this.getLoginView().FollowingCount++;
        this.followingList.add(following);
    }

    public void removeFollowing(Integer following){
        this.getLoginView().FollowingCount--;
        this.followingList.remove(following);
    }

    private boolean isSuccess;
    private String message;
    private String token;
    private LoginViewBean loginView;
    private List<Integer> followingList;
    private List<Integer> followersList;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginViewBean getLoginView() {
        return loginView;
    }

    public void setLoginView(LoginViewBean loginView) {
        this.loginView = loginView;
    }

    public List<Integer> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<Integer> followingList) {
        this.followingList = followingList;
    }



    public List<Integer> getFollowersList() {
        return followersList;
    }

    public void setFollowersList(List<Integer> followersList) {
        this.followersList = followersList;
    }

    public static class LoginViewBean {

        private int ID;
        private String Email;
        private String AccountType;
        private String AccountID;
        private boolean IsActive;
        private boolean IsEmailApproved;
        private String NameSurname;
        private boolean IsHiddenProfile;
        private String ProfilePicURL;
        private int CommentCount;
        private String City;
        private String Country;
        private int HasVoteCount;
        private int VotedCount;
        private String Username;
        private String FlagCode;
        private int FollowingCount;
        private int FollowerCount;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public String getEmail() {
            return Email;
        }

        public void setEmail(String Email) {
            this.Email = Email;
        }

        public String getAccountType() {
            return AccountType;
        }

        public void setAccountType(String AccountType) {
            this.AccountType = AccountType;
        }

        public String getAccountID() {
            return AccountID;
        }

        public void setAccountID(String AccountID) {
            this.AccountID = AccountID;
        }

        public boolean isIsActive() {
            return IsActive;
        }

        public void setIsActive(boolean IsActive) {
            this.IsActive = IsActive;
        }

        public boolean isIsEmailApproved() {
            return IsEmailApproved;
        }

        public void setIsEmailApproved(boolean IsEmailApproved) {
            this.IsEmailApproved = IsEmailApproved;
        }

        public String getNameSurname() {
            return NameSurname;
        }

        public void setNameSurname(String NameSurname) {
            this.NameSurname = NameSurname;
        }

        public boolean isIsHiddenProfile() {
            return IsHiddenProfile;
        }

        public void setIsHiddenProfile(boolean IsHiddenProfile) {
            this.IsHiddenProfile = IsHiddenProfile;
        }

        public String getProfilePicURL() {
            return ProfilePicURL;
        }

        public void setProfilePicURL(String ProfilePicURL) {
            this.ProfilePicURL = ProfilePicURL;
        }

        public int getCommentCount() {
            return CommentCount;
        }

        public void setCommentCount(int CommentCount) {
            this.CommentCount = CommentCount;
        }

        public String getCity() {
            return City;
        }

        public void setCity(String City) {
            this.City = City;
        }

        public String getCountry() {
            return Country;
        }

        public void setCountry(String Country) {
            this.Country = Country;
        }

        public int getHasVoteCount() {
            return HasVoteCount;
        }

        public void setHasVoteCount(int HasVoteCount) {
            this.HasVoteCount = HasVoteCount;
        }

        public int getVotedCount() {
            return VotedCount;
        }

        public void setVotedCount(int VotedCount) {
            this.VotedCount = VotedCount;
        }

        public String getUsername() {
            return Username;
        }

        public void setUsername(String Username) {
            this.Username = Username;
        }

        public String getFlagCode() {
            return FlagCode;
        }

        public void setFlagCode(String FlagCode) {
            this.FlagCode = FlagCode;
        }

        public int getFollowingCount() {
            return FollowingCount;
        }

        public void setFollowingCount(int FollowingCount) {
            this.FollowingCount = FollowingCount;
        }

        public int getFollowerCount() {
            return FollowerCount;
        }

        public void setFollowerCount(int FollowerCount) {
            this.FollowerCount = FollowerCount;
        }
    }
}
