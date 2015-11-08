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
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.MenuUtama;

/**
 * Created by Gulajava Ministudio on 11/8/15.
 */


public class DialogKomoditasCarian extends DialogFragment {


    private View mViewDialog;

    @Bind(R.id.spin_namakomoditas)
    Spinner spinkomoditas;
    private ArrayAdapter<String> adapterspin;

    @Bind(R.id.seekbar_radius)
    SeekBar mSeekBarRadius;

    @Bind(R.id.teks_radius)
    TextView teks_radiusketerangan;

    private List<String> mStringListSpin;
    private int intradius = 100;
    private int posisipilih = 0;

    private Bundle mBundle;

    private MenuUtama mMenuUtamaAkt;

    private int posisipilihanpilih = 0;
    private String namakomoditaspilih = "";
    private int intradiuspilih = 100;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        mMenuUtamaAkt = (MenuUtama) DialogKomoditasCarian.this.getActivity();

        mBundle = DialogKomoditasCarian.this.getArguments();
        intradius = mBundle.getInt(Konstan.TAG_INTENT_RADIUS);
        mStringListSpin = mBundle.getStringArrayList(Konstan.TAG_INTENT_ARRAYKOMODITAS);
        posisipilih = mBundle.getInt(Konstan.TAG_INTENT_POSISIPILIH);

        mViewDialog = LayoutInflater.from(DialogKomoditasCarian.this.getActivity()).inflate(R.layout.layout_dialogcarikomoditas, null, false);
        ButterKnife.bind(DialogKomoditasCarian.this, mViewDialog);

        adapterspin = new ArrayAdapter<>(DialogKomoditasCarian.this.getActivity(), android.R.layout.simple_spinner_item, mStringListSpin);
        adapterspin.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinkomoditas.setAdapter(adapterspin);
        spinkomoditas.setOnItemSelectedListener(listenerspin);
        spinkomoditas.setSelection(posisipilih);

        mSeekBarRadius.setOnSeekBarChangeListener(listenerseekbar);
        mSeekBarRadius.setProgress(intradius);
        String teksradius = "" + intradius;
        teks_radiusketerangan.setText(teksradius);

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogKomoditasCarian.this.getActivity());
        builder.setView(mViewDialog);
        builder.setPositiveButton(R.string.teks_ok, listenerok);
        builder.setNegativeButton(R.string.teks_batal, listenergagal);

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return dialog;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(DialogKomoditasCarian.this);
    }


    DialogInterface.OnClickListener listenerok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            mMenuUtamaAkt.setPilihanDialogKomoditas(posisipilihanpilih, intradiuspilih);
            DialogKomoditasCarian.this.getDialog().dismiss();
        }
    };


    DialogInterface.OnClickListener listenergagal = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            DialogKomoditasCarian.this.getDialog().dismiss();
        }
    };


    SeekBar.OnSeekBarChangeListener listenerseekbar = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            intradiuspilih = progress;
            String teksradius = "" + intradiuspilih;
            teks_radiusketerangan.setText(teksradius);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };


    AdapterView.OnItemSelectedListener listenerspin = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            namakomoditaspilih = mStringListSpin.get(i);
            posisipilihanpilih = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };


}
