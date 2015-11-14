package pantauharga.gulajava.android.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.LoginRegistersPengguna;
import pantauharga.gulajava.android.databases.RMLogin;

/**
 * Created by Gulajava Ministudio on 11/10/15.
 */
public class FragmentDataPengguna extends Fragment {

    @Bind(R.id.teks_namalengkap)
    TextView teks_namalengkap;
    @Bind(R.id.teks_namapanggilan)
    TextView teks_namapanggilan;
    @Bind(R.id.teks_email)
    TextView teks_email;
    @Bind(R.id.teks_nomorktp)
    TextView teks_nomorktp;
    @Bind(R.id.teks_nomorhp)
    TextView teks_nomorhp;
    @Bind(R.id.teks_alamatlengkap)
    TextView teks_alamat;
    @Bind(R.id.teks_kodepos)
    TextView teks_kodepos;

    private String str_namalengkap = "";
    private String str_namapanggilan = "";
    private String str_email = "";
    private String str_nomorktp = "";
    private String str_nomorhp = "";
    private String str_alamat = "";
    private String str_kodepos = "";

    private Realm mRealm;
    private RealmQuery<RMLogin> mLoginRealmQuery;
    private RealmResults<RMLogin> mRealmResultsLogin;

    @Bind(R.id.tombol_keluar)
    Button tombolkeluar;

    //keluar dari akun aplikasi
    private ProgressDialog mProgressDialog;

    private LoginRegistersPengguna mLoginRegistersPengguna;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mLoginRegistersPengguna = (LoginRegistersPengguna) FragmentDataPengguna.this.getActivity();

        View view = inflater.inflate(R.layout.fragment_datapengguna, container, false);
        ButterKnife.bind(FragmentDataPengguna.this, view);

        mRealm = Realm.getInstance(FragmentDataPengguna.this.getActivity());
        ambilDataRealms();

        tombolkeluar.setOnClickListener(listenertombolkeluar);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentDataPengguna.this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }


    //AMBIL DATA REALM
    private void ambilDataRealms() {

        mLoginRealmQuery = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mLoginRealmQuery.findAll();

        if (mRealmResultsLogin.size() > 0) {

            RMLogin rmLogin = mRealmResultsLogin.first();
            str_namalengkap = rmLogin.getNama();
            str_namapanggilan = rmLogin.getUsername();
            str_email = rmLogin.getEmail();
            str_nomorktp = rmLogin.getKtp();
            str_nomorhp = rmLogin.getNohp();
            str_alamat = rmLogin.getAlamat();
            str_kodepos = rmLogin.getKodepos();

            //setel ke tampilan
            setelTampilan();

        }
    }


    private void setelTampilan() {

        if (str_namalengkap.length() > 0) {
            teks_namalengkap.setText(str_namalengkap);
        } else {
            teks_namalengkap.setText("Nama tidak tersedia");
        }


        if (str_namapanggilan.length() > 0) {
            teks_namapanggilan.setText(str_namapanggilan);
        } else {
            teks_namapanggilan.setText("tidak tersedia");
        }


        if (str_email.length() > 1) {
            teks_email.setText(str_email);
        } else {
            teks_email.setText("-");
        }


        if (str_nomorktp != null && str_nomorktp.length() > 0) {
            teks_nomorktp.setText(str_nomorktp);
        } else {
            teks_nomorktp.setText("-");
        }


        if (str_alamat != null && str_alamat.length() > 1) {
            teks_alamat.setText(str_alamat);
        } else {
            teks_alamat.setText("-");
        }


        if (str_kodepos != null && str_kodepos.length() > 1) {
            teks_kodepos.setText(str_kodepos);
        } else {
            teks_kodepos.setText("-");
        }
    }


    //KELUAR AKUN
    private void keluarAkunPantauHarga() {

        tampilProgressDialog();

        //reset data ke data kosong
        mLoginRealmQuery = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mLoginRealmQuery.findAll();

        if (mRealmResultsLogin.size() > 0) {

            RMLogin rmLogin = mRealmResultsLogin.first();

            mRealm.beginTransaction();
            rmLogin.setUsername("");
            rmLogin.setNama("");
            rmLogin.setEmail("");
            rmLogin.setKtp("");
            rmLogin.setNohp("");
            rmLogin.setAlamat("");
            rmLogin.setKodepos("");
            mRealm.commitTransaction();

            //jeda dismiss dialog
            Handler handler = new Handler();
            handler.postDelayed(jedadismiss, 1000);

        }

    }


    //TAMPIL PROGRESS DIALOG
    private void tampilProgressDialog() {

        mProgressDialog = new ProgressDialog(FragmentDataPengguna.this.getActivity());
        mProgressDialog.setMessage("Sedang diproses...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }


    Runnable jedadismiss = new Runnable() {
        @Override
        public void run() {

            mProgressDialog.dismiss();

            //tampilkan dialog peringatan keluar
            mLoginRegistersPengguna.tampilDialogLogOut();
        }
    };


    View.OnClickListener listenertombolkeluar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            keluarAkunPantauHarga();
        }
    };


}
