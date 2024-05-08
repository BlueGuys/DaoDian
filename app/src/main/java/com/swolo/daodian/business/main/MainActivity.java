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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gprinter.bean.PrinterDevices;
import com.gprinter.utils.CallbackListener;
import com.gprinter.utils.Command;
import com.gprinter.utils.ConnMethod;
import com.gprinter.utils.LogUtils;
import com.swolo.daodian.R;
import com.swolo.daodian.ui.BaseActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity {

    private TextView textViewUSB;

    Context context;

    private Printer printer = null;

    private StringBuilder mStateStringBuilder = new StringBuilder();

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
                    String message = (String) msg.obj;
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("提示")
                            .setMessage(message)
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            })
                            .create();
                    alertDialog.show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
        textViewUSB = findViewById(R.id.tv_usb);
        init(null);
    }

    public void init(View view) {
        mStateStringBuilder.delete(0, mStateStringBuilder.length());
        checkPermission();
        connectUSB();
        checkPrinter();
    }

    private void notifyState(String str) {
        mStateStringBuilder.append(str);
        textViewUSB.setText(mStateStringBuilder.toString());
    }

    private boolean checkPermission() {
        notifyState("1.权限检测:\n");
        if (EasyPermissions.hasPermissions(this, Manifest.permission.READ_EXTERNAL_STORAGE) && EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            notifyState("存储权限:OK\n");
            notifyState("位置权限:OK\n");
            return true;
        }
        EasyPermissions.requestPermissions(
                this,
                "权限申请原理对话框 : 描述申请权限的原理",
                100,
                // 下面是要申请的权限 可变参数列表
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION
        );
        return false;
    }

    private boolean connectUSB() {
        notifyState("-------------------------------------------------\n");
        notifyState("2.USB检测:\n");
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        // Get the list of attached devices
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
        if (count > 0) {
            while (deviceIterator.hasNext()) {
                UsbDevice usbDevice = deviceIterator.next();
                String deviceName = usbDevice.getDeviceName();
                PrinterDevices usb = new PrinterDevices.Build()
                        .setContext(MainActivity.this)
                        .setConnMethod(ConnMethod.USB)
                        .setUsbDevice(usbDevice)
                        .setCommand(Command.TSC)
                        .setCallbackListener(new CallbackListener() {
                            @Override
                            public void onConnecting() {
                                notifyState("连接中...");
                            }

                            @Override
                            public void onCheckCommand() {
                                notifyState("查询中...");
                            }

                            @Override
                            public void onSuccess(PrinterDevices printerDevices) {
                                notifyState("已连接");
                            }

                            @Override
                            public void onReceive(byte[] data) {
                                notifyState("连接中...");
                            }

                            @Override
                            public void onFailure() {
                                notifyState("连接失败...");
                            }

                            @Override
                            public void onDisconnect() {
                                notifyState("断开连接...");
                            }
                        })
                        .build();
                printer = Printer.getInstance();
                printer.connect(usb);
                return true;
            }
        } else {
            notifyState("usb连接失败");
        }
        return false;
    }

    /**
     * 检查标签打印机状态
     */
    public void checkPrinter() {
        notifyState("-------------------------------------------------\n");
        notifyState("打印机检测:\n");
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                if (printer.getPortManager() == null) {
                    notifyState("请先连接打印机\n");
                    return;
                }
                try {
                    Command command = printer.getPortManager().getCommand();
                    int status = printer.getPrinterState(command, 2000);
                    Message msg = new Message();
                    msg.what = 0x01;
                    msg.arg1 = status;
                    handler.sendMessage(msg);
                } catch (IOException e) {
                    notifyState("状态获取异常\n");
                } catch (Exception e) {
                    notifyState("状态获取异常\n");
                }
            }
        });
    }

    boolean checkUsbDevicePidVid(UsbDevice dev) {
        int pid = dev.getProductId();
        int vid = dev.getVendorId();
        return ((vid == 34918 && pid == 256) || (vid == 1137 && pid == 85)
                || (vid == 6790 && pid == 30084)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 512)
                || (vid == 26728 && pid == 256) || (vid == 26728 && pid == 768)
                || (vid == 26728 && pid == 1024) || (vid == 26728 && pid == 1280)
                || (vid == 26728 && pid == 1536));
    }


    /**
     * 打印案例
     *
     * @param view
     */
    public void print(View view) {
        ThreadPoolManager.getInstance().addTask(new Runnable() {
            @Override
            public void run() {
                try {
                    if (printer.getPortManager() == null) {
                        tipsToast("请先连接打印机");
                        return;
                    }
                    // TODO
                    //打印前后查询打印机状态，部分老款打印机不支持查询请去除下面查询代码
                    //******************     查询状态     ***************************
//                    if (swState.isChecked()) {
//                        Command command = printer.getPortManager().getCommand();
//                        int status = printer.getPrinterState(command,2000);
//                        if (status != 0) {//打印机处于不正常状态,则不发送打印任务
//                            Message msg = new Message();
//                            msg.what = 0x01;
//                            msg.arg1 = status;
//                            handler.sendMessage(msg);
//                            return;
//                        }
//                    }
                    //***************************************************************
                    // TODO 修复
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
            }
        });

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
