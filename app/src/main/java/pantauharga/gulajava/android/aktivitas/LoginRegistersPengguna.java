package pantauharga.gulajava.android.aktivitas;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.databases.RMLogin;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class LoginRegistersPengguna extends AppCompatActivity{


    private Toolbar toolbar;
    private ActionBar aksibar;



    private FragmentTransaction mFragmentTransaction;

    private Realm mRealm;
    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;

    private String str_namalengkap = "";
    private String str_namapanggilan = "";
    private String str_password = "";
    private String str_konfirmasipassword = "";
    private String str_email = "";
    private String str_nomorktp = "";
    private String str_nomorhandphone = "";
    private String str_alamatlengkap = "";
    private String str_kodepos = "";

    private boolean isLogin = false;

    private ProgressDialog mProgressDialog;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loginregister);
        ButterKnife.bind(LoginRegistersPengguna.this);



    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    //AMBIL DATA REALM LOGIN
    private void ambilDataRealmLogins() {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {

            isLogin = true;
            //tampilkan data pengguna

        }
        else {

            isLogin = false;

            //tampilkan halaman login/register

        }

    }




    //PINDAH HALAMAN FRAGMENT












}
