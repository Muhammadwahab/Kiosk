package com.linkitsoft.kioskproject.deemons.serialportlib;

import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {
    private static final String DEFAULT_SU_PATH = "/system/bin/su";
    private static final String TAG = "SerialPort";
    private static String sSuPath = "/system/bin/su";
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    private static native FileDescriptor open(String str, int i, int i2, int i3, int i4, int i5);

    public native void close();

    public static void setSuPath(String str) {
        if (str != null) {
            sSuPath = str;
        }
    }

    public static String getSuPath() {
        return sSuPath;
    }

    public SerialPort(String str, int i) throws SecurityException, IOException {
        this(new File(str), i);
    }

    public SerialPort(File file, int i) throws SecurityException, IOException {
        this(file, i, 0);
    }

    public SerialPort(File file, int i, int i2) throws SecurityException, IOException {
        this(file, i, 0, 8, 1, i2);
    }

    public SerialPort(File file, int i, int i2, int i3, int i4, int i5) throws SecurityException, IOException {
        if (!file.canRead() || !file.canWrite()) {
            Log.d(TAG, "Missing read/write permission, trying to chmod the file");
            try {
                Process exec = Runtime.getRuntime().exec(sSuPath);
                StringBuilder sb = new StringBuilder();
                sb.append("chmod 666 ");
                sb.append(file.getAbsolutePath());
                sb.append("\nexit\n");
                exec.getOutputStream().write(sb.toString().getBytes());
                if (exec.waitFor() != 0 || !file.canRead() || !file.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }
        this.mFd = open(file.getAbsolutePath(), i, i2, i3, i4, i5);
        if (this.mFd != null) {
            this.mFileInputStream = new FileInputStream(this.mFd);
            this.mFileOutputStream = new FileOutputStream(this.mFd);
            return;
        }
        Log.e(TAG, "native open returns null");
        throw new IOException();
    }

    public InputStream getInputStream() {
        return this.mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return this.mFileOutputStream;
    }

    static {
        System.loadLibrary("serial-port");
    }
}