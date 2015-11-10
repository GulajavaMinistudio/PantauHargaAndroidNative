package pantauharga.gulajava.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.LoginRegistersPengguna;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class DialogPeringatanLoginDulu extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);


        AlertDialog.Builder builder = new AlertDialog.Builder(DialogPeringatanLoginDulu.this.getActivity());
        builder.setMessage(R.string.toast_loginmasukdulu);
        builder.setPositiveButton(R.string.teks_ok, listenerok);
        builder.setNegativeButton(R.string.teks_batal, listenerbatal);

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }


    DialogInterface.OnClickListener listenerok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            Intent intentadminpengguna = new Intent(DialogPeringatanLoginDulu.this.getActivity(), LoginRegistersPengguna.class);
            DialogPeringatanLoginDulu.this.startActivity(intentadminpengguna);
            DialogPeringatanLoginDulu.this.dismiss();
        }
    };

    DialogInterface.OnClickListener listenerbatal = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            DialogPeringatanLoginDulu.this.dismiss();
        }
    };


}
