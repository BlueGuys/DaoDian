package com.swolo.daodian.business.main;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.swolo.daodian.AccountManager;
import com.swolo.daodian.R;
import com.swolo.daodian.business.login.LoginActivity;
import com.swolo.daodian.business.login.UserResult;
import com.swolo.daodian.ui.ActivityUtil;
import com.swolo.daodian.ui.BaseActivity;
import com.swolo.daodian.utils.GsonUtils;

import java.util.ArrayList;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 长连接参考：
 */

public class MainActivity extends BaseActivity {

    private LinearLayout linearSetting;
    private TextView textViewUSB;

    Context context;

    private int nxCommunityUserId;

    public static final String TAG = MainActivity.class.getName();

    private final StringBuilder mStateStringBuilder = new StringBuilder();

    private ListView listView;
    private OrderListAdapter orderListAdapter;


    private PrintService.MyBinder binder;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PrintService.MyBinder) service;
            PrintService printService = binder.getService();

            printService.setPrintServiceListener(new PrintService.PrintServiceListener() {
                @Override
                public void onConnection(String message) {
                    showToast("打印机已连接");
                }

                @Override
                public void onOrderListChange(ArrayList<NewOrderResult.Order> orders) {
                    runOnUiThread(() -> orderListAdapter.notify(orders));
                }

                @Override
                public void notifyState(String message) {
                    notifyPrinterState(message);
                    Log.e("PrintService", message);
                }
            });
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    binder.connectPrinter();
                }
            }, 1000);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "OtherActivity service disconnect: " + name.getClassName());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        linearSetting = findViewById(R.id.linear_setting);
        textViewUSB = findViewById(R.id.tv_usb);
        listView = findViewById(R.id.listview_order);
        orderListAdapter = new OrderListAdapter();
        listView.setAdapter(orderListAdapter);
        if (AccountManager.getInstance().isLogin()) {
            String userInfoStr = AccountManager.getInstance().getUserInfo();
            UserResult result = GsonUtils.gsonResolve(userInfoStr, UserResult.class);
            nxCommunityUserId = result.data.nxCommunityUserId;
            bindService(null);
        } else {
            startActivityForResult(new Intent(this, LoginActivity.class), 100);
        }
        checkPermission();
        mStateStringBuilder.delete(0, mStateStringBuilder.length());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            String userInfoStr = AccountManager.getInstance().getUserInfo();
            UserResult result = GsonUtils.gsonResolve(userInfoStr, UserResult.class);
            nxCommunityUserId = result.data.nxCommunityUserId;
            bindService(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    // 绑定服务
    public void bindService(View view) {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PrintService.class);
        intent.putExtra("userID", String.valueOf(nxCommunityUserId));
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    // 解绑服务
    public void unbindService() {
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, PrintService.class);
        unbindService(connection);
    }

    public void connectPrint(View v) {
        binder.connectPrinter();
    }

    public void testPrint(View v) {
        binder.testPrinter();
    }

    private void checkPermission() {
        boolean hasStorage = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!hasStorage) {
            EasyPermissions.requestPermissions(this, "权限申请原理对话框 : 描述申请权限的原理", 100, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        boolean hasLocation = EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (!hasLocation) {
            EasyPermissions.requestPermissions(this, "权限申请原理对话框 : 描述申请权限的原理", 100, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void notifyPrinterState(String str) {
        mStateStringBuilder.append(str);
        textViewUSB.setText(mStateStringBuilder.toString());
    }

    public void printSetting(View view) {
        linearSetting.setVisibility(View.VISIBLE);
    }

    public void hideSetting(View view) {
        linearSetting.setVisibility(View.GONE);
    }

}
