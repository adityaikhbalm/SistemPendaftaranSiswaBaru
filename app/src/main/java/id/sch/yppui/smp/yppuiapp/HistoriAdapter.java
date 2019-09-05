package id.sch.yppui.smp.yppuiapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ipin on 7/15/2018.
 */

public class HistoriAdapter extends ArrayAdapter<HistoriResult> {
    private List<HistoriResult> arrayListData;

    public HistoriAdapter(Context context, int resource, List<HistoriResult> objects){
        super(context,resource,objects);
        this.arrayListData = objects;
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        View v = convertView;
        if(v == null){
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = li.inflate(R.layout.list_histori_pembayaran,null);
        }

        HistoriResult ha = arrayListData.get(pos);
        if(ha != null){
            TextView no = (TextView) v.findViewById(R.id.txtNoHistori);
            TextView nama = (TextView) v.findViewById(R.id.txtNamaHistori);
            Button download = (Button) v.findViewById(R.id.btnDownloadHistori);
            no.setText(String.valueOf(pos+1));
            nama.setText(ha.getNm_biaya());

            download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ((MainActivity) FromService.currentActivity()).cetakBukti(ha.getNo_pembayaran(), ha.getNo_angsuran(), ha.getKd_biaya());
                }
            });
        }

        return v;
    }
}
