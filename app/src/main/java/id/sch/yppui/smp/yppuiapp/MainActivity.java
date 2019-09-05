package id.sch.yppui.smp.yppuiapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import id.sch.yppui.smp.yppuiapp.models.ServerRequest;
import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
import id.sch.yppui.smp.yppuiapp.models.User;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private String filename;
    private Toolbar mToolbar;
    private SharedPreferences pref;
    private TextView message;
    private EditText editOldPassword, editNewPassword;
    private ProgressDialog mLoadingProgress, mPasswordProgress, mDownloadProgress;;
    private AlertDialog PasswordDialog;
    private String status, tanggal_awal, tanggal_akhir, tanggal_pembayaran, tanggal_pengumuman;
    private Fragment ganti;
    private List<HistoriResult> arrayList;
    private HistoriAdapter adapterHistori;
    private ListView listHistori;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        pref = getApplicationContext().getSharedPreferences("Login", MODE_PRIVATE);
        mToolbar = (Toolbar) findViewById(R.id.main_menu_toolbar);
        setSupportActionBar(mToolbar);
//        ganti = new HomeFragment();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            changeStatusKoneksi(true);
        } else {
            changeStatusKoneksi(false);
        }
    }

    private void changeStatusKoneksi(boolean isConnected) {
        if (isConnected) {
            cekStatus(pref.getString(Constants.ID,""), pref.getString(Constants.EMAIL,""), "activity");
        } else {
            Fragment fragment = new KoneksiFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_frame, fragment);
            ft.commit();
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        private boolean firstConnect = true;

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                boolean isVisible = MyApplication.isActivityVisible();
                Log.i("Activity is Visible ", "Is activity visible : " + isVisible);

                // If it is visible then trigger the task else do nothing
                if (isVisible == true) {
                    ConnectivityManager connectivityManager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {
                        if(firstConnect){
                            firstConnect = false;
                            changeStatusKoneksi(true);
                        }
                    } else {
                        if(!firstConnect) {
                            firstConnect = true;
                            changeStatusKoneksi(false);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        FromService.setCurrentActivity(null);
        MyApplication.activityPaused();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FromService.setCurrentActivity(this);
        MyApplication.activityResumed();
        registerReceiver(broadcastReceiver, new IntentFilter("Koneksi"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_item, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {
            FirebaseMessaging.getInstance().unsubscribeFromTopic(pref.getString(Constants.ID,""));
            FirebaseMessaging.getInstance().unsubscribeFromTopic("status");

            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
//            editor.commit();
            editor.apply();

            Intent mainIntent = new Intent(MainActivity.this, StartActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            finish();
        }

        if (item.getItemId() == R.id.main_ubah_password_btn) {
            showDialog();
        }

        if (item.getItemId() == R.id.main_histori_pembayaran_btn) {
            showDialogHistori(pref.getString(Constants.NO_PENDAFTARAN,""));
        }

        return true;
    }

    private void showDialogHistori(String no_pendaftaran) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_histori_pembayaran, null);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        Call<List<HistoriResult>> response = requestInterface.bukti_pembayaran(no_pendaftaran);
        response.enqueue(new Callback<List<HistoriResult>>() {
            @Override
            public void onResponse(Call<List<HistoriResult>> call, Response<List<HistoriResult>> response) {
                SharedPreferences.Editor editor = pref.edit();

                arrayList = response.body();
                adapterHistori = new HistoriAdapter(getApplicationContext(),R.layout.list_histori_pembayaran,arrayList);
                listHistori = (ListView) view.findViewById(R.id.listHistori);
                listHistori.setAdapter(adapterHistori);
            }

            @Override
            public void onFailure(Call<List<HistoriResult>> call, Throwable t) {
                Snackbar.make(findViewById(android.R.id.content), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
            }
        });

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);

        editOldPassword = (EditText)view.findViewById(R.id.editOldPassword);
        editNewPassword = (EditText)view.findViewById(R.id.editNewPassword);
        message = (TextView)view.findViewById(R.id.textMessage);

        builder.setView(view);
        builder.setPositiveButton("Yes", null);
        builder.setNegativeButton("No", null);

        PasswordDialog = builder.create();
        PasswordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveButton = PasswordDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String old_password = editOldPassword.getText().toString().trim();
                        String new_password = editNewPassword.getText().toString().trim();

                        if (!old_password.isEmpty() && !new_password.isEmpty()) {
                            mPasswordProgress = new ProgressDialog(MainActivity.this);
                            mPasswordProgress.setTitle("Ubah Password");
                            mPasswordProgress.setMessage("Mohon Tunggu ");
                            mPasswordProgress.setCanceledOnTouchOutside(false);
                            mPasswordProgress.show();

                            changePasswordProcess(pref.getString(Constants.EMAIL,""), old_password, new_password);
                        }
                        else {
                            message.setVisibility(View.VISIBLE);
                            message.setText(" Semua field harus di isi!");
                        }
                    }
                });

                Button negativeButton = PasswordDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        PasswordDialog.show();
    }

    public void cekStatus(String id, String email, String dari) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                mLoadingProgress = new ProgressDialog(MainActivity.this);
                mLoadingProgress.setMessage("Loading");
                mLoadingProgress.setCanceledOnTouchOutside(false);
                mLoadingProgress.show();
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.STATUS_USER);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                if (resp.getResult().equals(Constants.SUCCESS)) {
                    status = resp.getUser().getStatus().toString();
                    SharedPreferences.Editor editor = pref.edit();

                    try {
                        if(status != "blank") {
                            tanggal_awal = resp.getUser().getTanggal_awal().toString();
                            tanggal_akhir = resp.getUser().getTanggal_akhir().toString();
                            tanggal_pembayaran = resp.getUser().getTanggal_pembayaran().toString();
                            tanggal_pengumuman = resp.getUser().getTanggal_pengumuman().toString();

                            if (status.equals("bayarformulir")) {
                                editor.putString(Constants.HARGA_FORMULIR,resp.getUser().getHarga_formulir());
                            }
                            else {
                                if (pref.getString(Constants.HARGA_FORMULIR, null) != null)
                                    editor.remove(Constants.HARGA_FORMULIR).apply();
                            }

                            if (status.equals("hasilpengumuman")) {
                                editor.putString(Constants.NAMA_SISWA,resp.getUser().getNama_siswa());
                                editor.putString(Constants.KELAS,resp.getUser().getKelas());
                                editor.putString(Constants.GURU,resp.getUser().getGuru());
                            }
                            else {
                                if (pref.getString(Constants.NAMA_SISWA, null) != null) {
                                    editor.remove(Constants.NAMA_SISWA);
                                    editor.remove(Constants.KELAS);
                                    editor.remove(Constants.GURU);
                                }
                            }

                            editor.putString(Constants.SISA_PEMBAYARAN,resp.getUser().getSisa_pembayaran());
                            editor.putString(Constants.HARGA_SEKOLAH,resp.getUser().getHarga_sekolah());
                            editor.putString(Constants.ANGSURAN,resp.getUser().getAngsuran());
                            editor.putString(Constants.NO_PENDAFTARAN, resp.getUser().getNodaftar().toString());
                            editor.putString("tanggal_awal", tanggal_awal);
                            editor.putString("tanggal_akhir", tanggal_akhir);
                            editor.putString("tanggal_pembayaran", tanggal_pembayaran);
                            editor.putString("tanggal_pengumuman", tanggal_pengumuman);
                        }
                    }
                    catch (Exception ex) {
                        mLoadingProgress.dismiss();
                    }

                    editor.putString(Constants.STATUS, status);
                    editor.apply();

                    ubah(status);
                    mLoadingProgress.dismiss();
                    komit(dari);
                }
                else {
                    ubah(pref.getString(Constants.STATUS, ""));
                    mLoadingProgress.dismiss();
                    komit(dari);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                ubah(pref.getString(Constants.STATUS, ""));
                mLoadingProgress.dismiss();
                komit(dari);
            }
        });

