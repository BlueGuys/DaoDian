package com.swolo.daodian.business.main;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gprinter.bean.PrinterDevices;
import com.gprinter.utils.CallbackListener;
import com.gprinter.utils.Command;
import com.gprinter.utils.ConnMethod;
import com.gprinter.utils.LogUtils;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.kongzue.baseokhttp.util.Parameter;
import com.swolo.daodian.AccountManager;
import com.swolo.daodian.R;
import com.swolo.daodian.business.login.LoginActivity;
import com.swolo.daodian.business.login.UserResult;
import com.swolo.daodian.business.order.OrderListActivity;
import com.swolo.daodian.network.NetworkConfig;
import com.swolo.daodian.ui.ActivityUtil;
import com.swolo.daodian.ui.BaseActivity;
import com.swolo.daodian.utils.GsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 长连接参考：
 */

public class MainActivity extends BaseActivity {

    private LinearLayout linearSetting;
    private TextView textViewUSB;

    Context context;

    private Printer printer = null;
    private int nxCommunityUserId;

    public static final String TAG = MainActivity.class.getName();

    private final StringBuilder mStateStringBuilder = new StringBuilder();

    private ArrayList<NewOrderResult.Order> orderArrayList = new ArrayList<>();
    private Timer mRequestTimer;

    private Timer mPrintTimer;

