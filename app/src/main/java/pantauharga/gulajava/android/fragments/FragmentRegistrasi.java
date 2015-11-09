package pantauharga.gulajava.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.LoginRegistersPengguna;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class FragmentRegistrasi extends Fragment {


    @Bind(R.id.teksinputlay_editnamalengkap) TextInputLayout teksinput_namalengkap;
    @Bind(R.id.teksinputlay_editnamapanggil) TextInputLayout teksinput_namapanggilan;
    @Bind(R.id.teksinputlay_editpassword) TextInputLayout teksinput_password;
    @Bind(R.id.teksinputlay_editkonfirmasipass) TextInputLayout teksinput_konfirmasipassword;
    @Bind(R.id.teksinputlay_editemail) TextInputLayout teksinput_email;
    @Bind(R.id.teksinputlay_editktp) TextInputLayout teksinput_nomorktp;
    @Bind(R.id.teksinputlay_editnomorhp) TextInputLayout teksinput_nomorhandphone;
    @Bind(R.id.teksinputlay_editalamat) TextInputLayout teksinput_alamatlengkap;
    @Bind(R.id.teksinputlay_editkodepos) TextInputLayout teksinput_kodepos;


    @Bind(R.id.edit_namalengkap) EditText edit_namalengkap;
    @Bind(R.id.edit_namapanggilan) EditText edit_namapanggilan;
    @Bind(R.id.edit_password) EditText edit_password;
    @Bind(R.id.edit_konfirmasipass) EditText edit_konfirmasipassword;
    @Bind(R.id.edit_email) EditText edit_email;
    @Bind(R.id.edit_nomorktp) EditText edit_nomorktp;
    @Bind(R.id.edit_nomorhp) EditText edit_nomorhandphone;
    @Bind(R.id.edit_alamat) EditText edit_alamatlengkap;
    @Bind(R.id.edit_kodepos) EditText edit_kodepos;

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

    @Bind(R.id.tomboldaftar) Button tombolRegister;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mLoginRegistersPenggunaAkt = (LoginRegistersPengguna) FragmentRegistrasi.this.getActivity();

        View viewfrag = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(FragmentRegistrasi.this, viewfrag);



        return viewfrag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentRegistrasi.this);
    }




    //SEMBUNYIKAN KEYBOARD
    private static void sembunyikeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }










}
