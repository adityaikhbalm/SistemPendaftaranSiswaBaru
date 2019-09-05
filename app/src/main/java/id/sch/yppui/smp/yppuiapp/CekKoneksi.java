package id.sch.yppui.smp.yppuiapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by ipin on 4/28/2018.
 */

public class CekKoneksi extends BroadcastReceiver {
    public CekKoneksi() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        context.sendBroadcast(new Intent("Koneksi"));
    }
}
