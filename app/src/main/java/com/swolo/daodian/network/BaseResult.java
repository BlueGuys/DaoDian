package com.swolo.daodian.network;

import com.swolo.daodian.utils.StringUtils;

public class BaseResult {

    private String code;

    private String msg;

    public boolean isSuccessful() {
        return StringUtils.notEmpty(code) && "0".equals(code);
    }

    public String getMsg() {
        if (StringUtils.isEmpty(msg)) {
            return "error";
        }
        return msg;
    }
}
