package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class VoteResponse {


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
