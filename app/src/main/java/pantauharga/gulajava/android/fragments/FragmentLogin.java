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

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class FragmentLogin extends Fragment {


    @Bind(R.id.teksinputlay_editnamapanggil)
    TextInputLayout teksinput_namapanggilan;
    @Bind(R.id.teksinputlay_editpassword)
    TextInputLayout teksinput_password;


    @Bind(R.id.edit_namapanggilan)
    EditText edit_namapanggilan;
    @Bind(R.id.edit_password)
    EditText edit_password;

    private String str_namapanggilan = "";
    private String str_password = "";


    private LoginRegistersPengguna mLoginRegistersPenggunaAkt;

    @Bind(R.id.tomboldaftar)
    Button tombolregister;
    @Bind(R.id.tombolmasuk)
    Button tombologin;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mLoginRegistersPenggunaAkt = (LoginRegistersPengguna) FragmentLogin.this.getActivity();

        View viewfrag = inflater.inflate(R.layout.fragment_logins, container, false);
        ButterKnife.bind(FragmentLogin.this, viewfrag);

        tombologin.setOnClickListener(listenertombol);
        tombolregister.setOnClickListener(listenertombol);

        return viewfrag;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(FragmentLogin.this);
    }


    private void ambilDataLogin() {

        boolean isValids = false;

        str_namapanggilan = edit_namapanggilan.getText().toString();
        str_password = edit_password.getText().toString();

        if (str_namapanggilan.length() < 3) {
            setelPeringatan(edit_namapanggilan, teksinput_namapanggilan, R.string.gagalogin_namapanggil);
        } else if (str_password.length() < 6) {
            setelPeringatan(edit_password, teksinput_password, R.string.gagalogin_password);
        } else {
            isValids = true;
        }

        if (isValids) {

            //kirim pesan ke aktivitas untuk kirim data
            Bundle bundle = new Bundle();
            bundle.putString(Konstan.TAG_INTENT_USERNAME, str_namapanggilan);
            bundle.putString(Konstan.TAG_INTENT_PASSWORD, str_password);

            mLoginRegistersPenggunaAkt.terimaDataLogin(bundle);

        }
    }


    //peringatkan data dan edit text
    private void setelPeringatan(EditText edits, TextInputLayout textInputLayout, int resId) {
        edits.requestFocus();
        textInputLayout.setError(FragmentLogin.this.getResources().getString(resId));

        Log.w("SET ERROR", "SET ERROR");
    }

    //RESET PERINGATAN
    private void resetPeringatan() {

        teksinput_namapanggilan.setError("");
        teksinput_password.setError("");
    }


    View.OnClickListener listenertombol = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            sembunyikeyboard(FragmentLogin.this.getActivity(), view);
            resetPeringatan();

            switch (view.getId()) {

                case R.id.tomboldaftar:

                    //pindah ke halaman daftar
                    mLoginRegistersPenggunaAkt.pindahFragments(Konstan.KODE_FRAGMENT_REGISTER);

                    break;

                case R.id.tombolmasuk:

                    ambilDataLogin();

                    break;
            }
        }
    };


    //SEMBUNYIKAN KEYBOARD
    private static void sembunyikeyboard(Context context, View view) {
        InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }


}