//        try {
////            Date now = new Date();
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            Date now = dateFormat.parse(dateFormat.format(new Date()));
//            Date tanggal_akhir2 = dateFormat.parse(pref.getString("tanggal_akhir","")+" 24:00:00");
//            Date tanggal_pembayaran2 = dateFormat.parse(pref.getString("tanggal_pembayaran","")+" 24:00:00");
//            Date tanggal_pengumuman2 = dateFormat.parse(pref.getString("tanggal_pengumuman","")+" 24:00:00");
//
//            if (now.after(tanggal_akhir2)) {
//                Toast.makeText(this, "berakhir", Toast.LENGTH_LONG).show();
//                if (pref.getString(Constants.STATUS,"").equals("home") || pref.getString(Constants.STATUS,"").equals("kode")) {
//
//                }
//                else if (status.equals("bayarformulir") || status.equals("isiformulir")) {
//                    String bayar = pref.getString(Constants.BAYAR_FORMULIR,"");
//                    String isi = pref.getString(Constants.ISI_FORMULIR,"");
//                    if (bayar.equals("belum") || isi.equals("belum")) {
//
//                    }
//                }
//            }
//            else if (now.after(tanggal_akhir2) && now.after(tanggal_pembayaran2)) {
//                if (status.equals("bayarsekolah")) {
//
//                }
//            }
//            else if (now.after(tanggal_akhir2) && now.after(tanggal_pembayaran2) && now.after(tanggal_pengumuman2)) {
//                if (status.equals("pengumuman")) {
//
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void komit(String dari) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(dari.equals("fragment")) {
            ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_from_right);
        }
        ft.replace(R.id.fragment_frame, ganti);
        ft.commit();
    }

    private void ubah(String status) {
        if (status.equals("blank")) {
            ganti = new BlankFragment();
        }
        else if (status.equals("home")) {
            ganti = new HomeFragment();
        }
        else if (status.equals("kode")) {
            ganti = new KodeFragment();
        }
        else if (status.equals("bayarformulir")) {
            ganti = new BayarFormulirFragment();
        }
        else if (status.equals("verifikasipembayaranformulir")) {
            ganti = new VerifikasiPembayaranFormulirFragment();
        }
        else if (status.equals("isiformulir")) {
            ganti = new IsiFormulirFragment();
        }
        else if (status.equals("verifikasiisiformulir")) {
            ganti = new VerifikasiIsiFormulirFragment();
        }
        else if (status.equals("diskon")) {
            ganti = new DiskonFragment();
        }
        else if (status.equals("bayarsekolah")) {
            ganti = new BayarSekolahFragment();
        }
        else if (status.equals("verifikasipembayaransekolah")) {
            ganti = new VerifikasiPembayaranSekolahFragment();
        }
        else if (status.equals("bayarangsuran")) {
            ganti = new BayarAngsuranFragment();
        }
        else if (status.equals("verifikasipembayaranangsuran")) {
            ganti = new VerifikasiPembayaranSekolahFragment();
        }
        else if (status.equals("pengumuman")) {
            ganti = new PengumumanFragment();
        }
        else if (status.equals("hasilpengumuman")) {
            String cek = pref.getString(Constants.NAMA_SISWA,"");
            if(cek.equals("")) {
                ganti = new TidakLulusFragment();
            }
            else {
                ganti = new LulusFragment();
            }
        }
        else if (status.equals("bayarangsuran2")) {
            ganti = new BayarAngsuran2Fragment();
        }
        else if (status.equals("verifikasipembayaranangsuran2")) {
            ganti = new VerifikasiPembayaranSekolahFragment();
        }
    }

    private void changePasswordProcess(String email,String old_password,String new_password){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();
                if (resp.getResult().equals(Constants.SUCCESS)) {
                    message.setVisibility(View.GONE);
                    mPasswordProgress.dismiss();
                    PasswordDialog.dismiss();
                    Snackbar.make(findViewById(android.R.id.content), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                else {
                    mPasswordProgress.dismiss();
                    message.setVisibility(View.VISIBLE);
                    message.setText(resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                mPasswordProgress.dismiss();
                message.setVisibility(View.GONE);
                Snackbar.make(findViewById(android.R.id.content), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename (String filename) {
        this.filename = filename;
    }

    public void cetakBukti(String no_pembayaran, String no_angsuran, String kd_biaya) {

        mDownloadProgress = new ProgressDialog(this);
        mDownloadProgress.setTitle("Download File");
        mDownloadProgress.setMessage("Mohon Tunggu ");
        mDownloadProgress.setCanceledOnTouchOutside(false);
        mDownloadProgress.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Call<ResponseBody> download = requestInterface.cetak_bukti_pembayaran(no_pembayaran, no_angsuran, kd_biaya);

        download.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String content = response.headers().get("Content-Disposition");
                    String contentSplit[] = content.split("filename=");
                    String filename = contentSplit[1].replace("filename=", "").replace("\"", "").trim();
                    setFilename(filename);

                    DownloadFileAsyncTask downloadFileAsyncTask = new DownloadFileAsyncTask();
                    downloadFileAsyncTask.execute(response.body().byteStream());
                }
                catch (Exception ex) {
                    Snackbar.make(findViewById(android.R.id.content), "Data tidak ditemukan.", Snackbar.LENGTH_LONG).show();
                    mDownloadProgress.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mDownloadProgress.dismiss();
                Snackbar.make(findViewById(android.R.id.content), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private class DownloadFileAsyncTask extends AsyncTask<InputStream, Void, Boolean> {

        @Override
        protected Boolean doInBackground(InputStream... params) {
            InputStream inputStream = params[0];
            File file = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), getFilename());
            OutputStream output = null;
            try {
                output = new FileOutputStream(file);

                byte[] buffer = new byte[1024]; // or other buffer size
                int read;

                while ((read = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e){
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            callnotif();
            mDownloadProgress.dismiss();
        }
    }

    private void callnotif() {
        String title = "File Download";
        String message = getFilename();
        String channelId = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.mipmap.logo_yppui)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setAutoCancel(true);

        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), message);


        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String ext = file.getName().substring(file.getName().indexOf(".")+1);
        String type = mime.getMimeTypeFromExtension(ext);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.fromFile(file));
        resultIntent.setDataAndType(Uri.fromFile(file), type);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        0
                );

        builder.setContentIntent(resultPendingIntent);

        int mNotificationId = (int) System.currentTimeMillis();
        NotificationManager nm = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        nm.notify(mNotificationId, builder.build());
    }
}