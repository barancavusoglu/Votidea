package com.bcmobileappdevelopment.votidea.GsonResponse;

import java.util.List;

public class GetTopUserResponse {

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
        private UserBean user;

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

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean {

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
}
