package com.linkitsoft.kioskproject.Recycler;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.linkitsoft.kioskproject.Model.SliderItem;
import com.linkitsoft.kioskproject.R;
import com.linkitsoft.kioskproject.SelectOption;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {


    private SliderClickHandler sliderClickHandler;

    private Context context;
    private List<SliderItem> mSliderItems = new ArrayList<>();
    SharedPreferences sharedpreferences;
    String kioskid;

    public SliderAdapterExample(Context context, SliderClickHandler sliderClickHandler) {
        this.context = context;
        this.sliderClickHandler=sliderClickHandler;
    }

    public void renewItems(List<SliderItem> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.sliderlist, null);
        context = parent.getContext();
        sharedpreferences = context.getSharedPreferences("MyPrefs", 0);

        kioskid = sharedpreferences.getString("kid","0");

        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        String itemurl = mSliderItems.get(position).getImg();
        viewHolder.setdata(itemurl);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sliderClickHandler.onClick();
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;


        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.imageView5);

            this.itemView = itemView;
        }
        public void setdata(String url){

          //  File f = new File(url);


            if(url == "img0")
            Picasso.get().load(R.drawable.slider_1).into(imageViewBackground);

            if(url == "img1")
            Picasso.get().load(R.drawable.slider_2).into(imageViewBackground);

            if(url == "img2")
            Picasso.get().load(R.drawable.slider_3).into(imageViewBackground);

           /* if(url == "img4")
            Picasso.get().load(R.drawable.img5).into(imageViewBackground);
            if(url == "img5")
            Picasso.get().load(R.drawable.img5).into(imageViewBackground);
            if(url == "img6")
            Picasso.get().load(R.drawable.img6).into(imageViewBackground);
*/
            //Picasso.get().load(f).into(imageViewBackground);

        }
    }

}

