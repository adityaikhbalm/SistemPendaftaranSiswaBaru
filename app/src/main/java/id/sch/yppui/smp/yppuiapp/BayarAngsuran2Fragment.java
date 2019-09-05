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
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
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
 * Created by ipin on 7/17/2018.
 */

public class BayarAngsuran2Fragment extends Fragment {
    private String status = "kosong", part_image;
    private Button Pilih, Upload, Detil;
    private ImageView Gambar;
    private SharedPreferences pref;
    private static final int REQUEST_GALLERY = 9;
    private ProgressDialog loadingProgress;
    private AlertDialog bayarSekolahDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bayar_angsuran2_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        Gambar = (ImageView) v.findViewById(R.id.buktiPembayaran);
        Pilih = (Button) v.findViewById(R.id.btnPilih);
        Upload = (Button) v.findViewById(R.id.btnUpload);
        Detil = (Button) v.findViewById(R.id.btnDetil);

        Pilih.setOnClickListener(mListener);
        Upload.setOnClickListener(mListener);
        Detil.setOnClickListener(mListener);

        Upload.setEnabled(false);
        Upload.setBackgroundColor(Color.parseColor("#D7DCDA"));

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
                    detilPembayaranSekolah();
                    break;
            }
        }
    };

    private void detilPembayaranSekolah() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sisa_pembayaran_sekolah, null);

        builder.setView(view);

        bayarSekolahDialog = builder.create();
        bayarSekolahDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        bayarSekolahDialog.getWindow().getAttributes().windowAnimations = R.style.DialogSlide;
        bayarSekolahDialog.setCanceledOnTouchOutside(false);
        bayarSekolahDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button btnSekolah = (Button) view.findViewById(R.id.btnBayarSekolah);
                TextView harga = (TextView) view.findViewById(R.id.txtHarga);
                int sisa = Integer.parseInt(pref.getString(Constants.SISA_PEMBAYARAN,""));
                NumberFormat format = NumberFormat.getInstance(new Locale("id", "ID"));

                harga.setText("Rp. "+format.format(sisa)+",-");
                btnSekolah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        bayarSekolahDialog.show();
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
            MultipartBody.Part bukti = MultipartBody.Part.createFormData("bayarsekolah", imageFile.getName(), image);
            RequestBody id = RequestBody.create(MediaType.parse("text/plain"), pref.getString(Constants.ID, ""));
            RequestBody no_pendaftaran = RequestBody.create(MediaType.parse("text/plain"), pref.getString(Constants.NO_PENDAFTARAN, ""));

            Call<ServerResponse> response = upload.uploadImage3(id, no_pendaftaran, bukti);
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
}
