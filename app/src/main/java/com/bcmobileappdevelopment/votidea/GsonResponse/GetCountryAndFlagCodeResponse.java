package com.bcmobileappdevelopment.votidea.GsonResponse;

public class GetCountryAndFlagCodeResponse {

    private CountryAndFlagCodeBean countryAndFlagCode;
    private boolean isSuccess;

    public CountryAndFlagCodeBean getCountryAndFlagCode() {
        return countryAndFlagCode;
    }

    public void setCountryAndFlagCode(CountryAndFlagCodeBean countryAndFlagCode) {
        this.countryAndFlagCode = countryAndFlagCode;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public static class CountryAndFlagCodeBean {

        private String name;
        private String alpha3Code;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAlpha3Code() {
            return alpha3Code;
        }

        public void setAlpha3Code(String alpha3Code) {
            this.alpha3Code = alpha3Code;
        }
    }
}
