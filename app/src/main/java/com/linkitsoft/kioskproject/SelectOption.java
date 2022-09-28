package com.linkitsoft.kioskproject;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.linkitsoft.kioskproject.Model.TransactionModel;
import com.linkitsoft.kioskproject.Utilites.PortNVeriables;
import com.linkitsoft.kioskproject.deemons.serialportlib.SerialCom;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SelectOption extends AppCompatActivity {

    Button nextbtn;
    ConstraintLayout clay1;
    ConstraintLayout clay2;
    ImageView ico1;
    ImageView ico2;
    ImageView logo;
    TextView prc1;
    TextView prc2;
    TextView lbl1;
    TextView lbl2;
    TextView logs;
    EditText code;
    TabLayout tablayout;
    /* private RecyclerView recyclerView;
     private List<ProductModel> productModelList;
     ProductRecycler productRecycler;*/
    SerialCom serialCom;
    Button startMotor2;
    Button stopMotor1;
    Button stopMotor2;
    Button proceed;

    SweetAlertDialog progressbar;
    SweetAlertDialog sweetAlertDialog;

    private PortNVeriables portsandVeriables;
    private SweetAlertDialog pDialog;
    SharedPreferences sharedpreferences;
    String kioskid;
    Double balance;
    String type;
    String csid;
    String precode;

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

                            if (!threadintrupt && !isuserpaying ) {
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

    void showsweetalerttimeout(final CountDownTimer[] ct)
    {
        sweetAlertDialog = new SweetAlertDialog(SelectOption.this,SweetAlertDialog.WARNING_TYPE);

        sweetAlertDialog.setTitleText("Press Anywhere on screen to Continue");
        sweetAlertDialog.setContentText("This session will end in 60 Seconds");
        sweetAlertDialog.setConfirmButton("Continue", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();
            }
        });

        sweetAlertDialog.setCancelButton("Close", new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {

                threadintrupt= true;
                ct[0].cancel();
                sweetAlertDialog.dismissWithAnimation();
                finish();

            }
        });

        sweetAlertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                sweetAlertDialog.dismissWithAnimation();
                ct[0].cancel();
            }
        });
        try {
            sweetAlertDialog.show();
        }
        catch (Exception ex){}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option);

        getWindow().getDecorView().setSystemUiVisibility(flags);

        logo = findViewById(R.id.imageView2);
        proceed = findViewById(R.id.button8);
        tablayout = findViewById(R.id.tabLayoutSelection);
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


        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openprogrsspopup();

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


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

                String result=tablayout.getSelectedTabPosition()==0?"1":"0";


                try {
                    proceed.setEnabled(false);
                    pDialog = new SweetAlertDialog(SelectOption.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.setTitle("Dispensing Product");
                    pDialog.setCanceledOnTouchOutside(false);
                    pDialog.show();
                }catch (Exception ex){

                }
                isuserpaying = true;

                dispense(result);
//
//                if(!code.getText().toString().isEmpty())
//                {
//                    if(!chkinternet()){
//
//                        new SweetAlertDialog(SelectOption.this,SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("No internet")
//                                .setContentText("Please check your internet connection")
//                                .show();
//                    }
//                    else {
//
//                      try {
//                          proceed.setEnabled(false);
//                          pDialog = new SweetAlertDialog(SelectOption.this, SweetAlertDialog.PROGRESS_TYPE);
//                          pDialog.setTitle("Processing");
//                          pDialog.setCanceledOnTouchOutside(false);
//                          pDialog.show();
//                      }catch (Exception ex){
//
//                      }
//                        isuserpaying = true;
//
//                        checkprecode(code.getText().toString());
//
//
//                    }
//                }
//
//                else
//                {
//                    new SweetAlertDialog(SelectOption.this,SweetAlertDialog.ERROR_TYPE).setTitleText("Must fill both field").show();
//                }
            }
        });

       /* setuptThings();

        addlisteners();*/





    }

    private void closeAllConnection() {
        try {
            serialCom.port.close();
        } catch (Exception e) {
        }
    }

    private Boolean checkConnectionBeforeDispence() {

        try {
            byte[] buf = new byte[2];
            int len =  serialCom.connection.controlTransfer(0x80 /*DEVICE*/, 0 /*GET_STATUS*/, 0, 0, buf, buf.length, 200);
            if(len < 0)
            {
                Log.e(SelectOption.class.getSimpleName(),"connection is not establish");
            }else{
                return true;
            }
        }catch (Exception exception){

        }
       return false;
    }

    private void checkprecode(String codee) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final String uri;
        uri = portsandVeriables.com+"Customer?prepaidCode="+codee;
        precode = codee;
        code.setText("");


        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

              //  pDialog.dismissWithAnimation();
                int result = 1;


                try {
                    result = response.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(result == 1){
                    showdialog("Checkout Failed", "Invalid prepaid code", 1);
                    proceed.setEnabled(true);
                    pDialog.dismissWithAnimation();
                    isuserpaying = false;
                }
                else if(result == 0)
                {

                    try {
                        balance = response.getDouble("amountPresent");
                        type = response.getString("type");
                        csid = response.getString("csid");

                        if(balance >= 10){

                            dispense(type);

                        }
                        else{
                            showdialog("Insufficient Balance", "Please Topup", 1);
                            proceed.setEnabled(true);
                            isuserpaying = false;
                            pDialog.dismissWithAnimation();


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();

                    }


                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();
                showdialog("Failed","Try again",1);
                proceed.setEnabled(true);
                isuserpaying = false;

            }
        });
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(myReq);
    }

    private  void dispense(String prod){

        if (prod.equals("1")){
           // "Scented";
            try {
                serialCom.startMotor(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
           //"Unscented";
            serialCom.startMotor(1);
        }

    // After success dispense call this

/*      pDialog = new SweetAlertDialog(SelectOption.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.setTitle("Dispensing Product");
        pDialog.show();
        updatetransection();    */

    }
    private void updatetransection()
    {

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            JSONObject jsonParam = null;
            String url = portsandVeriables.com + "TransactionHistory";

            TransactionModel transactionModel = new TransactionModel();
            transactionModel.setCustomerId(Integer.parseInt(csid));
            transactionModel.setKioskId(Integer.parseInt(kioskid));
            transactionModel.setQuantityDispensed(10);
            transactionModel.setPrepaidcode(precode);
            transactionModel.setPid(Integer.parseInt(type));


            try {
                jsonParam = new JSONObject(new Gson().toJson(transactionModel));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final JSONObject requestBody = jsonParam;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, requestBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    pDialog.dismissWithAnimation();
                    isuserpaying = false;
                    proceed.setEnabled(true);


                    try {
                        int status = response.getInt("status");

                        System.out.println("Check Return value from api " + status);
                        if (status == 1) {

                            Double balance = response.getDouble("amountPresent");

                            thankyouScreen(balance);
                        }
                        else {
                            proceed.setEnabled(true);

                        }

                    } catch (JSONException e) {
                        System.out.println("FIX Error" + e);
                        pDialog.dismissWithAnimation();
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    // error report
                    System.out.println("FIX Error" + error);
                    proceed.setEnabled(true);

                    pDialog.dismissWithAnimation();
                    showdialog("Error", "Kindly fix internet connection then try again or Contact customer support", 1);

                }
            });

            requestQueue.add(jsonObjectRequest);
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

    private void openprogrsspopup() {

        LayoutInflater inflater = (LayoutInflater)
                this.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.activity_password_activity, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        final EditText pass = popupView.findViewById(R.id.editTextTextPersonName4);
        final Button save = popupView.findViewById(R.id.button2);

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);


        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                popupWindow.dismiss();

                return true;
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pass.getText().toString().equals("1080"))
                {
                    Intent optinpage = new Intent(SelectOption.this, Configration.class);
                    startActivity(optinpage);

                }
                else
                popupWindow.dismiss();
            }
        });
    }

    public void showcount(int c1, int c2, int m) {

        Log.e("SelectOption","Dispense counter, Counter 1 : " + c1 + " Counter 2 : " + c2);

       // Toast.makeText(this, "Dispense counter, Counter 1 : " + c1 + " Counter 2 : " + c2, Toast.LENGTH_LONG).show();

        try{
            if (c1 >= 10 || c2 >=10){
            serialCom.stopMotor(m);
            pDialog.setTitle("Dispensing Product");
            pDialog.show();
            thankyouScreen(0.0);
          //  updatetransection();


               // logs.setText("Dispense Completed, Counter 1 : " + c1 + " Counter 2 : " + c2);
                //Toast.makeText(this, "Dispense Completed, Counter 1 : " + c1 + " Counter 2 : " + c2, Toast.LENGTH_LONG).show();
        }

//        if (c2 < 10) {
//        } else {
//            serialCom.stopMotor(m);
//            pDialog.setTitle("Dispensing Product");
//            pDialog.show();
//            thankyouScreen(0.0);
//            //updatetransection();
//           // code.setText("Dispense Completed, Counter 1 : " + c1 + " Counter 2 : " + c2);
//        }

       /* if(m==0)
        {
            nextbtn.setEnabled(true);
            stopMotor1.setEnabled(false);
        }else
        {
            startMotor2.setEnabled(true);
            stopMotor2.setEnabled(false);
        }*/
        System.out.println("Barla : Class connection:  Counter 1 : " + c1 + " Counter 2 : " + c2);
      //  code.setText(/*(m==0?"Motor 1":"Motor 2")+*/" Counter 1 : " + c1 + " Counter 2 : " + c2);
 }catch (Exception ex){
     ex.printStackTrace();
 }
    }


    private void setuptThings()
    {
        nextbtn = findViewById(R.id.button6);
        stopMotor1 = findViewById(R.id.button4);
        startMotor2 = findViewById(R.id.button7);
        stopMotor2 = findViewById(R.id.button5);


        clay2 = findViewById(R.id.clayout);
        clay1 = findViewById(R.id.clayout2);

        ico1 = findViewById(R.id.imageView4);
        ico2 = findViewById(R.id.imageView3);

        lbl1 = findViewById(R.id.textView6);
        lbl2 = findViewById(R.id.textView3);

        prc1 = findViewById(R.id.textView8);
        prc2 = findViewById(R.id.textView4);

        code = findViewById(R.id.editTextTextPersonName);


        progressbar = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        progressbar.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        progressbar.setTitleText("Setting things up please wait...");

        progressbar.setCancelable(false);

        stopMotor1.setEnabled(false);
        stopMotor2.setEnabled(false);

        // showLoading();

    }


    private void addlisteners()
    {

        View.OnClickListener oc1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clay1.setBackgroundResource(R.drawable.optionbg);
                clay2.setBackgroundResource(0);
                nextbtn.setText("Procced to pay $23.00");
            }
        };

        View.OnClickListener oc2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clay2.setBackgroundResource(R.drawable.optionbg);
                clay1.setBackgroundResource(0);
                nextbtn.setText("Procced to pay $12.00");

            }
        };

        clay1.setOnClickListener(oc1);
        ico1.setOnClickListener(oc1);
        prc1.setOnClickListener(oc1);
        lbl1.setOnClickListener(oc1);

        clay2.setOnClickListener(oc2);
        ico2.setOnClickListener(oc2);
        prc2.setOnClickListener(oc2);
        lbl2.setOnClickListener(oc2);


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dispensing product here
                try {
                    code.setText("Please wait sheets are dispensing .....");
                    serialCom.startMotor(0);
                    stopMotor1.setEnabled(true);
                    v.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        startMotor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    code.setText("Please wait sheets are dispensing .....");
                    serialCom.startMotor(1);
                    stopMotor2.setEnabled(true);
                    v.setEnabled(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        stopMotor1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopmotor(0,"Dispense Canceled");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        stopMotor2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    stopmotor(1,"Dispense Canceled");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }


    public void timeout(){
      try{
          pDialog.dismissWithAnimation();
      }
      catch (Exception ex){}
        isuserpaying = false;
        threadintrupt = true;
        proceed.setEnabled(true);
        finish();


    }

    public void stopmotor(int m,String tex)
    {
        serialCom.stopMotor(m);



       /* code.setText(tex);
        if(m==0) {
            stopMotor1.setEnabled(false);
            nextbtn.setEnabled(true);
        } if(m==1) {
            stopMotor2.setEnabled(false);
            startMotor2.setEnabled(true);
        }*/
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
          catch (Exception ex){}
        }

    }

    public  boolean chkinternet()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showdialog(String title, String content, int type) {

        final SweetAlertDialog sd = new SweetAlertDialog(SelectOption.this, type)
                .setTitleText(title)
                .setContentText(content);
        sd.show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        threadintrupt = true;
        isuserpaying = true;
    }



}