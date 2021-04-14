package com.sistechnology.fiscalservicesdk;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.os.RemoteException;

import java.net.BindException;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class FiscalService {
    private static final String SERVICE_ACTION = "com.sistechnology.fiscalservice.FISCAL";

    private IFiscalAidlInterface fiscalService = null;

    private boolean isBound = false;
    private static FiscalService instance;

    private OnBindListener mListener = null;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

            fiscalService = IFiscalAidlInterface.Stub.asInterface(iBinder);

            isBound = true;

            if (mListener != null)
                mListener.onBindComplete();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            fiscalService = null;
            isBound = false;
        }
    };

    private FiscalService(){
    }

    public static FiscalService getInstance() {
        if (instance == null)
            instance = new FiscalService();

        return instance;
    }

    public void bind(Context context, OnBindListener listener) throws Exception {
        if (!isServiceExist(context))
            throw new Exception("Functionality not supported");

        if (isBound)
            return;

        mListener = listener;

        Intent intent = new Intent(SERVICE_ACTION);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setPackage("com.sistechnology.pos.auroraposmobile");

        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbind(Context context) {
        if (isBound) {
            context.unbindService(serviceConnection);
            fiscalService = null;
            isBound = false;
        }

        mListener = null;
    }

    public String exchangeCommand(String json, long timeOut) throws Exception {
        if (!isBound) {
            throw new BindException("call .bind(context) fist");
        }

        long startTime = System.currentTimeMillis();
        while ((System.currentTimeMillis() - startTime < timeOut)) {
            try {
                if (fiscalService != null) {
                    return fiscalService.executeCommand(json);
                }
            }
            catch (IllegalStateException ignored) {
            }
            catch (RemoteException e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        throw new TimeoutException("Function did not return result in the required time period");
    }

    private boolean isServiceExist(Context context) {
        Intent intent = new Intent(SERVICE_ACTION, null);
        intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

        List<ResolveInfo> services = context.getPackageManager().queryIntentServices(intent, 0);
        return !services.isEmpty();
    }

    public boolean isSupported() {
        return fiscalService != null;
    }

}
