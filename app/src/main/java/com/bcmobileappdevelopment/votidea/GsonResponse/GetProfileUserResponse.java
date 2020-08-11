package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetProfileUserResponse {

    public void addFollower(Integer follower){
        this.getUser().FollowerCount++;
        this.followerList.add(follower);
    }

    public void removeFollower(Integer follower){
        this.getUser().FollowerCount--;
        this.followerList.remove(follower);
    }


    private boolean isSuccess;
    private String message;
    private UserBean user;
    private List<Integer> followingList;
    private List<Integer> followerList;

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

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public List<Integer> getFollowingList() {
        return followingList;
    }

    public void setFollowingList(List<Integer> followingList) {
        this.followingList = followingList;
    }

    public List<Integer> getFollowerList() {
        return followerList;
    }

    public void setFollowerList(List<Integer> followerList) {
        this.followerList = followerList;
    }

    public static class UserBean {

        private int ID;
        private boolean IsActive;
        private String NameSurname;
        private boolean IsHiddenProfile;
        private String ProfilePicURL;
        private int CommentCount;
        private String City;
        private String Country;
        private int HasVoteCount;
        private int VotedCount;
        private String FlagCode;
        private int FollowingCount;
        private int FollowerCount;
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

        public String getUsername() {
            return Username;
        }

        public void setUsername(String Username) {
            this.Username = Username;
        }
    }
}
