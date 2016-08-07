package com.android.binder;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int THREAD_COUNT = 5;

    private class AidlConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mReporterAidl = IReporter.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mReporterAidl = null;
        }
    }

    private class BindConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mReporterBind = service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mReporterBind = null;
        }
    }

    private ExecutorService mExecutorService;
    private IReporter mReporterAidl;
    private IBinder   mReporterBind;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_start_aidl).setOnClickListener(this);
        findViewById(R.id.btn_start_bind).setOnClickListener(this);

        mExecutorService = Executors.newFixedThreadPool(THREAD_COUNT);

        Intent intent = new Intent(this, AidlService.class);
        bindService(intent, new AidlConnection(), BIND_AUTO_CREATE);

        intent = new Intent(this, BinderService.class);
        bindService(intent, new BindConnection(), BIND_AUTO_CREATE);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start_aidl) {
            for (int i = 0; i < THREAD_COUNT; ++i) {
                final int type = i;
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            int result = mReporterAidl.report("this is a test string.", type);
                            Log.i("IReporter", "result is " + result);
                        } catch (RemoteException e) {

                        }
                    }
                });
            }
        } else if (v.getId() == R.id.btn_start_bind) {
            for (int i = 0; i < THREAD_COUNT; ++i) {
                final int type = i;
                mExecutorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Parcel data = Parcel.obtain();
                            Parcel reply = Parcel.obtain();
                            data.writeInterfaceToken("reporter");
                            data.writeString("this is a test string.");
                            data.writeInt(type);

                            mReporterBind.transact(BinderService.REPORT_CODE, data, reply, 0);
                            reply.enforceInterface("reporter");
                            int result = reply.readInt();
                            data.recycle();
                            reply.recycle();
                            Log.i("IReporter", "result is " + result);
                        } catch (RemoteException e) {
                        }
                    }
                });
            }
        }
    }
}
