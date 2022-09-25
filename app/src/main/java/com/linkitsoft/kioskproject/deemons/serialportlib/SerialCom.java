package com.linkitsoft.kioskproject.deemons.serialportlib;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.linkitsoft.kioskproject.MainActivity;
import com.linkitsoft.kioskproject.SelectOption;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.ContentValues.TAG;

public class SerialCom {

    Context context;
    private boolean activated;
    private boolean motorRunning = false;
    private ReadThread mReadThread;
    UsbSerialPort port;

    byte m = 0x00;
    int amount = 0;
    int c = 0;
    static int timeOut = 60000;

    static byte zero = 0x00;
    static byte setMotorSpeedCommand = 0x03;
    static byte startMotorCommand = 0x04;
    static byte stopMotorCommand = 0x05;

    static byte parameterUpdateCommand = 0x07;
    static byte storeMotorParametersCommand = 0x08;
    static byte retreatMotorParamCommand = 0x09;

    static byte opticalSensorReportCommand = 0x10;
    static byte motorReportCommand = 0x12;

    static byte terminalEnableCommand = 0x20;
    static byte terminalDisableCommand = 0x21;

    static byte motorParamNoStatus = 0x0;
    static byte motorParamStoredSuccess = 0x01;
    static byte motorParamStoredNotSuccess = 0x02;
    static byte motorParamReadSuccess = 0x03;
    static byte motorParamReadNotSuccess = 0x04;


    SelectOption selectoptn;
    MainActivity mainActivity;

    public void setSlctOptn(SelectOption selectoptn) {
        SerialCom.this.selectoptn = selectoptn;
        SerialCom.this.context = selectoptn.getApplicationContext();
        //    openport();
    }

    public void setMainActivity(MainActivity mainActivity) {
        SerialCom.this.mainActivity = mainActivity;
        SerialCom.this.context = mainActivity.getApplicationContext();
        //    openport();
    }

    private SerialCom() {

    }

    public void setContext(Context context) {
        this.context = context;
    }

    private int myVar = 0;
    private static SerialCom instance;

    static {
        instance = new SerialCom();
    }

    public static SerialCom getInstance() {
        return SerialCom.instance;
    }

    void activate() {
        if (!activated)
            sendSerialPort(new byte[]{retreatMotorParamCommand, m});
    }

    int count1a;
    int count1b;
    int count2a;
    int count2b;
    List<byte[]> responselist = new ArrayList<>();

    private class ReadThread extends Thread {
        private ReadThread() {
        }

