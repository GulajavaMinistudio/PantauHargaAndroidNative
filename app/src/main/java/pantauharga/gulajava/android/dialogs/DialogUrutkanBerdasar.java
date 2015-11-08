package pantauharga.gulajava.android.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.MenuUtama;

/**
 * Created by Gulajava Ministudio on 11/8/15.
 */
public class DialogUrutkanBerdasar extends DialogFragment {


    @Bind(R.id.spin_modeurutan)
    Spinner spinmodeurutan;
    private ArrayAdapter<String> adaptermodeurutan;
    private String[] strarrayurutan;

    private Bundle mBundle;
    private int posisipilihan = 0;

    private MenuUtama mMenuUtama;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        View viewdialog = LayoutInflater.from(DialogUrutkanBerdasar.this.getActivity())
                .inflate(R.layout.layout_dialogurutkan, null, false);
        ButterKnife.bind(DialogUrutkanBerdasar.this, viewdialog);

        mMenuUtama = (MenuUtama) DialogUrutkanBerdasar.this.getActivity();

        mBundle = DialogUrutkanBerdasar.this.getArguments();
        posisipilihan = mBundle.getInt(Konstan.TAG_INTENT_POSISIURUTANPILIH);

        strarrayurutan = DialogUrutkanBerdasar.this.getResources().getStringArray(R.array.array_modeurutan);
        adaptermodeurutan = new ArrayAdapter<>(DialogUrutkanBerdasar.this.getActivity(), android.R.layout.simple_spinner_item, strarrayurutan);
        adaptermodeurutan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinmodeurutan.setAdapter(adaptermodeurutan);
        spinmodeurutan.setSelection(posisipilihan);
        spinmodeurutan.setOnItemSelectedListener(listenerspin);

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogUrutkanBerdasar.this.getActivity());
        builder.setView(viewdialog);
        builder.setPositiveButton(R.string.teks_ok, listenerok);
        builder.setNegativeButton(R.string.teks_batal, listenerbatal);

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(DialogUrutkanBerdasar.this);
    }


    AdapterView.OnItemSelectedListener listenerspin = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            posisipilihan = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


    DialogInterface.OnClickListener listenerok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            mMenuUtama.setModeUrutanList(posisipilihan);
            DialogUrutkanBerdasar.this.getDialog().dismiss();
        }
    };

    DialogInterface.OnClickListener listenerbatal = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {


            DialogUrutkanBerdasar.this.getDialog().dismiss();
        }
    };


}
