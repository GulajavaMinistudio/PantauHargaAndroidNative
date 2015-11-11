package pantauharga.gulajava.android.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.adapters.RecyclerBantuanPetaLokasi;

/**
 * Created by Gulajava Ministudio on 9/20/15.
 */


public class DialogBantuanLokasi extends DialogFragment {

    private LayoutInflater inflater;
    private View view;


    private String[] strbantuan;
    @Bind(R.id.recycler_list)
    RecyclerView recyclerView;

    private RecyclerBantuanPetaLokasi recyclerBantuanPetaLokasi;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        strbantuan = DialogBantuanLokasi.this.getResources().getStringArray(R.array.array_bantuanlokasi);

        AlertDialog.Builder builder = new AlertDialog.Builder(DialogBantuanLokasi.this.getActivity());

        inflater = (LayoutInflater) DialogBantuanLokasi.this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.dialog_bantuan_lokasi, null);
        ButterKnife.bind(DialogBantuanLokasi.this, view);

        setelRecyclerViewMenu();

        builder.setView(view);
        builder.setPositiveButton(R.string.teks_ok, listenerdialogok);
        return builder.create();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(DialogBantuanLokasi.this);
    }


    private void setelRecyclerViewMenu() {

        recyclerView.setLayoutManager(new LinearLayoutManager(DialogBantuanLokasi.this.getActivity()));

        recyclerBantuanPetaLokasi = new RecyclerBantuanPetaLokasi(strbantuan, DialogBantuanLokasi.this.getActivity());

        recyclerView.setAdapter(recyclerBantuanPetaLokasi);
    }


    DialogInterface.OnClickListener listenerdialogok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

            DialogBantuanLokasi.this.dismiss();
        }
    };


}
