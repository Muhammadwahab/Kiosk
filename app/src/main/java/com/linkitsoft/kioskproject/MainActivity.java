package com.linkitsoft.kioskproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.linkitsoft.kioskproject.Model.SliderItem;
import com.linkitsoft.kioskproject.Recycler.SliderAdapterExample;
import com.linkitsoft.kioskproject.deemons.serialportlib.SerialCom;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {
    private SliderView sliderView;
    private List<SliderItem> sliderItems;
    Button nextbtn;
    SharedPreferences sharedpreferences;
    String kioskid;

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


        SliderAdapterExample adapter = new SliderAdapterExample(this);
        sliderItems = new ArrayList<SliderItem>();

       //  sliderItems.add()
        sliderItems.add(new SliderItem("img0"));
        sliderItems.add(new SliderItem("img1"));
        sliderItems.add(new SliderItem("img2"));
       // sliderItems.add(new SliderItem("img3"));
        sliderItems.add(new SliderItem("img4"));
        sliderItems.add(new SliderItem("img5"));
        sliderItems.add(new SliderItem("img6"));

       /* sliderItems.add(new SliderItem("img5"));
        sliderItems.add(new SliderItem("img6"));*/

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

                if(kioskid.equals("0")){
                    new SweetAlertDialog(MainActivity.this,SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Not Registered")
                            .setContentText("Kindly register the kiosk")
                            .show();
                }
                else {
                    Intent optinpage = new Intent(MainActivity.this, SelectOption.class);
                    startActivity(optinpage);
                }
            }
        });

    }


}