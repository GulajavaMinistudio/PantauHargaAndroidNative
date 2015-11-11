package pantauharga.gulajava.android.adapters;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import pantauharga.gulajava.android.R;

/**
 * Created by Gulajava Ministudio on 11/11/15.
 */
public class ViewHolderan {

    private View mView;

    private TextView teks_namakomoditas = null;
    private TextView teks_hargakomoditas = null;
    private TextView teks_alamatkomoditas = null;

    private ImageView gambar_statuskirim = null;

    public ViewHolderan(View view) {
        mView = view;
    }


    public TextView getTeks_namakomoditas() {

        if (teks_namakomoditas == null) {
            teks_namakomoditas = (TextView) mView.findViewById(R.id.teks_namakomoditas);
        }

        return teks_namakomoditas;
    }

    public TextView getTeks_hargakomoditas() {

        if (teks_hargakomoditas == null) {
            teks_hargakomoditas = (TextView) mView.findViewById(R.id.teks_hargakomoditas);
        }

        return teks_hargakomoditas;
    }

    public TextView getTeks_alamatkomoditas() {

        if (teks_alamatkomoditas == null) {
            teks_alamatkomoditas = (TextView) mView.findViewById(R.id.teks_lokasilaporan);
        }

        return teks_alamatkomoditas;
    }

    public ImageView getGambar_statuskirim() {

        if (gambar_statuskirim == null) {
            gambar_statuskirim = (ImageView) mView.findViewById(R.id.gambarstatuskirim);
        }
        return gambar_statuskirim;
    }
}
