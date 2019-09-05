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
 * Created by ipin on 7/17/2018.
 */

public class LulusFragment extends Fragment {

    private SharedPreferences pref;
    private TextView nama, kelas, guru;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.lulus_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        nama = (TextView) v.findViewById(R.id.txtNamaSiswa);
        kelas = (TextView) v.findViewById(R.id.txtKelas);
        guru = (TextView) v.findViewById(R.id.txtGuru);
        nama.setText("Nama : "+pref.getString(Constants.NAMA_SISWA, ""));
        kelas.setText("Kelas : "+pref.getString(Constants.KELAS, ""));
        guru.setText("Wali Kelas : "+pref.getString(Constants.GURU, ""));

        return v;
    }
}
