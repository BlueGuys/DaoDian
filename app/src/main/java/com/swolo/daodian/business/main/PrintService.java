package com.swolo.daodian.business.main;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gprinter.bean.PrinterDevices;
import com.gprinter.utils.CallbackListener;
import com.gprinter.utils.Command;
import com.gprinter.utils.ConnMethod;
import com.gprinter.utils.LogUtils;
import com.kongzue.baseokhttp.HttpRequest;
import com.kongzue.baseokhttp.listener.ResponseListener;
import com.kongzue.baseokhttp.util.Parameter;
import com.swolo.daodian.R;
import com.swolo.daodian.network.NetworkConfig;
import com.swolo.daodian.utils.GsonUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class PrintService extends Service {
    private final static String TAG = "PrintService";
    private MyBinder mBinder = new MyBinder();

    private Timer mRequestTimer;

    private Timer mPrintTimer;

    private Printer printer;

    private ArrayList<NewOrderResult.Order> orderArrayList = new ArrayList<>();

    private String mUserID;

    interface PrintServiceListener {
        void onConnection(String message);

        void onOrderListChange(ArrayList<NewOrderResult.Order> orders);

        void notifyState(String message);
    }

    private PrintServiceListener mPrintServiceListener;

    public void setPrintServiceListener(PrintServiceListener mPrintServiceListener) {
        this.mPrintServiceListener = mPrintServiceListener;
    }

    class MyBinder extends Binder {
        public void connectPrinter() {
            Log.e(TAG, TAG + "connectPrinter");
            connectPrinter1();
        }

        public void testPrinter() {
            Log.e(TAG, TAG + "testPrinter");
            testPrinter1();
        }

        public PrintService getService() {
            return PrintService.this;
        }
    }

    public PrintService() {
        Log.e(TAG, "NormalServices constructor.");
    }

    private MediaPlayer mMediaPlayer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "on create.");
        mRequestTimer = new Timer();
        printer = Printer.getInstance();
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
        mMediaPlayer = MediaPlayer.create(this, R.raw.bird);
    }

    public void printOrder(NewOrderResult.Order order) {
        Log.e(TAG, "打印新订单，当前订单数量：" + orderArrayList.size() + "\n");
        ThreadPoolManager.getInstance().addTask(() -> {
            try {
                if (printer.getPortManager() == null) {
                    tipsToast("请先连接打印机\n");
                    return;
                }
                boolean result = printer.getPortManager().writeDataImmediately(PrintContent.getOrderLabel(PrintService.this, 3, order));
                if (result) {
                    commitPrintOrder(order);
                    tipsToast("打印成功\n");
                } else {
                    tipsToast("打印失败\b");
                }
                LogUtils.e("send result", result);
            } catch (IOException e) {
                tipsToast("打印失败" + e.getMessage() + "\n");
            } catch (Exception e) {
                tipsToast("打印失败" + e.getMessage() + "\n");
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "on start command.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        mUserID = intent.getStringExtra("userID");
        Log.e(TAG, "on bind" + ": username: " + mUserID);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        String username = intent.getStringExtra("username");
        Log.e(TAG, "on unbind: " + super.onUnbind(intent) + ", username: " + username);
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.e(TAG, "on rebind");
        super.onRebind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "on onDestroy");
        super.onDestroy();
        if (mRequestTimer != null) {
            mRequestTimer.cancel();
        }
        if (mPrintTimer != null) {
            mPrintTimer.cancel();
        }
    }

    /**
     * 获取可用的usb
     */
    private void connectPrinter1() {
        //如果打印机持有的端口管理器为空，则重新连接
        notifyState("-------------------------------------------------\n");
        notifyState("1.连接打印机:\n");
        notifyState("先关闭打印机，等待2.20s\n");
        Printer.close();
        try {
            Thread.sleep(2200);
        } catch (InterruptedException e) {
        }
        notifyState("寻找设备的USB端口...\n");
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> devices = manager.getDeviceList();
        Iterator<UsbDevice> deviceIterator = devices.values().iterator();
        int count = devices.size();
        if (count == 0) {
            notifyState("获取到可用的usb端口数量为0\n");
            return;
        }
        notifyState("获取到可用的usb端口数量为:" + count + "\n");
        while (deviceIterator.hasNext()) {
            UsbDevice usbDevice = deviceIterator.next();
            String deviceName = usbDevice.getDeviceName();
            notifyState("获取到的usb:" + deviceName + "Vid="+ usbDevice.getVendorId() + "pid="+ usbDevice.getProductId());
            PrinterDevices usb = new PrinterDevices.Build()
                    .setContext(PrintService.this)
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
            notifyState("Printer.connect(usb)\n");
            Printer.connect(usb);
        }
    }

    /**
     * 检查USB设备的PID与VID
     * @param dev
     * @return
     */
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
     * 检查标签打印机状态
     */
    public void checkPrinter() {
        notifyState("检查打印机状态\n");
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
     * 请求未打印订单
     */
    private void requestNewestOrder() {
        Log.e(TAG, "正在获取未打印订单,requestNewestOrder");
        HttpRequest.POST(PrintService.this, NetworkConfig.getUnPrintSubOrderUrl(), new Parameter().add("commId", String.valueOf(mUserID)).add("status", "1"), new ResponseListener() {
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
                            mPrintServiceListener.onOrderListChange(orderArrayList);
                            Log.e(TAG, "发现新订单：" + order.nxCommunityOrdersPrintSubId);
                            mMediaPlayer.start();
                        }
                    }
                }
            }
        });
    }

    private void commitPrintOrder(NewOrderResult.Order order) {
        Log.e(TAG, "commitOrder" + order.nxCommunityOrdersPrintSubId);
        HttpRequest.POST(PrintService.this, NetworkConfig.getCommitSubOrders(), new Parameter().add("subOrderId", order.nxCommunityOrdersPrintSubId).add("status", "2"), new ResponseListener() {
            @Override
            public void onResponse(String main, Exception error) {
                PrintOrderResult result = GsonUtils.gsonResolve(main, PrintOrderResult.class);
                Log.e(TAG, "获取新订单");
                if (result != null && result.isSuccessful()) {
                    orderArrayList.remove(order);
                    mPrintServiceListener.onOrderListChange(orderArrayList);
                }
            }
        });
    }

    /**
     * 打印样张
     */
    public void testPrinter1() {
        notifyState("打印样张:\n");
        ThreadPoolManager.getInstance().addTask(() -> {
            try {
                if (printer.getPortManager() == null) {
                    tipsToast("请先连接打印机\n");
                    return;
                }
                boolean result = printer.getPortManager().writeDataImmediately(PrintContent.getLabel(PrintService.this, 3));
                if (result) {
                    tipsToast("发送成功\n");
                } else {
                    tipsToast("发送失败\n");
                }
                LogUtils.e("send result", result);
            } catch (IOException e) {
                tipsToast("打印失败" + e.getMessage() + "\n");
            } catch (Exception e) {
                tipsToast("打印失败" + e.getMessage() + "\n");
            }
        });

    }

    private void notifyState(String str) {
        this.mPrintServiceListener.notifyState(str);
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 0x00:
                    String tip = (String) msg.obj;
                    notifyState(tip);
                    break;
                case 0x01:
                    int status = msg.arg1;
                    if (status == -1) {//获取状态失败
                        notifyState("打印机状态获取失败，请检查打印机是否缺纸或开盖\n");
                        return;
                    } else if (status == 1) {
                        notifyState("状态走纸、打印\n");
                        return;
                    } else if (status == 0) {//状态正常
                        notifyState("状态正常\n");
                        return;
                    } else if (status == -2) {//状态缺纸
                        notifyState("状态缺纸\n");
                        return;
                    } else if (status == -3) {//状态开盖
                        notifyState("状态开盖\n");
                        return;
                    } else if (status == -4) {
                        notifyState("状态过热\n");
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
                    notifyState("未连接");
                    break;
            }
        }
    };

    private void tipsToast(String message) {
        Message msg = new Message();
        msg.what = 0x00;
        msg.obj = message;
        handler.sendMessage(msg);
    }

}
