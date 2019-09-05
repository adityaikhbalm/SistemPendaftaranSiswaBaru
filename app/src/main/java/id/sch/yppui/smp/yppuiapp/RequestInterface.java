package id.sch.yppui.smp.yppuiapp;

import java.util.List;

import id.sch.yppui.smp.yppuiapp.models.ServerRequest;
import id.sch.yppui.smp.yppuiapp.models.ServerResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * Created by ipin on 4/8/2018.
 */

public interface RequestInterface {

    @POST("smp_yppui/")
    Call<ServerResponse> operation(@Body ServerRequest request);

    @FormUrlEncoded
    @POST("api/change-diskon")
    Call<ServerResponse> change_diskon(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/diskon")
    Call<ServerResponse> diskon(
            @Field("id") String id,
            @Field("no_pendaftaran") String no_pendaftaran,
            @Field("kd_diskon") String kd_diskon
    );

    @FormUrlEncoded
    @POST("api/list-bukti-pembayaran")
    Call<List<HistoriResult>> bukti_pembayaran(@Field("no_pendaftaran") String no_pendaftaran);

    @Streaming
    @FormUrlEncoded
    @POST("api/cetak-bukti-pembayaran")
    Call<ResponseBody> cetak_bukti_pembayaran(
            @Field("no_pembayaran") String no_pembayaran,
            @Field("no_angsuran") String no_angsuran,
            @Field("kd_biaya") String kd_biaya
    );
}
