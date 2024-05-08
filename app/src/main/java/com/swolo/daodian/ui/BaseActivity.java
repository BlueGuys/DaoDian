package com.swolo.daodian.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

/**
 * Created by hezhisu on 2016/12/20.
 */

public abstract class BaseActivity extends Activity {

    private CommonLoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityUtil.pushActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog = new CommonLoadingDialog(this);
    }

    public void showToast(String messsage){
        Toast.makeText(this,messsage,Toast.LENGTH_SHORT).show();
    }

    public void showLoading(){
        if(mLoadingDialog != null && !mLoadingDialog.isShowing()){
            mLoadingDialog.show();
        }
    }

    public void stopLoading(){
        if(mLoadingDialog != null && mLoadingDialog.isShowing()){
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityUtil.pushActivity(this);
    }

}
