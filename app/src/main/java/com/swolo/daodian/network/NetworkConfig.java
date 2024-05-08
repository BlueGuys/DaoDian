package com.swolo.daodian.network;

public class NetworkConfig {

    public static final String BASE_URL = "https://grainservice.club:8443/nongxinle/";
    public static final String BASE_WS = "ws://grainservice.club:8443/nongxinle/";

    /**
     * 用于请求关注
     */
    public static String getLoginUrl() {
        return BASE_URL + "api/nxcommunityuser/comUserLoginAndroid/";
    }
}
