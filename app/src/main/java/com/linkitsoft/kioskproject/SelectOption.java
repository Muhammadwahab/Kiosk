package com.linkitsoft.kioskproject;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.linkitsoft.kioskproject.Model.TransactionModel;
import com.linkitsoft.kioskproject.Utilites.PortNVeriables;
import com.linkitsoft.kioskproject.deemons.serialportlib.SerialCom;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SelectOption extends AppCompatActivity {


    ImageView logo;
    EditText code;
    SerialCom serialCom;
    ImageView proceed;
    ImageView scented;
    ImageView unScented;

    SweetAlertDialog sweetAlertDialog;

    private PortNVeriables portsandVeriables;
    private SweetAlertDialog pDialog;
    SharedPreferences sharedpreferences;
    String kioskid;
    String type;
    String selected="";

    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    Boolean isuserpaying=false;
    Boolean threadintrupt=false;
    Boolean oncreate=false;
    private TextClock tClock;


    public class wait30 extends Thread {
        public wait30() {
        }

        public void run() {

            super.run();

            while (!threadintrupt){

                try {
                    Thread.sleep(45000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(e);

                }

                final CountDownTimer[] ct = new CountDownTimer[1];
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ct[0] =  new CountDownTimer(1000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                    if(!isuserpaying){
                                        if( millisUntilFinished<0)
                                        {
                                        threadintrupt= true;
                                        try {
                                            sweetAlertDialog.dismissWithAnimation();
                                        } catch (Exception ex) {
                                            FirebaseCrashlytics.getInstance().recordException(ex);

                                        }
                                        finish();
                                        ct[0].cancel();
                                    }}
                                }

                                public void onFinish() {

                                    try {
                                        sweetAlertDialog.dismissWithAnimation();
                                    } catch (Exception ex) {
                                        FirebaseCrashlytics.getInstance().recordException(ex);

                                    }
                                    threadintrupt= true;
                                    ct[0].cancel();
                                   finish();
                                }};

                            if (!threadintrupt && !isuserpaying ) {
                               // showsweetalerttimeout(ct);
                                ct[0].start();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            FirebaseCrashlytics.getInstance().recordException(e);

                        }}
                });

            }}
    }


    wait30 w30;

    @Override
    protected void onDestroy() {
        threadintrupt = true;
        w30.interrupt();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);

        getWindow().getDecorView().setSystemUiVisibility(flags);

        logo = findViewById(R.id.imageView2);
        proceed = findViewById(R.id.button8);
        scented = findViewById(R.id.scented);
        unScented = findViewById(R.id.unScented);
        code = findViewById(R.id.editTextTextPersonName);
       // logs = findViewById(R.id.logs);
        serialCom = SerialCom.getInstance();
        serialCom.setSlctOptn(this);

        portsandVeriables = new PortNVeriables();
        sharedpreferences = getSharedPreferences("MyPrefs", 0);

        kioskid = sharedpreferences.getString("kid","0");

        pDialog = new SweetAlertDialog(SelectOption.this, SweetAlertDialog.PROGRESS_TYPE);

        w30 = new wait30();
        w30.start();
        oncreate= true;



        scented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scented.setImageResource(R.drawable.scented_selected);
                unScented.setImageResource(R.drawable.unscened_neutral);
                proceed.setImageResource(R.drawable.dispence_button);

                selected="1";

            }
        });

        unScented.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unScented.setImageResource(R.drawable.unscented_selected);
                scented.setImageResource(R.drawable.scented_neutral);
                proceed.setImageResource(R.drawable.dispence_button);
                selected="0";

            }
        });
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //openprogrsspopup();

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selected.isEmpty()){
                  //  Toast.makeText(getApplicationContext(),"Please select dispense option ",Toast.LENGTH_LONG).show();

                    return;
                }


               if (!checkConnectionBeforeDispence()){
                   pDialog = new SweetAlertDialog(SelectOption.this, SweetAlertDialog.PROGRESS_TYPE);
                   pDialog.setTitle("Connection is not establish restarting kiosk");
                   pDialog.setCanceledOnTouchOutside(false);
                   pDialog.setNeutralButton(R.string.dialog_ok, new SweetAlertDialog.OnSweetClickListener() {
                       @Override
                       public void onClick(SweetAlertDialog sweetAlertDialog) {

                           closeAllConnection();
                           finish();
                           Intent intent = new Intent(sweetAlertDialog.getContext(), Login.class);
                           intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                           sweetAlertDialog.getContext().startActivity(intent);
                       }
                   });
                   pDialog.show();
                   return;
               }

                String result=selected;


                try {
                    proceed.setEnabled(false);
                    pDialog = new SweetAlertDialog(SelectOption.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.setTitle("Dispensing Product");
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.show();
                }catch (Exception ex){
                    FirebaseCrashlytics.getInstance().recordException(ex);

                }
                isuserpaying = true;

                dispense(result);

            }
        });
    }

    private void closeAllConnection() {
        try {
            serialCom.port.close();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);

        }
    }

    private Boolean checkConnectionBeforeDispence() {

        try {
            byte[] buf = new byte[2];
            int len =  serialCom.connection.controlTransfer(0x80 /*DEVICE*/, 0 /*GET_STATUS*/, 0, 0, buf, buf.length, 500);
            if(len < 0)
            {
                Log.e(SelectOption.class.getSimpleName(),"connection is not establish");
            }else{
                return true;
            }
        }catch (Exception exception){
            FirebaseCrashlytics.getInstance().recordException(exception);

        }
       return false;
    }

    private  void dispense(String prod){

        if (prod.equals("1")){
           // "Scented";
            try {
                serialCom.startMotor(0);
            } catch (Exception e) {
                e.printStackTrace();
                FirebaseCrashlytics.getInstance().recordException(e);

            }
        }
        else {
           //"Unscented";
            serialCom.startMotor(1);
        }
    }


    private void thankyouScreen(Double balance) {

        pDialog.dismissWithAnimation();
        isuserpaying = false;
        proceed.setEnabled(true);

        Intent intent = new Intent(SelectOption.this, Thankyou.class);
        intent.putExtra("balance", balance);
        intent.putExtra("prod", type);
        startActivity(intent);
        finish();
    }


    public void showcount(int c1, int c2, int m) {

        Log.e("SelectOption","Dispense counter, Counter 1 : " + c1 + " Counter 2 : " + c2);
        try{
            if (c1 >= 6 || c2 >=6){
            serialCom.stopMotor(m);
            pDialog.setTitle("Dispensing Product");
            pDialog.show();
            thankyouScreen(0.0);

        }
 }catch (Exception ex){
     ex.printStackTrace();
            FirebaseCrashlytics.getInstance().recordException(ex);

        }
    }


    public void timeout(){
      try{
          pDialog.dismissWithAnimation();
      }
      catch (Exception ex){
          FirebaseCrashlytics.getInstance().recordException(ex);

      }
        isuserpaying = false;
        threadintrupt = true;
        proceed.setEnabled(true);
        finish();


    }

    public void stopmotor(int m,String tex)
    {
        serialCom.stopMotor(m);
    }

    @Override
    protected void onResume() {
        super.onResume();

        threadintrupt=false;
        isuserpaying = false;
        if(!oncreate) {
       //     new wait30().start();
        }
        else{

            oncreate = false;

        }

        Intent intent = getIntent();
        if (intent != null) {
          try {
              if (intent.getAction().equals(ACTION_USB_DEVICE_ATTACHED)) {
                  Parcelable usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

                  // Create a new intent and put the usb device in as an extra
                  Intent broadcastIntent = new Intent(ACTION_USB_DEVICE_ATTACHED);
                  broadcastIntent.putExtra(UsbManager.EXTRA_DEVICE, usbDevice);

                  // Broadcast this event so we can receive it
                  sendBroadcast(broadcastIntent);
              }
          }
          catch (Exception ex){
              FirebaseCrashlytics.getInstance().recordException(ex);

          }
        }

    }
    @Override
    protected void onPause() {
        super.onPause();
        threadintrupt = true;
        isuserpaying = true;
    }
}