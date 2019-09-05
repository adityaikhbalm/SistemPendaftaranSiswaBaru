package id.sch.yppui.smp.yppuiapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ipin on 6/27/2018.
 */

public class VerifikasiPembayaranSekolahFragment extends Fragment {

    private SharedPreferences pref;
    private TextView nodaftar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.verifikasi_pembayaran_sekolah_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        nodaftar = (TextView) v.findViewById(R.id.txtNoPendaftaranSekolah);
        nodaftar.setText(pref.getString(Constants.NO_PENDAFTARAN,""));

        return v;
    }
}
