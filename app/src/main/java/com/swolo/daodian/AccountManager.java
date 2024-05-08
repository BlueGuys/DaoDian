package com.swolo.daodian;

import com.swolo.daodian.storage.PreferenceUtil;
import com.swolo.daodian.utils.StringUtils;

public class AccountManager  {

    private static AccountManager mInstance;

    private String mUserInfo;


    public static synchronized AccountManager getInstance() {
        if (mInstance == null) {
            mInstance = new AccountManager();
        }
        return mInstance;
    }

    /**
     * 判断当前用户是否登录
     */
    public boolean isLogin() {
        String userInfo = PreferenceUtil.getString(PurchaseApplication.getAppContext(), "user");
        if (StringUtils.isEmpty(userInfo)) {
            return false;
        } else {
            this.mUserInfo = userInfo;
            return true;
        }
    }

    public String getUserInfo() {
        return mUserInfo;
    }


    public void setUserInfo(String mUserInfo) {
        this.mUserInfo = mUserInfo;
        PreferenceUtil.putString(PurchaseApplication.getAppContext(), "user", mUserInfo);
    }
}
