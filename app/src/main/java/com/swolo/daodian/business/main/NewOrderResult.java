package com.swolo.daodian.business.main;

import com.swolo.daodian.business.login.NoProguard;
import com.swolo.daodian.network.BaseResult;

import java.util.ArrayList;

/**
 * Created by hezhisu on 2016/12/21.
 */

public class NewOrderResult extends BaseResult {

    public Data data;

    public static class Data implements NoProguard {

        public ArrayList<Order> orderList;

    }

    public static class Order implements NoProguard {

        public String orderId;
        public String orderPickupNumber;
        public String orderGoodsName;
        public String orderGoodsCount;
        public String remark;

    }

}
