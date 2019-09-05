package id.sch.yppui.smp.yppuiapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ipin on 7/15/2018.
 */

public class HistoriResult {

    @SerializedName("nm_biaya")
    @Expose
    String nm_biaya;
    @SerializedName("no_pembayaran")
    @Expose
    String no_pembayaran;
    @SerializedName("no_angsuran")
    @Expose
    String no_angsuran;
    @SerializedName("kd_biaya")
    @Expose
    String kd_biaya;

    public String getNm_biaya() {
        return nm_biaya;
    }

    public String getNo_pembayaran() {
        return no_pembayaran;
    }

    public String getNo_angsuran() {
        return no_angsuran;
    }

    public String getKd_biaya() {
        return kd_biaya;
    }
}
