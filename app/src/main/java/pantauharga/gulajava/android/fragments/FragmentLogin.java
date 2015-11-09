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
public class FragmentLogin extends Fragment {


    private TextInputLayout teksinput_namapanggilan;
    private TextInputLayout teksinput_password;


    private EditText edit_namapanggilan;
    private EditText edit_password;

    private String str_namapanggilan = "";
    private String str_password = "";


    private LoginRegistersPengguna mLoginRegistersPenggunaAkt;






    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mLoginRegistersPenggunaAkt = (LoginRegistersPengguna) FragmentLogin.this.getActivity();

        View viewfrag = inflater.inflate(R.layout.fragment_logins, container, false);
        ButterKnife.bind(FragmentLogin.this, viewfrag);



        return viewfrag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentLogin.this);
    }















}