        public void run() {
            super.run();
            while (!Thread.interrupted()) {
                int i = 1;
                int i2 = SerialCom.this.port != null ? 1 : 0;

                if ((i2 & i) != 0) {
                    try {
                        byte[] bArr = new byte[5];

                        Log.e("SerialComLoop","run checking");
                        if (SerialCom.this.port.read(bArr, timeOut) > 0) {
                            Log.e("SerialComLooptrue","run checking");

                            String response = ByteUtils.bytesToHexString(bArr);

                            if (bArr[0] == 0x10 && SerialCom.this.motorRunning) {


                                byte[] previousbArr;
                                try {
                                    previousbArr = responselist.get(responselist.size() - 1);
                                } catch (Exception ex) {
                                    previousbArr = null;
                                }

                                if (previousbArr == null)
                                    previousbArr = bArr;


                                //  10-01-01-01-01 represents ALL optical path is clear

                                //count
                                // 10 00 01 01 01 - 1B
                                // 10 01 00 01 01 - 1A
                                // 10 01 01 00 01 - 2B
                                // 10 01 01 01 00 - 2A

//                                showToast("Barla Previous Byte :" + ByteUtils.bytesToHexString(previousbArr));
//                                showToast("Barla current Byte :" + response);


                                if ((bArr[1] == 0x00)) {
                                    SerialCom.this.count1b += 1;
                                }
                                    if ((bArr[2] == 0x00)) {
                                        SerialCom.this.count1a += 1;
                                    }
                                if ((bArr[3] == 0x00)) {
                                    SerialCom.this.count2b += 1;
                                }
                                    if ((bArr[4] == 0x00)) {
                                        SerialCom.this.count2a += 1;
                                    }
                                Log.e("SerialComLoop"," SerialCom.this.count1b "+ SerialCom.this.count1b);
                                Log.e("SerialComLoop"," SerialCom.this.count1a "+ SerialCom.this.count1a);
                                Log.e("SerialComLoop"," SerialCom.this.count2b "+ SerialCom.this.count2b);
                                Log.e("SerialComLoop"," SerialCom.this.count2a "+ SerialCom.this.count2a);


                                showcount();
                                responselist.add(bArr);

                            }

                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
                }

            }
        }
    }

    public void showcount() {
        SerialCom.this.selectoptn.runOnUiThread(new Runnable() {
            public void run() {
                if (SerialCom.this.m == 0) {
                    SerialCom.this.selectoptn.showcount(SerialCom.this.count1a, SerialCom.this.count1b, SerialCom.this.m);
                } else {
                    SerialCom.this.selectoptn.showcount(SerialCom.this.count2a, SerialCom.this.count2b, SerialCom.this.m);
                }

            }
        });
    }

    public void showToast(String message) {
        SerialCom.this.selectoptn.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(selectoptn.getApplicationContext(),  message ,Toast.LENGTH_LONG).show();


            }
        });
    }

    public void Dispense(int amount, int m) throws Exception {

        //motor no
        if (m == 0 || m == 1) {
            this.m = ByteUtils.intToBytes(m)[0];
            this.amount = amount;
            try {

                if (nodrivers) {
                    Toast.makeText(context, "Could not find any available devices.", Toast.LENGTH_LONG).show();
                } else {
                    Thread thread = new Thread() {
                        @Override
                        public void run() {
                            try {
                                //init
                                activate();

                                //start motor
                                startMotor(m);

                                //wait
                                sleep(3000);

                                //stop motor
                                stopMotor(m);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("SerialCom", "invalid param while dispense");
            throw new Exception("invalid param while dispense");
        }


    }

    Handler handler = new Handler();
    Runnable myrn =
            new Runnable() {
                public void run() {
                    if (SerialCom.this.m == 0)// && (!(SerialCom.this.count1a > 0 || SerialCom.this.count1b > 0)))
                    {
                        stopMotor(SerialCom.this.m);
                        SerialCom.this.selectoptn.runOnUiThread(new Runnable() {
                            public void run() {

                                showToast("myRun :Dispense time out motor stop");
                                SerialCom.this.selectoptn.stopmotor(m, "Dispense time out.");
                                SerialCom.this.selectoptn.timeout();

                            }
                        });

                    } else if (SerialCom.this.m == 1) {// && (!(SerialCom.this.count2a > 0 || SerialCom.this.count2b > 0))) {
                        stopMotor(SerialCom.this.m);
                        SerialCom.this.selectoptn.runOnUiThread(new Runnable() {
                            public void run() {
                                showToast("myRun :Dispense time out motor stop");

                                SerialCom.this.selectoptn.stopmotor(m, "Dispense time out.");
                                SerialCom.this.selectoptn.timeout();

                            }
                        });

                    }
                }
            };

    public void startMotor(int m) {
        SerialCom.this.count1a = 0;
        SerialCom.this.count2a = 0;
        SerialCom.this.count1b = 0;
        SerialCom.this.count2b = 0;
        SerialCom.this.m = ByteUtils.intToBytes(m)[0];
        if (!SerialCom.this.motorRunning) {

            sendSerialPort(new byte[]{startMotorCommand, SerialCom.this.m});
            SerialCom.this.motorRunning = true;
            startreading();


            handler.postDelayed(myrn, 15000);

        }
    }

    public void stopMotor(int m) {
        SerialCom.this.count1a = 0;
        SerialCom.this.count2a = 0;
        SerialCom.this.count1b = 0;
        SerialCom.this.count2b = 0;
        SerialCom.this.m = ByteUtils.intToBytes(m)[0];
        if (SerialCom.this.motorRunning) {
            sendSerialPort(new byte[]{stopMotorCommand, this.m});
            SerialCom.this.motorRunning = false;
            stopreading();
        }
        handler.removeCallbacks(myrn);

    }


    private void sendSerialPort(byte[] str) {
        try {
            if (SerialCom.this.port != null) {
                SerialCom.this.port.write(str, timeOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Boolean nodrivers;
    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";

    public void openport() {
        try {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        UsbManager manager = (UsbManager) SerialCom.this.context.getSystemService(Context.USB_SERVICE);

                        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);

                        if (availableDrivers.isEmpty()) {
                            SerialCom.this.nodrivers = true;
                            showtoast();
                            hidealert();
                            return;
                        }

                        // Open a connection to the first available driver.
                        UsbSerialDriver driver = availableDrivers.get(0);
                        PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(context, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                        manager.requestPermission(driver.getDevice(), usbPermissionIntent);
                        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
                        if (connection == null) {
                            showtoast();
                            hidealert();
                            return;
                        }

                        SerialCom.this.port = driver.getPorts().get(0); // Most devices have just one port (port 0)
                        SerialCom.this.port.open(connection);
                        SerialCom.this.port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);

                        //  startreading();


                        hidealert();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            thread.start();


        } catch (Exception e) {
            Toast.makeText(context, "Machine connection failed " + e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void startreading() {
        SerialCom.this.mReadThread = new ReadThread();
        SerialCom.this.mReadThread.start();
    }

    private void hidealert() {
//        SerialCom.this.selectoptn.runOnUiThread(new Runnable() {
//            public void run() {
//                SerialCom.this.selectoptn.hideLoading();
//            }
//        });

    }

    SweetAlertDialog alertDialog;

    private void showtoast() {
        Activity act;
        if (selectoptn != null)
            act = SerialCom.this.selectoptn;
        else
            act = SerialCom.this.mainActivity;


        act.runOnUiThread(new Runnable() {
            public void run() {

                try {
                    alertDialog = new SweetAlertDialog(act, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Connection Failed")
                            .setContentText("Could not connect to the machine, which means the kiosk will be unable to dispense.")
                            .setConfirmButton("Retry", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    sweetAlertDialog.dismissWithAnimation();
                                    openport();
                                }
                            });
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void stopreading() {
        this.mReadThread.interrupt();
        this.mReadThread = null;
    }

    private void closeSerialPort() {
        try {
            port.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
