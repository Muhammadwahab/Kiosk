package com.linkitsoft.kioskproject;


import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class ExceptionHandler2 implements Thread.UncaughtExceptionHandler {
    private Context context;

    public ExceptionHandler2(Context context) {
        this.context = context;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

        FirebaseCrashlytics.getInstance().recordException(e);
        Intent intent = new Intent(context, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
