package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetVotingRecordsProfileResponse {


    private boolean isSuccess;
    private String message;
    private List<ProfileVotingListBean> profileVotingList;

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

    public List<ProfileVotingListBean> getProfileVotingList() {
        return profileVotingList;
    }

    public void setProfileVotingList(List<ProfileVotingListBean> profileVotingList) {
        this.profileVotingList = profileVotingList;
    }

    public static class ProfileVotingListBean {

        private ProfileVotingBean profileVoting;
        private int selectedChoice;
        private List<ChoiceListBean> choiceList;

        public ProfileVotingBean getProfileVoting() {
            return profileVoting;
        }

        public void setProfileVoting(ProfileVotingBean profileVoting) {
            this.profileVoting = profileVoting;
        }

        public int getSelectedChoice() {
            return selectedChoice;
        }

        public void setSelectedChoice(int selectedChoice) {
            this.selectedChoice = selectedChoice;
        }

        public List<ChoiceListBean> getChoiceList() {
            return choiceList;
        }

        public void setChoiceList(List<ChoiceListBean> choiceList) {
            this.choiceList = choiceList;
        }

        public static class ProfileVotingBean {

            private int ID;
            private String CreationDate;
            private int ChoiceCount;
            private String Description;
            private boolean IsActive;
            private int OwnerUserID;
            private int StateID;
            private int CommentCount;
            private boolean IsCommentAllowed;
            private boolean IsAnonymous;

            public int getID() {
                return ID;
            }

            public void setID(int ID) {
                this.ID = ID;
            }

            public String getCreationDate() {
                return CreationDate;
            }

            public void setCreationDate(String CreationDate) {
                this.CreationDate = CreationDate;
            }

            public int getChoiceCount() {
                return ChoiceCount;
            }

            public void setChoiceCount(int ChoiceCount) {
                this.ChoiceCount = ChoiceCount;
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

            public int getOwnerUserID() {
                return OwnerUserID;
            }

            public void setOwnerUserID(int OwnerUserID) {
                this.OwnerUserID = OwnerUserID;
            }

            public int getStateID() {
                return StateID;
            }

            public void setStateID(int StateID) {
                this.StateID = StateID;
            }

            public int getCommentCount() {
                return CommentCount;
            }

            public void setCommentCount(int CommentCount) {
                this.CommentCount = CommentCount;
            }

            public boolean isIsCommentAllowed() {
                return IsCommentAllowed;
            }

            public void setIsCommentAllowed(boolean IsCommentAllowed) {
                this.IsCommentAllowed = IsCommentAllowed;
            }

            public boolean isIsAnonymous() {
                return IsAnonymous;
            }

            public void setIsAnonymous(boolean IsAnonymous) {
                this.IsAnonymous = IsAnonymous;
            }
        }

        public static class ChoiceListBean {

            private int ID;
            private int VotingID;
            private String PictureURL;
            private double Percentage;
            private int ClickedCount;

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

            public String getPictureURL() {
                return PictureURL;
            }

            public void setPictureURL(String PictureURL) {
                this.PictureURL = PictureURL;
            }

            public double getPercentage() {
                return Percentage;
            }

            public void setPercentage(double Percentage) {
                this.Percentage = Percentage;
            }

            public int getClickedCount() {
                return ClickedCount;
            }

            public void setClickedCount(int ClickedCount) {
                this.ClickedCount = ClickedCount;
            }
        }
    }
}
