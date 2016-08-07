package com.android.binder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class BinderService extends Service {

    public static final int REPORT_CODE = 0;

    public interface IReporter {
        int report(String values, int type);
    }

    public final class Reporter extends Binder implements IReporter {
        @Override
        protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case REPORT_CODE:
                    data.enforceInterface("reporter");
                    String values = data.readString();
                    Log.i("IReporter", "data is '" + values + "'");
                    int type = data.readInt();
                    int result = report(values, type);
                    reply.writeInterfaceToken("reporter");
                    reply.writeInt(result);
                    return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        @Override
        public int report(String values, int type) {
            try {
                Thread.sleep(30 * 1000);
            } catch (InterruptedException e) {
            }
            return type;
        }
    }

    private Reporter mReporter;

    public BinderService() {
        mReporter = new Reporter();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mReporter;
    }
}
