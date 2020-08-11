package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetTopCommentedResponse {


    private boolean isSuccess;
    private String message;
    private List<TopResultsBean> topResults;

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

    public List<TopResultsBean> getTopResults() {
        return topResults;
    }

    public void setTopResults(List<TopResultsBean> topResults) {
        this.topResults = topResults;
    }

    public static class TopResultsBean {

        private int ID;
        private int count;
        private VotingBean voting;

        public int getID() {
            return ID;
        }

        public void setID(int ID) {
            this.ID = ID;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public VotingBean getVoting() {
            return voting;
        }

        public void setVoting(VotingBean voting) {
            this.voting = voting;
        }

        public static class VotingBean {

            private MasterVotingViewBean masterVotingView;
            private int selectedChoice;
            private List<ChoiceListBean> choiceList;

            public MasterVotingViewBean getMasterVotingView() {
                return masterVotingView;
            }

            public void setMasterVotingView(MasterVotingViewBean masterVotingView) {
                this.masterVotingView = masterVotingView;
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

            public static class MasterVotingViewBean {

                private int VotingID;
                private String OwnerPicURL;
                private String OwnerNameSurname;
                private String CreationDate;
                private boolean IsCommentAllowed;
                private String Description;
                private int ChoiceCount;
                private boolean IsVotingActive;
                private int OwnerUserID;
                private boolean IsOwnerActive;
                private String FlagCode;
                private int StateID;
                private int CommentCount;
                private boolean IsAnonymous;
                private String OwnerUsername;

                public int getVotingID() {
                    return VotingID;
                }

                public void setVotingID(int VotingID) {
                    this.VotingID = VotingID;
                }

                public String getOwnerPicURL() {
                    return OwnerPicURL;
                }

                public void setOwnerPicURL(String OwnerPicURL) {
                    this.OwnerPicURL = OwnerPicURL;
                }

                public String getOwnerNameSurname() {
                    return OwnerNameSurname;
                }

                public void setOwnerNameSurname(String OwnerNameSurname) {
                    this.OwnerNameSurname = OwnerNameSurname;
                }

                public String getCreationDate() {
                    return CreationDate;
                }

                public void setCreationDate(String CreationDate) {
                    this.CreationDate = CreationDate;
                }

                public boolean isIsCommentAllowed() {
                    return IsCommentAllowed;
                }

                public void setIsCommentAllowed(boolean IsCommentAllowed) {
                    this.IsCommentAllowed = IsCommentAllowed;
                }

                public String getDescription() {
                    return Description;
                }

                public void setDescription(String Description) {
                    this.Description = Description;
                }

                public int getChoiceCount() {
                    return ChoiceCount;
                }

                public void setChoiceCount(int ChoiceCount) {
                    this.ChoiceCount = ChoiceCount;
                }

                public boolean isIsVotingActive() {
                    return IsVotingActive;
                }

                public void setIsVotingActive(boolean IsVotingActive) {
                    this.IsVotingActive = IsVotingActive;
                }

                public int getOwnerUserID() {
                    return OwnerUserID;
                }

                public void setOwnerUserID(int OwnerUserID) {
                    this.OwnerUserID = OwnerUserID;
                }

                public boolean isIsOwnerActive() {
                    return IsOwnerActive;
                }

                public void setIsOwnerActive(boolean IsOwnerActive) {
                    this.IsOwnerActive = IsOwnerActive;
                }

                public String getFlagCode() {
                    return FlagCode;
                }

                public void setFlagCode(String FlagCode) {
                    this.FlagCode = FlagCode;
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

                public boolean isIsAnonymous() {
                    return IsAnonymous;
                }

                public void setIsAnonymous(boolean IsAnonymous) {
                    this.IsAnonymous = IsAnonymous;
                }

                public String getOwnerUsername() {
                    return OwnerUsername;
                }

                public void setOwnerUsername(String OwnerUsername) {
                    this.OwnerUsername = OwnerUsername;
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
}
