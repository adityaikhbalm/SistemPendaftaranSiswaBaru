package id.sch.yppui.smp.yppuiapp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import id.sch.yppui.smp.yppuiapp.models.ServerRequest;
import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
import id.sch.yppui.smp.yppuiapp.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ipin on 4/12/2018.
 */

public class HomeFragment extends Fragment {

    private String EVENT_DATE_TIME;
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private TextView Hari, Jam, Menit, Detik;
    private Button btnPendaftaran;
    private SharedPreferences pref;
    private ProgressDialog mLoginProgress;
    private FrameLayout fm1;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.home_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        Hari = (TextView) v.findViewById(R.id.txtHari);
        Jam = (TextView) v.findViewById(R.id.txtJam);
        Menit = (TextView) v.findViewById(R.id.txtMenit);
        Detik = (TextView) v.findViewById(R.id.txtDetik);

        btnPendaftaran = (Button) v.findViewById(R.id.btnPendaftaran);
        fm1 = (FrameLayout) v.findViewById(R.id.fm1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fm1.setBackgroundResource(android.R.color.transparent);
            btnPendaftaran.setBackgroundResource(R.drawable.signin_ripple);
        }

        btnPendaftaran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoginProgress = new ProgressDialog(getActivity());
                mLoginProgress.setTitle("Kode Verifikasi");
                mLoginProgress.setMessage("Mohon Tunggu ");
                mLoginProgress.setCanceledOnTouchOutside(false);
                mLoginProgress.show();

                kode_pendaftaran();
            }
        });

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        EVENT_DATE_TIME = pref.getString("tanggal_akhir", "").toString()+" 24:00:00";
        countDownStart();
        return v;
    }

    private void kode_pendaftaran() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        user.setName(pref.getString(Constants.NAME, ""));
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_KODE);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    mLoginProgress.dismiss();
                    ((MainActivity)getActivity()).cekStatus(pref.getString(Constants.ID,""), pref.getString(Constants.EMAIL,""), "fragment");
                }
                else {
                    mLoginProgress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {

                mLoginProgress.dismiss();
                Snackbar.make(getView(), "Koneksi internet atau jaringan bermasalah, periksa kembali " +
                        "koneksi internet dan coba lagi.", Snackbar.LENGTH_LONG).show();
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

    @Override
    public void onStop() {
        super.onStop();
        handler.removeCallbacks(runnable);
    }
}
