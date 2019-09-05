package id.sch.yppui.smp.yppuiapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ipin on 6/26/2018.
 */

public class IsiFormulirFragment extends Fragment {

    private String EVENT_DATE_TIME;
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private TextView NoPendaftaran, Hari, Jam, Menit, Detik;
    private SharedPreferences pref;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.isi_formulir_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        Hari = (TextView) v.findViewById(R.id.txtHari);
        Jam = (TextView) v.findViewById(R.id.txtJam);
        Menit = (TextView) v.findViewById(R.id.txtMenit);
        Detik = (TextView) v.findViewById(R.id.txtDetik);
        NoPendaftaran = (TextView) v.findViewById(R.id.txtNoPendaftaran);

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        EVENT_DATE_TIME = pref.getString("tanggal_akhir", "").toString()+" 24:00:00";
        NoPendaftaran.setText(pref.getString(Constants.NO_PENDAFTARAN, ""));
        countDownStart();
        return v;
    }

    private void countDownStart() {
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    handler.postDelayed(this, 1000);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date event_date = dateFormat.parse(EVENT_DATE_TIME);
                    Date current_date = new Date();
                    if (!current_date.after(event_date)) {
                        long diff = event_date.getTime() - current_date.getTime();
                        long Days = diff / (24 * 60 * 60 * 1000);
                        long Hours = diff / (60 * 60 * 1000) % 24;
                        long Minutes = diff / (60 * 1000) % 60;
                        long Seconds = diff / 1000 % 60;
                        //
                        Hari.setText(String.format("%d", Days));
                        Jam.setText(String.format("%d", Hours));
                        Menit.setText(String.format("%d", Minutes));
                        Detik.setText(String.format("%d", Seconds));
                    } else {
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }
}
