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

public class TidakLulusFragment extends Fragment {

    private SharedPreferences pref;
    private TextView nama;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tidak_lulus_fragment, container, false);

        pref = getActivity().getSharedPreferences("Login", MODE_PRIVATE);
        nama = (TextView) v.findViewById(R.id.txtNama);
        nama.setText(pref.getString(Constants.NAMA_SISWA, pref.getString(Constants.NAME,"")));

        return v;
    }
}
