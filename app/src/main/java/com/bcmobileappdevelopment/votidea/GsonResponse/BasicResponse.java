package com.bcmobileappdevelopment.votidea.GsonResponse;

public class BasicResponse {

    private boolean isSuccess;
    private String message;

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
}
