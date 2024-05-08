package com.swolo.daodian.business.main;

import android.app.Activity;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.gprinter.bean.PrinterDevices;
import com.gprinter.utils.CallbackListener;
import com.gprinter.utils.Command;
import com.gprinter.utils.ConnMethod;
import com.swolo.daodian.R;
import com.swolo.daodian.business.usb.UsbDeviceActivity;
import com.swolo.daodian.ui.BaseActivity;
import com.swolo.daodian.utils.Utils;

public class MainActivity extends BaseActivity {

    private Button buttonUSB;
    private TextView textViewUSB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewUSB = findViewById(R.id.tv_usb);
        buttonUSB = findViewById(R.id.btn_usb);
        buttonUSB.setOnClickListener(view -> {
            startActivityForResult(new Intent(MainActivity.this, UsbDeviceActivity.class),0x01);
        });
//        if (AccountManager.getInstance().isLogin()) {
//            startActivity(new Intent(MainActivity.this, OrderListActivity.class));
//        } else {
//            startActivity(new Intent(MainActivity.this, LoginActivity.class));
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case 0x01://usb返回USB名称
                    String name =data.getStringExtra(UsbDeviceActivity.USB_NAME);
                    UsbDevice usbDevice = Utils.getUsbDeviceFromName(MainActivity.this, name);
                    PrinterDevices usb=new PrinterDevices.Build()
                            .setContext(MainActivity.this)
                            .setConnMethod(ConnMethod.USB)
                            .setUsbDevice(usbDevice)
                            .setCommand(Command.TSC)
                            .setCallbackListener(new CallbackListener() {
                                @Override
                                public void onConnecting() {
                                    textViewUSB.setText("连接中...");
                                }

                                @Override
                                public void onCheckCommand() {
                                    textViewUSB.setText("查询中...");
                                }

                                @Override
                                public void onSuccess(PrinterDevices printerDevices) {
                                    textViewUSB.setText("已连接");
                                }

                                @Override
                                public void onReceive(byte[] data) {
                                    textViewUSB.setText("连接中...");
                                }

                                @Override
                                public void onFailure() {
                                    textViewUSB.setText("连接失败...");
                                }

                                @Override
                                public void onDisconnect() {
                                    textViewUSB.setText("断开连接...");
                                }
                            })
                            .build();
//                    printer.connect(usb);
                    break;
            }
        }
    }
}
