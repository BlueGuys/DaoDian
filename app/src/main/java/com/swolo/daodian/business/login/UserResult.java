package com.swolo.daodian.business.login;

import com.swolo.daodian.network.BaseResult;

/**
 * Created by hezhisu on 2016/12/21.
 */

public class UserResult extends BaseResult {

    public Data data;

    public class Data implements NoProguard {


        public int nxCommunityUserId;
        /**
         *  用户名
         */
        public String nxCouWxAvartraUrl;
        /**
         *  登陆密码
         */
        public String nxCouWxNickName;
        /**
         *
         */
        public String nxCouWxOpenId;
        /**
         *
         */
        public String nxCouWxPhone;
        /**
         *
         */
        public int nxCouCommunityId;
        public Boolean socketOn;
        /**
         *
         */
        public int nxCouAdmin;

        public int nxCouCode;
        public int nxCouRoleId;
        public int nxCouWorkingStatus;
    }
}