    private ListView listView;
    private OrderListAdapter orderListAdapter;

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0x00:
                    String tip = (String) msg.obj;
                    showToast(tip);
                    break;
                case 0x01:
                    int status = msg.arg1;
                    if (status == -1) {//获取状态失败
                        notifyState("打印机状态获取失败，请检查打印机是否缺纸或开盖\n");
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setTitle("提示")
                                .setMessage("打印机状态获取失败，请检查打印机是否缺纸或开盖")
                                .setIcon(R.mipmap.ic_launcher)
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                })
                                .create();
                        alertDialog.show();
                        return;
                    } else if (status == 1) {
                        notifyState("状态走纸、打印\n");
                        showToast("状态走纸、打印");
                        return;
                    } else if (status == 0) {//状态正常
                        notifyState("状态正常\n");
                        showToast("状态正常");
                        return;
                    } else if (status == -2) {//状态缺纸
                        notifyState("状态缺纸\n");
                        showToast("状态缺纸");
                        return;
                    } else if (status == -3) {//状态开盖
                        notifyState("状态开盖\n");
                        showToast("状态开盖");
                        return;
                    } else if (status == -4) {
                        notifyState("状态过热\n");
                        showToast("状态过热");
                        return;
                    }
                    break;
                case 0x02://关闭连接
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (printer.getPortManager() != null) {
                                printer.close();
                            }
                        }
                    }).start();
                    textViewUSB.setText("未连接");
                    break;
                case 0x03:
                    showToast((String) msg.obj);
                    break;
            }
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
        } else {
            ActivityUtil.next(this, LoginActivity.class);
        }
        mRequestTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                requestNewestOrder();
            }
        };
        mRequestTimer.schedule(mTimerTask, 1000, 5 * 1000);

        mPrintTimer = new Timer();
        TimerTask printTak = new TimerTask() {
            @Override
            public void run() {
                Iterator<NewOrderResult.Order> it = orderArrayList.iterator();
                while (it.hasNext()) {
                    NewOrderResult.Order order = it.next();
                    printOrder(order);
                }
            }
        };
        mPrintTimer.schedule(printTak, 1000, 5 * 1000);
        getAvailableUSB();
        checkPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                connectUSB();
            }
        }, 2000);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (printer.getPortManager() != null) {
            printer.close();
            notifyState("打印机断开连接");
        }
    }


    public void printOrder(NewOrderResult.Order order) {
        Log.e(TAG, "打印新订单，当前订单数量：" + orderArrayList.size());
        ThreadPoolManager.getInstance().addTask(() -> {
            try {
                if (printer.getPortManager() == null) {
                    tipsToast("请先连接打印机");
                    return;
                }
                boolean result = printer.getPortManager().writeDataImmediately(PrintContent.getOrderLabel(context, 3, order));
                if (result) {
                    commitPrintOrder(order);
                    tipsDialog("打印成功");
                } else {
                    tipsDialog("打印失败");
                }
                LogUtils.e("send result", result);
            } catch (IOException e) {
                tipsDialog("打印失败" + e.getMessage());
            } catch (Exception e) {
                tipsDialog("打印失败" + e.getMessage());
            } finally {
                if (printer.getPortManager() == null) {
                    printer.close();
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        if (mRequestTimer != null) {
            mRequestTimer.cancel();
        }
        if (mPrintTimer != null) {
            mPrintTimer.cancel();
        }
    }

    public void init(View view) {
        mStateStringBuilder.delete(0, mStateStringBuilder.length());
        connectUSB();
    }

    private void notifyState(String str) {
        mStateStringBuilder.append(str);
        textViewUSB.setText(mStateStringBuilder.toString());
    }

    private void checkPermission() {
        notifyState("-------------------------------------------------\n");
        notifyState("2.权限检测:\n");
        boolean hasStorage = EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (hasStorage) {
            notifyState("存储权限:OK\n");
        } else {
            EasyPermissions.requestPermissions(this, "权限申请原理对话框 : 描述申请权限的原理", 100, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        boolean hasLocation = EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasLocation) {
            notifyState("位置权限:OK\n");
        } else {
            EasyPermissions.requestPermissions(this, "权限申请原理对话框 : 描述申请权限的原理", 100, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    private PrinterDevices mUsb;

    /**
     * 获取可用的usb
     */
    private void getAvailableUSB() {
        //如果打印机持有的端口管理器为空，则重新连接
        notifyState("-------------------------------------------------\n");
        notifyState("1.获取USB:\n");
        notifyState("onCreate 寻找设备的USB端口...\n");
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
        if (count == 0) {
            notifyState("获取到可用的usb端口数量为0\n");
            return;
        }
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            String deviceName = usbDevice.getDeviceName();
            notifyState("获取到的usb:" + deviceName + "\n");
            this.mUsb = new PrinterDevices.Build()
                    .setContext(MainActivity.this)
                    .setConnMethod(ConnMethod.USB)
                    .setUsbDevice(usbDevice)
                    .setCommand(Command.TSC)
                    .setCallbackListener(new CallbackListener() {
                        @Override
                        public void onConnecting() {
                            notifyState("连接中...\n");
                        }

                        @Override
                        public void onCheckCommand() {
                            notifyState("查询中...\n");
                        }

                        @Override
                        public void onSuccess(PrinterDevices printerDevices) {
                            notifyState("已连接\n");
                            checkPrinter();
                        }

                        @Override
                        public void onReceive(byte[] data) {
                            notifyState("连接中...\n");
                        }

                        @Override
                        public void onFailure() {
                            notifyState("连接失败...\n");
                        }

                        @Override
                        public void onDisconnect() {
                            notifyState("断开连接...\n");
                        }
                    })
                    .build();
        }
    }

    private void connectUSB() {
        notifyState("-------------------------------------------------\n");
        notifyState("3.连接打印机:\n");
        //如果打印机持有的端口管理器不为空，则检查状态
        if (printer.getPortManager() != null) {
            notifyState("主动断开打印机\n");
            printer.close();
        }
        if (this.mUsb != null) {
            notifyState("打印机连接：Printer.connect(usb)\n");
            Printer.connect(this.mUsb);
        } else {
            notifyState("打印机连接：Printer.connect(usb) this.mUsb == null\n");
        }
    }

    /**
     * 请求未打印订单
     */
    private void requestNewestOrder() {
        HttpRequest.POST(MainActivity.this, NetworkConfig.getUnPrintSubOrderUrl(), new Parameter().add("commId", String.valueOf(nxCommunityUserId)).add("status", "1"), new ResponseListener() {
            @Override
            public void onResponse(String main, Exception error) {
                NewOrderResult result = GsonUtils.gsonResolve(main, NewOrderResult.class);
                if (result != null && result.isSuccessful()) {
                    ArrayList<NewOrderResult.Order> list = result.data;
                    if (list == null) {
                        return;
                    }
                    for (int i = 0; i < list.size(); i++) {
                        NewOrderResult.Order order = list.get(i);
                        boolean hasContain = false;
                        for (NewOrderResult.Order orderOrigin : orderArrayList) {
                            if (Objects.equals(order.nxCommunityOrdersPrintSubId, orderOrigin.nxCommunityOrdersPrintSubId)) {
                                hasContain = true;
                            }
                        }
                        if (!hasContain) {
                            orderArrayList.add(order);
                        }
                    }
                    orderListAdapter.notify(orderArrayList);
                }
            }
        });
    }

    private void commitPrintOrder(NewOrderResult.Order order) {
        HttpRequest.POST(MainActivity.this, NetworkConfig.getCommitSubOrders(), new Parameter().add("subOrderId", order.nxCommunityOrdersPrintSubId).add("status", "2"), new ResponseListener() {
            @Override
            public void onResponse(String main, Exception error) {
                PrintOrderResult result = GsonUtils.gsonResolve(main, PrintOrderResult.class);
                Log.e(TAG, "获取新订单");
                if (result != null && result.isSuccessful()) {
                    orderArrayList.remove(order);
                    orderListAdapter.notify(orderArrayList);
                }
            }
        });
    }

    /**
     * 检查标签打印机状态
     */
    public void checkPrinter() {
        notifyState("检测打印机状态\n");
        if (printer.getPortManager() == null) {
            notifyState("请先连接打印机\n");
            return;
        }
        ThreadPoolManager.getInstance().addTask(() -> {
            try {
                Command command = printer.getPortManager().getCommand();
                int status = printer.getPrinterState(command, 2000);
                Message msg = new Message();
                msg.what = 0x01;
                msg.arg1 = status;
                handler.sendMessage(msg);
            } catch (Exception e) {
            }
        });
    }

    /**
     * 打印样张
     */
    public void testPrint(View view) {
        ThreadPoolManager.getInstance().addTask(() -> {
            try {
                if (printer.getPortManager() == null) {
                    tipsToast("请先连接打印机");
                    return;
                }
                boolean result = printer.getPortManager().writeDataImmediately(PrintContent.getLabel(context, 3));
                if (result) {
                    tipsDialog("发送成功");
                } else {
                    tipsDialog("发送失败");
                }
                LogUtils.e("send result", result);
            } catch (IOException e) {
                tipsDialog("打印失败" + e.getMessage());
            } catch (Exception e) {
                tipsDialog("打印失败" + e.getMessage());
            } finally {
                if (printer.getPortManager() == null) {
                    printer.close();
                }
            }
        });

    }

    public void jumpOrderList(View view) {
        ActivityUtil.next(this, OrderListActivity.class);
    }

    public void printSetting(View view) {
        linearSetting.setVisibility(View.VISIBLE);
    }

    public void hideSetting(View view) {
        linearSetting.setVisibility(View.GONE);
    }

    /**
     * 提示弹框
     *
     * @param message
     */
    private void tipsToast(String message) {
        Message msg = new Message();
        msg.what = 0x00;
        msg.obj = message;
        handler.sendMessage(msg);
    }

    /**
     * 提示弹框
     *
     * @param message
     */
    private void tipsDialog(String message) {
        Message msg = new Message();
        msg.what = 0x03;
        msg.obj = message;
        handler.sendMessage(msg);
    }

}
