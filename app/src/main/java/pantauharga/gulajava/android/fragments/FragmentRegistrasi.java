package pantauharga.gulajava.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.LoginRegistersPengguna;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class FragmentRegistrasi extends Fragment {


    @Bind(R.id.teksinputlay_editnamalengkap)
    TextInputLayout teksinput_namalengkap;
    @Bind(R.id.teksinputlay_editnamapanggil)
    TextInputLayout teksinput_namapanggilan;
    @Bind(R.id.teksinputlay_editpassword)
    TextInputLayout teksinput_password;
    @Bind(R.id.teksinputlay_editkonfirmasipass)
    TextInputLayout teksinput_konfirmasipassword;
    @Bind(R.id.teksinputlay_editktp)
    TextInputLayout teksinput_nomorktp;
    @Bind(R.id.teksinputlay_editnomorhp)
    TextInputLayout teksinput_nomorhandphone;
    @Bind(R.id.teksinputlay_editalamat)
    TextInputLayout teksinput_alamatlengkap;
    @Bind(R.id.teksinputlay_editkodepos)
    TextInputLayout teksinput_kodepos;


    @Bind(R.id.edit_namalengkap)
    EditText edit_namalengkap;
    @Bind(R.id.edit_namapanggilan)
    EditText edit_namapanggilan;
    @Bind(R.id.edit_password)
    EditText edit_password;
    @Bind(R.id.edit_konfirmasipass)
    EditText edit_konfirmasipassword;
    @Bind(R.id.edit_nomorktp)
    EditText edit_nomorktp;
    @Bind(R.id.edit_nomorhp)
    EditText edit_nomorhandphone;
    @Bind(R.id.edit_alamat)
    EditText edit_alamatlengkap;
    @Bind(R.id.edit_kodepos)
    EditText edit_kodepos;

    private String str_namalengkap = "";
    private String str_namapanggilan = "";
    private String str_password = "";
    private String str_konfirmasipassword = "";
    private String str_email = "";
    private String str_nomorktp = "";
    private String str_nomorhandphone = "";
    private String str_alamatlengkap = "";
    private String str_kodepos = "";


    private LoginRegistersPengguna mLoginRegistersPenggunaAkt;

    @Bind(R.id.tomboldaftar)
    Button tombolRegister;


    private Parseran mParseran;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mLoginRegistersPenggunaAkt = (LoginRegistersPengguna) FragmentRegistrasi.this.getActivity();

        View viewfrag = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(FragmentRegistrasi.this, viewfrag);

        mParseran = new Parseran(FragmentRegistrasi.this.getActivity());

        tombolRegister.setOnClickListener(listenertombolregister);


        return viewfrag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentRegistrasi.this);
    }


    //AMBIL DATA PENGGUNA
    private void ambilDataPengguna() {

        resetPeringatan();

        str_namalengkap = edit_namalengkap.getText().toString();
        str_namapanggilan = edit_namapanggilan.getText().toString();
        str_password = edit_password.getText().toString();
        str_konfirmasipassword = edit_konfirmasipassword.getText().toString();
        str_email = edit_namapanggilan.getText().toString();
        str_nomorktp = edit_nomorktp.getText().toString();
        str_nomorhandphone = edit_nomorhandphone.getText().toString();
        str_alamatlengkap = edit_alamatlengkap.getText().toString();
        str_kodepos = edit_kodepos.getText().toString();


        boolean isValidasiData = false;
        boolean isValidasiEmail = mParseran.validasiEmail(str_email);


        if (str_namalengkap.length() < 5) {
            setelPeringatan(edit_namalengkap, teksinput_namalengkap, R.string.regist_namalengkap);
        } else if (str_password.length() < 6) {
            setelPeringatan(edit_password, teksinput_password, R.string.regist_password);
        } else if (str_konfirmasipassword.length() < 6) {
            setelPeringatan(edit_konfirmasipassword, teksinput_konfirmasipassword, R.string.regist_password);
        } else if (!str_password.contentEquals(str_konfirmasipassword)) {
            setelPeringatan(edit_password, teksinput_password, R.string.regist_passgasama);
        } else if (!isValidasiEmail) {
            setelPeringatan(edit_namapanggilan, teksinput_namapanggilan, R.string.regist_email);
        } else if (str_nomorhandphone.length() < 10) {
            setelPeringatan(edit_nomorhandphone, teksinput_nomorhandphone, R.string.regist_nomorhp);
        } else if (str_alamatlengkap.length() < 5) {
            setelPeringatan(edit_alamatlengkap, teksinput_alamatlengkap, R.string.regist_alamat);
        } else {
            isValidasiData = true;
        }

        if (isValidasiData) {

            //kirim data ke fragment utama
            Bundle bundle = new Bundle();
            bundle.putString(Konstan.TAG_INTENT_NAMALENGKAP, str_namalengkap);
            bundle.putString(Konstan.TAG_INTENT_USERNAME, str_namapanggilan);
            bundle.putString(Konstan.TAG_INTENT_PASSWORD, str_password);
            bundle.putString(Konstan.TAG_INTENT_EMAIL, str_email);
            bundle.putString(Konstan.TAG_INTENT_NOMORKTP, str_nomorktp);
            bundle.putString(Konstan.TAG_INTENT_NOMORHP, str_nomorhandphone);
            bundle.putString(Konstan.TAG_INTENT_ALAMATLENGKAP, str_alamatlengkap);
            bundle.putString(Konstan.TAG_INTENT_KODEPOS, str_kodepos);

            //kirim ke aktivitas utama
            mLoginRegistersPenggunaAkt.terimaDataRegistrasi(bundle);

        }
    }


    //peringatkan data dan edit text
    private void setelPeringatan(EditText edits, TextInputLayout textInputLayout, int resId) {
        edits.requestFocus();
        textInputLayout.setError(FragmentRegistrasi.this.getResources().getString(resId));

        Log.w("SET ERROR", "SET ERROR");
    }

    //RESET PERINGATAN
    private void resetPeringatan() {

        teksinput_namalengkap.setError("");
        teksinput_namapanggilan.setError("");
        teksinput_password.setError("");
        teksinput_konfirmasipassword.setError("");
        teksinput_nomorktp.setError("");
        teksinput_nomorhandphone.setError("");
        teksinput_alamatlengkap.setError("");
        teksinput_kodepos.setError("");

    }

    View.OnClickListener listenertombolregister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            sembunyikeyboard(FragmentRegistrasi.this.getActivity(), view);

            ambilDataPengguna();
        }
    };


    //SEMBUNYIKAN KEYBOARD
    private static void sembunyikeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


}
