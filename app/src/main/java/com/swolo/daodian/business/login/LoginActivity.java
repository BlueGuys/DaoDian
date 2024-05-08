package com.swolo.daodian.business.login;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.kongzue.baseokhttp.util.Parameter;
import com.swolo.daodian.AccountManager;
import com.swolo.daodian.R;
import com.swolo.daodian.network.NetworkConfig;
import com.swolo.daodian.ui.BaseActivity;
import com.swolo.daodian.utils.GsonUtils;
import com.swolo.daodian.utils.StringUtils;

public class LoginActivity extends BaseActivity {

    private EditText etUserPhone;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserPhone = findViewById(R.id.et_login_username);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(view -> login());
    }

    private void login() {
        String userPhoneNumber = etUserPhone.getText().toString();
        userPhoneNumber = "123";
        if (StringUtils.isEmpty(userPhoneNumber)) {
            showToast("请输入手机号");
        } else {
            showLoading();
            HttpRequest.POST(LoginActivity.this, NetworkConfig.getLoginUrl() + userPhoneNumber, new Parameter().add("page", "1"), new ResponseListener() {
                @Override
                public void onResponse(String main, Exception error) {
                    stopLoading();
                    UserResult result = GsonUtils.gsonResolve(main, UserResult.class);
                    if (result.isSuccessful()) {
                        showToast("登录成功！");
                        AccountManager.getInstance().setUserInfo(GsonUtils.toJson(result.data));
                    } else {
                        showToast(result.getMsg());
                    }
                }
            });
        }
    }

}
