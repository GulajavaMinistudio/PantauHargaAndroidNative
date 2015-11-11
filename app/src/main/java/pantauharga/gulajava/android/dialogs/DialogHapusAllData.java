package pantauharga.gulajava.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.LaporRiwayat;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class DialogHapusAllData extends DialogFragment {

    private LaporRiwayat mLaporRiwayat;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mLaporRiwayat = (LaporRiwayat) DialogHapusAllData.this.getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogHapusAllData.this.getActivity());
        builder.setMessage(R.string.riwayat_ingathapus);
        builder.setPositiveButton(R.string.teks_ok, listenerok);
        builder.setNegativeButton(R.string.teks_batal, listenerkirimlain);

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }


    DialogInterface.OnClickListener listenerok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            mLaporRiwayat.hapusDataSemua();
            DialogHapusAllData.this.dismiss();
        }
    };

    DialogInterface.OnClickListener listenerkirimlain = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            DialogHapusAllData.this.dismiss();
        }
    };


}
