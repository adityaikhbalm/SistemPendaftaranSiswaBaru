package id.sch.yppui.smp.yppuiapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import id.sch.yppui.smp.yppuiapp.models.ServerRequest;
import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
import id.sch.yppui.smp.yppuiapp.models.User;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ipin on 4/16/2018.
 */

public class BayarFormulirFragment extends Fragment {

    private String EVENT_DATE_TIME;
    private String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private String status = "kosong", part_image;
    private Button Pilih, Upload, Detil;
    private TextView Hari, Jam, Menit, Detik;
    private ImageView Gambar;
    private SharedPreferences pref;
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final int REQUEST_GALLERY = 9;
    private ProgressDialog loadingProgress;
    private AlertDialog bayarFormulirDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bayar_formulir_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        Hari = (TextView) v.findViewById(R.id.txtHari);
        Jam = (TextView) v.findViewById(R.id.txtJam);
        Menit = (TextView) v.findViewById(R.id.txtMenit);
        Detik = (TextView) v.findViewById(R.id.txtDetik);
        Gambar = (ImageView) v.findViewById(R.id.buktiPembayaran);
        Pilih = (Button) v.findViewById(R.id.btnPilih);
        Upload = (Button) v.findViewById(R.id.btnUpload);
        Detil = (Button) v.findViewById(R.id.btnDetil);

        Pilih.setOnClickListener(mListener);
        Upload.setOnClickListener(mListener);
        Detil.setOnClickListener(mListener);

        Upload.setEnabled(false);
        Upload.setBackgroundColor(Color.parseColor("#D7DCDA"));

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));
        EVENT_DATE_TIME = pref.getString("tanggal_akhir", "")+" 24:00:00";
		countDownStart();

        return v;
    }

    private final View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.btnPilih:
                    Intent i = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_GALLERY);
                    break;
                case R.id.btnUpload:
                    loadingProgress = new ProgressDialog(getActivity());
                    loadingProgress.setTitle("Upload Gambar");
                    loadingProgress.setMessage("Mohon Tunggu ");
                    loadingProgress.setCanceledOnTouchOutside(false);
                    loadingProgress.show();

                    uploadGambar();
                    break;
                case R.id.btnDetil:
                    detilPembayaranFormulir();
                    break;
            }
        }
    };

    private void detilPembayaranFormulir() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_detil_pembayaran_formulir, null);

        builder.setView(view);

        bayarFormulirDialog = builder.create();
        bayarFormulirDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bayarFormulirDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;
        bayarFormulirDialog.setCanceledOnTouchOutside(false);
        bayarFormulirDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button btnFormulir = (Button) view.findViewById(R.id.btnBayarFormulir);
                TextView harga = (TextView) view.findViewById(R.id.txtHarga);
                int formulir = Integer.parseInt(pref.getString(Constants.HARGA_FORMULIR,""));
                NumberFormat format = NumberFormat.getInstance(new Locale("id", "ID"));

                harga.setText("Rp. "+format.format(formulir));
                btnFormulir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        bayarFormulirDialog.show();
    }


    private void uploadGambar() {
        try {
            File imageFile = new File(part_image);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            UploadImageInterface upload = retrofit.create(UploadImageInterface.class);

            RequestBody image = RequestBody.create(MediaType.parse("image/*"), imageFile);
            MultipartBody.Part bukti = MultipartBody.Part.createFormData("bayarformulir", imageFile.getName(), image);
            RequestBody id = RequestBody.create(MediaType.parse("text/plain"), pref.getString(Constants.ID, ""));

            Call<ServerResponse> response = upload.uploadImage(id, bukti);
            response.enqueue(new Callback<ServerResponse>() {
                @Override
                public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {

                    ServerResponse resp = response.body();

                    if(resp.getResult().equals(Constants.SUCCESS)){
                        loadingProgress.dismiss();
                        ((MainActivity)getActivity()).cekStatus(pref.getString(Constants.ID,""), pref.getString(Constants.EMAIL,""), "fragment");
                    }
                    else {
                        loadingProgress.dismiss();
                    }

                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(Call<ServerResponse> call, Throwable t) {
                    loadingProgress.dismiss();
                    Snackbar.make(getView(), "Jaringan anda atau server bermasalah.", Snackbar.LENGTH_LONG).show();
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == Activity.RESULT_OK && data != null) {
            Uri dataImage = data.getData();
            String[] imageProjection = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(dataImage,
                    imageProjection, null, null, null);

            if(cursor != null) {
                cursor.moveToFirst();
                int indexImage = cursor.getColumnIndex(imageProjection[0]);
                part_image = cursor.getString(indexImage);
                cursor.close();

                if(part_image != null) {
                    Gambar.setImageBitmap(CompressImage.decodeSampledBitmapFromFile(part_image,500,500));
                    Upload.setEnabled(true);
                    Upload.setBackgroundColor(Color.parseColor("#303F9F"));
                }
            }
        }
//        else {
//            Snackbar.make(getView(), "Gagal, mohon coba lagi.", Snackbar.LENGTH_LONG).show();
//        }
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
                    }
                    else {
                        handler.removeCallbacks(runnable);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 0);
    }

    public void cekStatus() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setId(pref.getString(Constants.ID, ""));
        user.setEmail(pref.getString(Constants.EMAIL, ""));
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.STATUS_USER);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {

                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    status = resp.getUser().getStatus().toString();
                    if (status == pref.getString(Constants.STATUS, "")) {
//                        Toast.makeText(getActivity(), "Tetap", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(getActivity(), "Berubah", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
            }
        });
    }
}
