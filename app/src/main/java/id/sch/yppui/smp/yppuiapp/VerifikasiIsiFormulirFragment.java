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
 * Created by ipin on 7/5/2018.
 */

public class VerifikasiIsiFormulirFragment extends Fragment {

    private SharedPreferences pref;
    private TextView nodaftar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.verifikasi_isi_formulir_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        nodaftar = (TextView) v.findViewById(R.id.txtNoPendaftaranIsiFormulir);
        nodaftar.setText(pref.getString(Constants.NO_PENDAFTARAN,""));

        return v;
    }
}
