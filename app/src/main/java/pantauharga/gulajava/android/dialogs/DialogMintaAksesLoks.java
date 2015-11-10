package pantauharga.gulajava.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.BaseActivityLocation;


/**
 * Created by Gulajava Ministudio on 9/20/15.
 */


public class DialogMintaAksesLoks extends DialogFragment {


    private BaseActivityLocation baseLocationAktivitas;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        baseLocationAktivitas = (BaseActivityLocation) DialogMintaAksesLoks.this.getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogMintaAksesLoks.this.getActivity());
        builder.setMessage(R.string.teks_isimintalokasi);
        builder.setPositiveButton(R.string.teks_ok, listenerdialogok);
        builder.setNegativeButton(R.string.teks_batal, listenerbatal);


        return builder.create();
    }


    DialogInterface.OnClickListener listenerdialogok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            baseLocationAktivitas.mintaLokasi();

            DialogMintaAksesLoks.this.dismiss();
        }
    };

    DialogInterface.OnClickListener listenerbatal = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {


            DialogMintaAksesLoks.this.dismiss();
        }
    };


}
