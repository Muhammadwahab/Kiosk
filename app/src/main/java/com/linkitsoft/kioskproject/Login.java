package com.linkitsoft.kioskproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkitsoft.kioskproject.Model.ProductModel;
import com.linkitsoft.kioskproject.Utilites.PortNVeriables;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity {

    Button nextbtn;
//    EditText username;
//    EditText password;
    String kioskid;

    private PortNVeriables portsandVeriables;
    private SweetAlertDialog pDialog;
    SharedPreferences sharedpreferences;

    final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;


    private String filename = "SampleFile.txt";
    private String filepath = "MyFileStorage";
    File myExternalFile;
    String myData = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().getDecorView().setSystemUiVisibility(flags);

        nextbtn = findViewById(R.id.button3);
///        username = findViewById(R.id.editTextTextPersonName3);
      //  password = findViewById(R.id.editTextTextPersonName2);

        portsandVeriables = new PortNVeriables();

        sharedpreferences = getSharedPreferences("MyPrefs", 0);

        kioskid = sharedpreferences.getString("kid","0");

        assert kioskid != null;
        if(!kioskid.equals("0")){
            Intent main = new Intent(Login.this, MainActivity.class);
            startActivity(main);
            finish();
//            Intent optinpage = new Intent(Login.this, SelectOption.class);
//            startActivity(optinpage);
//            finish();

//            Intent intent = new Intent(Login.this, Thankyou.class);
//            intent.putExtra("balance", 0.0);
//            intent.putExtra("prod", "1");
//            startActivity(intent);
//            finish();
        }


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString("kid", String.valueOf(123));
                editor.apply();

                Intent optinpag = new Intent(Login.this, MainActivity.class);
                startActivity(optinpag);
                finish();
//
//                if(!username.getText().toString().isEmpty() && !password.getText().toString().isEmpty())
//                {
//                    if(!chkinternet()){
//
//                        new SweetAlertDialog(Login.this,SweetAlertDialog.WARNING_TYPE)
//                                .setTitleText("No internet")
//                                .setContentText("Please check your internet connection")
//                                .show();
//                    }
//                    else {
//
//                        nextbtn.setEnabled(false);
//                        pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
//                        pDialog.setTitle("Processing");
//                        pDialog.show();
//                        getkioskdetails(username.getText().toString(),password.getText().toString());
//                    }
//                }
//
//                else
//                {
//                    new SweetAlertDialog(Login.this,SweetAlertDialog.ERROR_TYPE).setTitleText("Must fill both field").show();
//                }


            }
        });


        //  emailDialogContent();


    }

    private void emailDialogContent() {
        final View view = getLayoutInflater().inflate(R.layout.vallidation_dailog, null);
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Email Validation");
        alertDialog.setCancelable(false);


        final TextInputEditText email = (TextInputEditText) view.findViewById(R.id.textInputedittext);
        final TextInputLayout textInputLayout = (TextInputLayout) view.findViewById(R.id.textInputedLayout);
        final AppCompatButton submitButton = (AppCompatButton) view.findViewById(R.id.submitButton);
        email.requestFocus();

        submitButton.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty()){
                textInputLayout.setError("Please enter email address prefix only");
            }
            else
            {
                textInputLayout.setErrorEnabled(false);

                String filename = "kioskEmail.txt";
                String fileContents = "\n"+email.getText().toString()+textInputLayout.getSuffixText().toString();
                myExternalFile = new File(getExternalFilesDir(filepath), filename);

                try {
                    FileOutputStream fileOutputStream=new FileOutputStream(myExternalFile,true);
                    fileOutputStream.write(fileContents.getBytes());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),"exception "+e.getMessage(),Toast.LENGTH_LONG).show();

                }



            }

        });


        alertDialog.setView(view);
        alertDialog.show();
    }

    private void getkioskdetails(String uname, String pass) {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final String uri;
        uri = portsandVeriables.com+"Kiosk?username="+uname+"&password="+pass;

        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.GET, uri, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                pDialog.dismissWithAnimation();
                int result = 1;
                int kid = 0;
                try {
                    result = response.getInt("status");
                } catch (JSONException e) {
                    e.printStackTrace();
                    FirebaseCrashlytics.getInstance().recordException(e);

                }
                if(result == 1){

                    showdialog("Registration Failed", "Invalid username or password", 1);
                }
                else {
                    try {
                        kid = response.getInt("kid");
                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        editor.putString("kid", String.valueOf(123));
                        editor.apply();

                        Intent optinpag = new Intent(Login.this, MainActivity.class);
                        startActivity(optinpag);
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        FirebaseCrashlytics.getInstance().recordException(e);

                    }
                }
                nextbtn.setEnabled(true);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.dismissWithAnimation();

                showdialog("Failed","Try again",1);
                nextbtn.setEnabled(true);
            }
        });
        myReq.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(myReq);
    }
    public  boolean chkinternet()
    {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void showdialog(String title, String content, int type) {

        final SweetAlertDialog sd = new SweetAlertDialog(Login.this, type)
                .setTitleText(title)
                .setContentText(content);
        sd.show();
    }

}