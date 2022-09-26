package com.linkitsoft.kioskproject;

import android.app.Application;

public class kioskApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        setExceptionHandler();
    }

    private void setExceptionHandler() {

            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler2(this));


    }
}
