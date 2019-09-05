package id.sch.yppui.smp.yppuiapp.models;

import android.content.Intent;

/**
 * Created by ipin on 4/8/2018.
 */

public class User {

    private String id;
    private String name;
    private String email;
    private String status;
    private String kode;
    private String nodaftar;
    private String bayar_formulir;
    private String bayar_sekolah;
    private String isiformulir;
    private String tanggal_awal;
    private String tanggal_akhir;
    private String tanggal_pembayaran;
    private String tanggal_pengumuman;
    private String password;
    private String old_password;
    private String new_password;
    private String harga_formulir;
    private String harga_sekolah;
    private String angsuran;
    private String sisa_pembayaran;
    private String nama_siswa;
    private String kelas;
    private String guru;

    public String getGuru() {
        return guru;
    }

    public String getNama_siswa() {
        return nama_siswa;
    }

    public String getKelas() {
        return kelas;
    }

    public String getSisa_pembayaran() {
        return sisa_pembayaran;
    }

    public String getAngsuran() {
        return angsuran;
    }

    public String getHarga_formulir() {
        return harga_formulir;
    }

    public String getHarga_sekolah() {
        return harga_sekolah;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStatus() {
        return status;
    }

    public String getNodaftar() {
        return nodaftar;
    }

    public String getBayarformulir() {
        return bayar_formulir;
    }

    public String getBayarsekolah() {
        return bayar_sekolah;
    }

    public String getIsiformulir() {
        return isiformulir;
    }

    public String getTanggal_awal() {
        return tanggal_awal;
    }

    public String getTanggal_akhir() {
        return tanggal_akhir;
    }

    public String getTanggal_pembayaran() {
        return tanggal_pembayaran;
    }

    public String getTanggal_pengumuman() {
        return tanggal_pengumuman;
    }

    public void setId (String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setKode(String kode) {
        this.kode = kode;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setOld_password(String old_password) {
        this.old_password = old_password;
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
