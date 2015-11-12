package pantauharga.gulajava.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.WindowManager;

import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.LaporHargaEdit;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class DialogOkKirimEdit extends DialogFragment {

    private LaporHargaEdit mLaporHargaAkt;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mLaporHargaAkt = (LaporHargaEdit) DialogOkKirimEdit.this.getActivity();

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogOkKirimEdit.this.getActivity());
        builder.setMessage(R.string.lapor_okkirimdata);
        builder.setPositiveButton(R.string.teks_dialog_selesai, listenerok);

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }


    DialogInterface.OnClickListener listenerok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            mLaporHargaAkt.setOkTerkirim();
            DialogOkKirimEdit.this.dismiss();
        }
    };


}
