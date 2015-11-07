package pantauharga.gulajava.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import pantauharga.gulajava.android.R;


/**
 * Created by Gulajava Ministudio on 9/20/15.
 */


public class DialogGagalGpsLokasi extends DialogFragment {


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogGagalGpsLokasi.this.getActivity());
        builder.setMessage(R.string.toastdialog_pesangagalgps);
        builder.setPositiveButton(R.string.teks_ok, listenerdialogok);


        return builder.create();
    }


    DialogInterface.OnClickListener listenerdialogok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            DialogGagalGpsLokasi.this.dismiss();
        }
    };


}
