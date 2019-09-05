package id.sch.yppui.smp.yppuiapp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ipin on 7/20/2018.
 */

public class DiskonFragment extends Fragment {

    private String EVENT_DATE_TIME;
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private TextView Hari, Jam, Menit, Detik;
    private Button Kirim, Lewati;
    private EditText Diskon;
    private SharedPreferences pref;
    private ProgressDialog mKodeProses;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.diskon_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        Hari = (TextView) v.findViewById(R.id.txtHari);
        Jam = (TextView) v.findViewById(R.id.txtJam);
        Menit = (TextView) v.findViewById(R.id.txtMenit);
        Detik = (TextView) v.findViewById(R.id.txtDetik);
        Diskon = (EditText) v.findViewById(R.id.editDiskon);
        Kirim = (Button) v.findViewById(R.id.btnKirim);
        Lewati = (Button) v.findViewById(R.id.btnLewati);

        Kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Diskon.getText().toString().trim().length() > 0){
                    mKodeProses = new ProgressDialog(getActivity());
                    mKodeProses.setTitle("Pengecekan Kode");
                    mKodeProses.setMessage("Mohon Tunggu ");
                    mKodeProses.setCanceledOnTouchOutside(false);
                    mKodeProses.show();

                    cekDiskon(pref.getString(Constants.ID, ""), pref.getString(Constants.NO_PENDAFTARAN, ""), Diskon.getText().toString());
                }
                else {
                    Toast.makeText(getActivity(), "Kode tidak boleh kosong.", Toast.LENGTH_LONG).show();
                }
            }
        });

        Lewati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lewati(pref.getString(Constants.ID, ""));
            }
        });

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        EVENT_DATE_TIME = pref.getString("tanggal_pembayaran", "").toString()+" 24:00:00";
        countDownStart();
        return v;
    }

    private void cekDiskon(String id, String no_pendaftaran, String kd_diskon) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Call<ServerResponse> response = requestInterface.diskon(id, no_pendaftaran, kd_diskon);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    mKodeProses.dismiss();
                    ((MainActivity)getActivity()).cekStatus(pref.getString(Constants.ID,""), pref.getString(Constants.EMAIL,""), "fragment");
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                else {
                    mKodeProses.dismiss();
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                mKodeProses.dismiss();
                Snackbar.make(getView(), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void lewati(String id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Call<ServerResponse> response = requestInterface.change_diskon(id);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    ((MainActivity)getActivity()).cekStatus(pref.getString(Constants.ID,""), pref.getString(Constants.EMAIL,""), "fragment");
                }
                else {
                    Snackbar.make(getView(), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Snackbar.make(getView(), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
            }
        });
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
