package pantauharga.gulajava.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.ButterKnife;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.aktivitas.LoginRegistersPengguna;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class FragmentRegisters extends Fragment {


    private TextInputLayout teksinput_namalengkap;
    private TextInputLayout teksinput_namapanggilan;
    private TextInputLayout teksinput_password;
    private TextInputLayout teksinput_konfirmasipassword;
    private TextInputLayout teksinput_email;
    private TextInputLayout teksinput_nomorktp;
    private TextInputLayout teksinput_nomorhandphone;
    private TextInputLayout teksinput_alamatlengkap;
    private TextInputLayout teksinput_kodepos;


    private EditText edit_namalengkap;
    private EditText edit_namapanggilan;
    private EditText edit_password;
    private EditText edit_konfirmasipassword;
    private EditText edit_email;
    private EditText edit_nomorktp;
    private EditText edit_nomorhandphone;
    private EditText edit_alamatlengkap;
    private EditText edit_kodepos;

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






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mLoginRegistersPenggunaAkt = (LoginRegistersPengguna) FragmentRegisters.this.getActivity();

        View viewfrag = inflater.inflate(R.layout.fragment_register, container, false);
        ButterKnife.bind(FragmentRegisters.this, viewfrag);



        return viewfrag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentRegisters.this);
    }















}
