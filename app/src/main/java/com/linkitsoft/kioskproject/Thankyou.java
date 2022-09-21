package com.linkitsoft.kioskproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextClock;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Thankyou extends AppCompatActivity {

    TextView text;
    TextView balacnetxt;
    String product;
    Double balance;
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
    SweetAlertDialog sweetAlertDialog;


    public class wait30 extends Thread {
        public wait30() {
        }

        public void run() {

            super.run();

            while (!threadintrupt){

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                final CountDownTimer[] ct = new CountDownTimer[1];
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            ct[0] =  new CountDownTimer(10000, 1000) {
                                public void onTick(long millisUntilFinished) {

                                    if(!isuserpaying){
                                        if( millisUntilFinished<0)
                                        {
                                            threadintrupt= true;
                                            try {
                                                sweetAlertDialog.dismissWithAnimation();
                                            } catch (Exception ex) {
                                            }
                                            finish();
                                            ct[0].cancel();
                                        }}
                                }

                                public void onFinish() {

                                    try {
                                        sweetAlertDialog.dismissWithAnimation();
                                    } catch (Exception ex) {
                                    }
                                    threadintrupt= true;
                                    ct[0].cancel();
                                    finish();
                                }};

                            if (!isuserpaying ) {
                                // showsweetalerttimeout(ct);
                                ct[0].start();
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }}
                });

//                runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        showsweetalerttimeout(ct);
//
//                    }
//                });

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

        getWindow().getDecorView().setSystemUiVisibility(flags);

        setContentView(R.layout.activity_thankyou);
        text = findViewById(R.id.textView6);
        balacnetxt = findViewById(R.id.textView14);

//        product = getIntent().getStringExtra("prod");
//        balance = getIntent().getDoubleExtra("balance",0.0);

        w30 = new wait30();
        w30.start();
        oncreate= true;


//        if (product.equals("1")){
//            product = "Scented";
//        }
//        else {
//            product = "Unscented";
//        }


//        Double d = balance;
//        int i = d.intValue();

      //  balacnetxt.setText("You have "+i+" "+product+" sheets left");






        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              finish();
            }
        });

    }

}