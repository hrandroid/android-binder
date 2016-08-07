package com.android.binder;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.util.Log;

public class AidlService extends Service {

    public static final class Reporter extends IReporter.Stub {

        @Override
        public int report(String values, int type) throws RemoteException {
            return type;
        }
    }

    private Reporter mReporter;

    public AidlService() {
        mReporter = new Reporter();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mReporter;
    }
}
