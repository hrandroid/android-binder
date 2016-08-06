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
            int processId = Process.myPid();
            int threadId = Process.myTid();

            Log.i("IReporter", "ReportService: report begin, process is " + processId + ", thread is " + threadId);
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
            }
            Log.i("IReporter", "ReportService: report finish, process is " + processId + ", thread is " + threadId);

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
