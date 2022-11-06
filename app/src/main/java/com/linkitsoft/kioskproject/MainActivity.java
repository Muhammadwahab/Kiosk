package com.linkitsoft.kioskproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.gson.Gson;
import com.linkitsoft.kioskproject.Model.SliderItem;
import com.linkitsoft.kioskproject.Recycler.SliderAdapterExample;
import com.linkitsoft.kioskproject.Recycler.SliderClickHandler;
import com.linkitsoft.kioskproject.deemons.serialportlib.SerialCom;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private SliderView sliderView;
    private List<SliderItem> sliderItems;
    ImageView nextbtn;
    SharedPreferences sharedpreferences;
    String kioskid;
    File myExternalFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        SerialCom.getInstance().setMainActivity(this);
        SerialCom.getInstance().openport();

        nextbtn = findViewById(R.id.button);

        sliderView = findViewById(R.id.imageSlider);

        sharedpreferences = getSharedPreferences("MyPrefs", 0);

        kioskid = sharedpreferences.getString("kid","0");


        SliderAdapterExample adapter = new SliderAdapterExample(this, () -> {
            dispensingScreenContent();
        });
        sliderItems = new ArrayList<SliderItem>();

       //  sliderItems.add()
        sliderItems.add(new SliderItem("img0"));
        sliderItems.add(new SliderItem("img1"));
        sliderItems.add(new SliderItem("img2"));

        adapter.renewItems(sliderItems);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(5); //set scroll delay in seconds :
        sliderView.startAutoCycle();


        nextbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dispensingScreenContent();
            }
        });

    }

    private void dispensingScreenContent() {
        if(kioskid.equals("0")){
            new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Not Registered")
                    .setContentText("Kindly register the kiosk")
                    .show();
        }
        else {
                    Intent optinpage = new Intent(MainActivity.this, SelectOption.class);
                    startActivity(optinpage);

          //  emailDialogContent();
        }
    }

    private void emailDialogContent() {
         String filepath = "MyFileStorage";

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


                alertDialog.dismiss();
                Intent optinpage = new Intent(MainActivity.this, SelectOption.class);
                    startActivity(optinpage);

            }

        });


        alertDialog.setView(view);
        alertDialog.show();
    }

}