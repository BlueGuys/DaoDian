package com.swolo.daodian.business.main;

import com.swolo.daodian.business.login.NoProguard;
import com.swolo.daodian.network.BaseResult;

import java.util.ArrayList;

/**
 * Created by hezhisu on 2016/12/21.
 */

public class NewOrderResult extends BaseResult {

    public ArrayList<Order> data;

    public static class Data implements NoProguard {

        public ArrayList<Order> orderList;

    }

    public static class Order implements NoProguard {

        public String nxCommunityOrdersPrintSubId;
        /**
         * 取餐号
         */
        public String nxCospPickUpCode;

        public NxCommunityGoodsEntity nxCommunityGoodsEntity;
        /**
         * 数量
         */
        public String nxCospQuantity;
        /**
         * 备注
         */
        public String nxCospRemark;
    }

    public static class NxCommunityGoodsEntity implements NoProguard {
        /**
         * 商品名称
         */
        public String nxCgGoodsName;
    }

}
