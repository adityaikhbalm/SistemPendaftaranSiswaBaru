package id.sch.yppui.smp.yppuiapp;

import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by ipin on 6/5/2018.
 */

public interface UploadImageInterface {
    @Multipart
    @POST("api/bayar-formulir")
    Call<ServerResponse> uploadImage(@Part("id") RequestBody id, @Part MultipartBody.Part image);

    @Multipart
    @POST("api/bayar-sekolah")
    Call<ServerResponse> uploadImage2(@Part("id") RequestBody id, @Part("no_pendaftaran") RequestBody no_pendaftaran, @Part MultipartBody.Part image);

    @Multipart
    @POST("api/bayar-sekolah")
    Call<ServerResponse> uploadImage3(@Part("id") RequestBody id, @Part("no_pendaftaran") RequestBody no_pendaftaran, @Part MultipartBody.Part image);
}