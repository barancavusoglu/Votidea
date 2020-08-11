package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetVotingRecordsResponse {


    private boolean isSuccess;
    private String message;
    private List<ChoiceListBean> choiceList;

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

    public List<ChoiceListBean> getChoiceList() {
        return choiceList;
    }

    public void setChoiceList(List<ChoiceListBean> choiceList) {
        this.choiceList = choiceList;
    }

    public static class ChoiceListBean {

        private String PictureURL;
        private int ClickedCount;
        private int VotingID;
        private String NameSurname;
        private int UserID;
        private int Percentage;
        private int ChoiceID;

        public String getPictureURL() {
            return PictureURL;
        }

        public void setPictureURL(String PictureURL) {
            this.PictureURL = PictureURL;
        }

        public int getClickedCount() {
            return ClickedCount;
        }

        public void setClickedCount(int ClickedCount) {
            this.ClickedCount = ClickedCount;
        }

        public int getVotingID() {
            return VotingID;
        }

        public void setVotingID(int VotingID) {
            this.VotingID = VotingID;
        }

        public String getNameSurname() {
            return NameSurname;
        }

        public void setNameSurname(String NameSurname) {
            this.NameSurname = NameSurname;
        }

        public int getUserID() {
            return UserID;
        }

        public void setUserID(int UserID) {
            this.UserID = UserID;
        }

        public int getPercentage() {
            return Percentage;
        }

        public void setPercentage(int Percentage) {
            this.Percentage = Percentage;
        }

        public int getChoiceID() {
            return ChoiceID;
        }

        public void setChoiceID(int ChoiceID) {
            this.ChoiceID = ChoiceID;
        }
    }
}
