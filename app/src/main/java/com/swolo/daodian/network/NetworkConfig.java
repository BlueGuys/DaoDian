package com.swolo.daodian.network;

public class NetworkConfig {

    public static final String BASE_URL = "https://grainservice.club:8445/nongxinle/";
    public static final String BASE_WS = "ws://grainservice.club:8443/nongxinle/";

    /**
     * 登录接口
     */
    public static String getLoginUrl() {
        return BASE_URL + "api/nxcommunityuser/comUserLoginAndroid/";
    }

    /**
     * 获取未打印订单
     */
    public static String getUnPrintSubOrderUrl() {
        return BASE_URL + "api/nxorderssub/getUnPrintSubOrders";
    }

    /**
     * 提交订单
     */
    public static String getCommitSubOrders() {
        return BASE_URL + "api/nxorderssub/printSubOrders";
    }

    /**
     * 用于请求关注
     */
    public static String getWebSocketUrl() {
        return BASE_WS + "webSocketIMServer";
    }

}
