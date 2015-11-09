package pantauharga.gulajava.android.aktivitas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import pantauharga.gulajava.android.Konstan;
import pantauharga.gulajava.android.R;
import pantauharga.gulajava.android.databases.RMLogin;
import pantauharga.gulajava.android.dialogs.DialogOkLogin;
import pantauharga.gulajava.android.fragments.FragmentLogin;
import pantauharga.gulajava.android.internets.Apis;
import pantauharga.gulajava.android.internets.JacksonRequest;
import pantauharga.gulajava.android.internets.Volleys;
import pantauharga.gulajava.android.modelgson.Logins;
import pantauharga.gulajava.android.modelgsonkirim.LoginKirim;
import pantauharga.gulajava.android.parsers.CekGPSNet;
import pantauharga.gulajava.android.parsers.Parseran;

/**
 * Created by Gulajava Ministudio on 11/9/15.
 */
public class LoginRegistersPengguna extends AppCompatActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    private ActionBar aksibar;


    private FragmentTransaction mFragmentTransaction;

    private Realm mRealm;
    private RealmQuery<RMLogin> mRealmQueryLogin;
    private RealmResults<RMLogin> mRealmResultsLogin;

    //login
    private String str_loginnamapanggilan = "";
    private String str_loginpassword = "";


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
    private ProgressDialog mProgressDialogFragment;

    private String strcek_username = "";
    private String strcek_nomorhp = "";

    private int kodepindah = Konstan.KODE_FRAGMENT_LOGINREGISTER;

    private Parseran mParseran;
    private CekGPSNet mCekGPSNet;
    private boolean isInternet = false;
    private boolean isProgresKirim = false;
    private boolean isAktJalan = false;

    private FragmentLogin mFragmentLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_loginregister);
        ButterKnife.bind(LoginRegistersPengguna.this);

        if (toolbar != null) {
            LoginRegistersPengguna.this.setSupportActionBar(toolbar);
        }

        isAktJalan = true;

        aksibar = LoginRegistersPengguna.this.getSupportActionBar();
        assert aksibar != null;
        aksibar.setDisplayHomeAsUpEnabled(true);
        aksibar.setTitle(R.string.login_register_datapengguna);

        mRealm = Realm.getInstance(LoginRegistersPengguna.this);
        mParseran = new Parseran(LoginRegistersPengguna.this);
        mCekGPSNet = new CekGPSNet(LoginRegistersPengguna.this);

        ambilDataRealmLogins();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        isAktJalan = false;
        mRealm.close();

        Volleys.getInstance(LoginRegistersPengguna.this).cancelPendingRequestsNoTag();
        Volleys.getInstance(LoginRegistersPengguna.this).clearVolleyCache();
    }


    //AMBIL DATA REALM LOGIN
    private void ambilDataRealmLogins() {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {

            //cek tampilkan data pengguna
            RMLogin rmLogin = mRealmResultsLogin.first();
            strcek_username = rmLogin.getUsername();
            strcek_nomorhp = rmLogin.getNohp();

            isLogin = strcek_username.length() > 2 && strcek_nomorhp.length() > 3;

        } else {

            isLogin = false;

            //db belum ada, diisi dulu
            RMLogin rmLogin = new RMLogin();
            rmLogin.setUsername("");
            rmLogin.setNama("");
            rmLogin.setEmail("");
            rmLogin.setKtp("");
            rmLogin.setNohp("");
            rmLogin.setAlamat("");
            rmLogin.setKodepos("");

            mRealm.beginTransaction();
            mRealm.copyToRealm(rmLogin);
            mRealm.commitTransaction();
        }

        if (isLogin) {

            //tampilkan fragment data pengguna

        } else {

            //tampilkan halaman login/register
            pindahFragments(Konstan.KODE_FRAGMENT_LOGINREGISTER);

        }
    }


    //PINDAH HALAMAN FRAGMENT
    public void pindahFragments(int kodepindahfragment) {

        kodepindah = kodepindahfragment;

        //muncul progress
        tampilProgressDialogFrag("Membuka halaman...");

        switch (kodepindah) {

            case Konstan.KODE_FRAGMENT_LOGINREGISTER:

                mFragmentLogin = new FragmentLogin();
                mFragmentTransaction = LoginRegistersPengguna.this.getSupportFragmentManager().beginTransaction();
                mFragmentTransaction.replace(R.id.frag_containers, mFragmentLogin);
                mFragmentTransaction.commit();

                break;

            case Konstan.KODE_FRAGMENT_REGISTER:


                break;

            case Konstan.KODE_FRAGMENT_DATAPENGGUNA:


                break;
        }

        Handler handler = new Handler();
        handler.postDelayed(jedadismisfrag, 950);
    }


    Runnable jedadismisfrag = new Runnable() {
        @Override
        public void run() {

            mProgressDialogFragment.dismiss();
        }
    };


    /**
     * ==============  LOGIN ======================
     **/

    //MENERIMA HASIL DARI FRAGMENT LOGIN
    public void terimaDataLogin(Bundle bundels) {

        isInternet = mCekGPSNet.cekStatsInternet();

        if (isInternet) {

            //ambil data
            str_loginnamapanggilan = bundels.getString(Konstan.TAG_INTENT_USERNAME);
            str_loginpassword = bundels.getString(Konstan.TAG_INTENT_PASSWORD);

            //kirim data ke volley
            parseDataLogin();

        } else {
            munculSnackbar(R.string.toastnointernet);
        }
    }


    //KIRIM DATA LOGIN PARSE JSON
    private void parseDataLogin() {

        //progress dialog
        isProgresKirim = true;
        tampilProgressDialog("Mengirim data...");


        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {

                LoginKirim loginKirim = new LoginKirim();
                loginKirim.setUsername(str_loginnamapanggilan);
                loginKirim.setPassword(str_loginpassword);

                return mParseran.konversiPojoJsonLogin(loginKirim);
            }
        })

                .continueWith(new Continuation<String, Object>() {
                    @Override
                    public Object then(Task<String> task) throws Exception {

                        String hasiljson = task.getResult();
                        Log.w("HASIL PARSE CEK", "HASIL PARSE JSON CEK " + hasiljson);

                        //kirim ke server
                        kirimJsonServerLogin(hasiljson);

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }


    //KIRIM DATA LOGIN VOLLEY
    private void kirimJsonServerLogin(String responbody) {

        String urls = Apis.getLinkLoginPengguna();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> parameters = new HashMap<>();

        JacksonRequest<Logins> jacksonRequest = Apis.postRequestLogin(
                urls,
                headers,
                parameters,
                responbody,
                new Response.Listener<Logins>() {
                    @Override
                    public void onResponse(Logins response) {

                        Log.w("SUKSES", "SUKSES");
                        if (isAktJalan) {
                            cekHasilLogin(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.w("GAGAL", "GAGAL");
                        if (isAktJalan) {
                            cekHasilLogin(null);
                        }
                    }
                }
        );

        Volleys.getInstance(LoginRegistersPengguna.this).addToRequestQueue(jacksonRequest);
    }


    //CEK HASIL LOGIN
    private void cekHasilLogin(Logins logins) {

        if (logins != null) {

            str_namalengkap = logins.getNama();
            str_namapanggilan = logins.getUsername();
            str_email = logins.getEmail();
            str_nomorktp = logins.getKtp();
            str_nomorhandphone = logins.getNohp();
            str_alamatlengkap = logins.getAlamat();
            str_kodepos = logins.getKodepos();

            if (str_namalengkap.length() > 0 && str_nomorhandphone.length() > 4) {

                //simpan ke database
                simpanDatabase(str_namapanggilan, str_namalengkap, str_email,
                        str_nomorktp, str_nomorhandphone, str_alamatlengkap,
                        str_kodepos);

            } else {
                munculSnackbar(R.string.gagalogin_masuk);
                isProgresKirim = false;
                mProgressDialog.dismiss();
            }
        } else {
            //gagal login
            munculSnackbar(R.string.gagalogin_masuk);
            isProgresKirim = false;
            mProgressDialog.dismiss();
        }
    }


    /** =====================================================**/


    /**
     * ========= SIMPAN DATABASE ===========
     **/

    private void simpanDatabase(String username, String namalengkap, String email,
                                String nomorktp, String nomorhp, String alamat,
                                String kodepos) {

        mRealmQueryLogin = mRealm.where(RMLogin.class);
        mRealmResultsLogin = mRealmQueryLogin.findAll();

        if (mRealmResultsLogin.size() > 0) {

            Log.w("HASIL DAFTAR LOG", "HASIL REGISTRASI ATAU LOGIN " + username + " " + nomorhp);

            //cek tampilkan data pengguna
//            RMLogin rmLogin = mRealmResultsLogin.first();
//
//            mRealm.beginTransaction();
//
//            rmLogin.setUsername(username);
//            rmLogin.setNama(namalengkap);
//            rmLogin.setEmail(email);
//            rmLogin.setKtp(nomorktp);
//            rmLogin.setNohp(nomorhp);
//            rmLogin.setAlamat(alamat);
//            rmLogin.setKodepos(kodepos);
//
//            mRealm.commitTransaction();


            isProgresKirim = false;
            mProgressDialog.dismiss();

            //beri peringatan dialog berhasil login
            tampilDialogOkLogin();

        }
    }


    //TAMPIL DIALOG OK LOGIN
    private void tampilDialogOkLogin() {

        FragmentTransaction fts = LoginRegistersPengguna.this.getSupportFragmentManager().beginTransaction();
        DialogOkLogin dialogOkLogin = new DialogOkLogin();
        dialogOkLogin.setCancelable(false);
        dialogOkLogin.show(fts, "dialog ok login");
    }


    //AKSI DIALOG OK LOGIN
    public void setOkLogin() {

        LoginRegistersPengguna.this.finish();

    }


    private void tampilProgressDialog(String pesan) {

        //progress dialogs
        mProgressDialog = new ProgressDialog(LoginRegistersPengguna.this);
        mProgressDialog.setMessage(pesan);
        mProgressDialog.setCancelable(true);
        mProgressDialog.show();
        mProgressDialog.setOnCancelListener(listenerbatals);

    }

    private void tampilProgressDialogFrag(String pesan) {

        //progress dialogs
        mProgressDialogFragment = new ProgressDialog(LoginRegistersPengguna.this);
        mProgressDialogFragment.setMessage(pesan);
        mProgressDialogFragment.setCancelable(false);
        mProgressDialogFragment.show();

    }

    ProgressDialog.OnCancelListener listenerbatals = new ProgressDialog.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialogInterface) {

            isProgresKirim = false;
            Volleys.getInstance(LoginRegistersPengguna.this).cancelPendingRequestsNoTag();
        }
    };


    //MUNCUL SNACKBAR
    private void munculSnackbar(int resPesan) {

        Snackbar.make(toolbar, resPesan, Snackbar.LENGTH_LONG).setAction("OK", listenersnackbar)
                .setActionTextColor(LoginRegistersPengguna.this.getResources().getColor(R.color.kuning_indikator)).show();
    }

    View.OnClickListener listenersnackbar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


}
