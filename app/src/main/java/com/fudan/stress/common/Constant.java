package com.fudan.stress.common;

public class Constant {
    public static final int IS_LOG = 1;
    //login
    public static final int REQUEST_SIGN_IN_LOGIN = 1002;
    //login by code
    public static final int REQUEST_SIGN_IN_LOGIN_CODE = 1003;

    public static final int SIGN_IN_HW = 1004;

    public static final int REQUEST_SLEEP_DATA = 1005;

    public static final int RESULT_OK = 200;

    //project code
    public static final String PROJECT_CODE = "c8644ab6";
    /**
     * your app’s client ID,please replace it of yours
     */
    public static final String CLIENT_ID = "102244693";

    /**
     * JWK JSON Web Key endpoint, developer can get the JWK of the last two days from this endpoint
     * See more about JWK in http://self-issued.info/docs/draft-ietf-jose-json-web-key.html
     */
    public static final String CERT_URL = "https://oauth-login.cloud.huawei.com/oauth2/v3/certs";
    /**
     *  Id Token issue
     */
    public static final String ID_TOKEN_ISSUE = "https://accounts.huawei.com";
}
